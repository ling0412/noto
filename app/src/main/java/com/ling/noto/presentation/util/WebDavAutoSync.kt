package com.ling.noto.presentation.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.ling.noto.domain.repository.AppDataStoreRepository
import com.ling.noto.domain.usecase.UseCases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * WebDAV 自动同步：修改完成后防抖触发，或应用进入后台时立即同步
 */
class WebDavAutoSync(
    private val appDataStoreRepository: AppDataStoreRepository,
    private val useCases: UseCases,
    private val scope: CoroutineScope
) {
    private var debounceJob: Job? = null
    private val debounceDelayMs = 3000L

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_STOP) {
                    debounceJob?.cancel()
                    debounceJob = null
                    scope.launch { trySync() }
                }
            }
        )
    }

    /**
     * 在数据修改后调用，防抖 3 秒后执行同步
     */
    fun scheduleSync() {
        if (!shouldSync()) return
        debounceJob?.cancel()
        debounceJob = scope.launch {
            delay(debounceDelayMs)
            debounceJob = null
            trySync()
        }
    }

    private fun shouldSync(): Boolean {
        if (!appDataStoreRepository.getBooleanValue(Constants.Preferences.WEBDAV_AUTO_SYNC_ENABLED, false)) return false
        val url = appDataStoreRepository.getStringValue(Constants.Preferences.WEBDAV_URL, "")
        val username = appDataStoreRepository.getStringValue(Constants.Preferences.WEBDAV_USERNAME, "")
        val password = appDataStoreRepository.getStringValue(Constants.Preferences.WEBDAV_PASSWORD, "")
        return url.isNotBlank() && username.isNotBlank() && password.isNotBlank()
    }

    private suspend fun trySync() {
        if (!shouldSync()) return
        val url = appDataStoreRepository.getStringValue(Constants.Preferences.WEBDAV_URL, "").trimEnd('/')
        val username = appDataStoreRepository.getStringValue(Constants.Preferences.WEBDAV_USERNAME, "")
        val password = appDataStoreRepository.getStringValue(Constants.Preferences.WEBDAV_PASSWORD, "")
        val client = WebDavClient(baseUrl = url, username1 = username, password1 = password)
        try {
            WebDavSync.sync(client, useCases).getOrElse { /* 静默失败，不打扰用户 */ }
        } finally {
            client.close()
        }
    }
}
