package com.ling.noto.presentation.util

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.statement.readRawBytes
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLDecoder

class WebDavClient(
    private val baseUrl: String,
    private val username1: String,
    private val password1: String
) {
    private val normalizedBaseUrl: String = baseUrl.trimEnd('/')
    // 使用 OkHttp 引擎：支持 PROPFIND、MKCOL 等非标准方法，且 TLS 兼容性优于 CIO
    private val client = HttpClient(OkHttp) {
        install(Logging) {
            level = LogLevel.HEADERS
        }
        install(Auth) {
            basic {
                credentials {
                    BasicAuthCredentials(username1, password1)
                }
                realm = "WebDAV"
                // 预发送认证：部分服务器（如 Nextcloud）不会对 OPTIONS 返回 401，
                // 导致默认的「等 401 再发凭据」流程无法触发，需主动携带凭据
                sendWithoutRequest { true }
            }
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 15000
            socketTimeoutMillis = 15000
        }
    }

    private fun buildUrl(path: String): String {
        val trimmedPath = path.trimStart('/')
        return if (trimmedPath.isEmpty()) {
            normalizedBaseUrl
        } else {
            "$normalizedBaseUrl/$trimmedPath"
        }
    }

    /**
     * Tests connection to the WebDAV server
     * @return Result with success/failure information
     */
    suspend fun testConnection(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val response = client.request(normalizedBaseUrl) {
                method = HttpMethod.Options
            }

            return@withContext if (response.status.isSuccess()) {
                Result.success(true)
            } else {
                Result.failure(Exception("Connection failed with status: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Lists files and directories at a given path.
     * Parses PROPFIND XML response - supports d:href, D:href and other namespace prefixes
     * used by different WebDAV servers (Nextcloud, ownCloud, Apache, etc.)
     */
    suspend fun listDirectory(path: String): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val url = buildUrl(path)
            val response = client.request(url) {
                method = HttpMethod("PROPFIND")
                headers {
                    append(HttpHeaders.Depth, "1")
                }
            }

            if (response.status.isSuccess()) {
                val responseBody = response.bodyAsText()
                // Match href elements with various namespace prefixes (d:, D:, nc:, oc:, or none)
                val hrefPattern = Regex("""<(?:[^:>]+:)?href>([^<]+)</(?:[^:>]+:)?href>""", RegexOption.IGNORE_CASE)
                val allHrefs = hrefPattern.findAll(responseBody)
                    .map { it.groupValues[1].trim() }
                    .map { href ->
                        try {
                            URLDecoder.decode(href, Charsets.UTF_8.name())
                        } catch (_: Exception) {
                            href
                        }
                    }
                    .filter { it.isNotEmpty() }
                    .toList()

                // First href is the requested collection itself, rest are its contents
                val contents = allHrefs.drop(1)

                Result.success(contents)
            } else {
                Result.failure(Exception("Failed to list directory: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Uploads a file to the WebDAV server
     */
    suspend fun uploadFile(remotePath: String, content: ByteArray): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val response = client.put(buildUrl(remotePath)) {
                    setBody(content)
                }

                if (response.status.isSuccess()) {
                    Result.success(true)
                } else {
                    Result.failure(Exception("Failed to upload file: ${response.status}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Downloads a file from the WebDAV server
     */
    suspend fun downloadFile(remotePath: String): Result<ByteArray> = withContext(Dispatchers.IO) {
        try {
            val response = client.get(buildUrl(remotePath))

            if (response.status.isSuccess()) {
                Result.success(response.readRawBytes())
            } else {
                Result.failure(Exception("Failed to download file: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Creates a directory on the WebDAV server
     */
    suspend fun createDirectory(remotePath: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val response = client.request(buildUrl(remotePath)) {
                method = HttpMethod("MKCOL")
            }

            if (response.status.isSuccess()) {
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to create directory: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun close() {
        client.close()
    }
}
