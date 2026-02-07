package com.ling.noto.presentation.util

import com.ling.noto.data.local.entity.BackupData
import com.ling.noto.data.local.entity.FolderEntity
import com.ling.noto.data.local.entity.NoteEntity
import com.ling.noto.domain.usecase.UseCases
import com.ling.noto.presentation.util.decryptBackupDataWithCompatibility
import com.ling.noto.presentation.util.encryptBackupData
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

/**
 * WebDAV 同步逻辑：合并策略为时间戳新的优先
 */
object WebDavSync {

    /**
     * 上传本地数据到 WebDAV（覆盖远程）
     */
    suspend fun push(
        client: WebDavClient,
        useCases: UseCases
    ): Result<Unit> {
        return runCatching {
            val notes = useCases.getNotes().first()
            val folders = useCases.getFolders().first()
            val backupData = BackupData(notes, folders)
            val json = Json.encodeToString(backupData)
            val encrypted = encryptBackupData(json)
            val content = encrypted.toByteArray(Charsets.UTF_8)

            client.uploadFile(Constants.WebDav.SYNC_FILE_NAME, content)
                .getOrThrow()
        }
    }

    /**
     * 从 WebDAV 下载并合并到本地
     * 合并策略：笔记按 timestamp 取新的；文件夹按 id 合并（远程覆盖）
     */
    suspend fun pull(
        client: WebDavClient,
        useCases: UseCases
    ): Result<Unit> {
        return runCatching {
            val downloadResult = client.downloadFile(Constants.WebDav.SYNC_FILE_NAME)
            val remoteJson = downloadResult.getOrElse { error ->
                // 404 表示远程尚无同步文件，视为首次同步，直接返回成功
                if (error.message?.contains("404") == true) return@runCatching
                throw error
            }

            val decrypted = decryptBackupDataWithCompatibility(
                String(remoteJson, Charsets.UTF_8)
            )
            val remoteData = Json.decodeFromString<BackupData>(decrypted)

            val localNotes = useCases.getNotes().first()
            val localFolders = useCases.getFolders().first()

            val mergedFolders = mergeFolders(localFolders, remoteData.folders)
            val mergedNotes = mergeNotes(localNotes, remoteData.notes)

            mergedFolders.forEach { useCases.addFolder(it) }
            mergedNotes.forEach { useCases.addNote(it) }
        }
    }

    /**
     * 双向同步：先拉取合并，再上传
     */
    suspend fun sync(
        client: WebDavClient,
        useCases: UseCases
    ): Result<Unit> {
        pull(client, useCases).getOrElse { return Result.failure(it) }
        return push(client, useCases)
    }

    private fun mergeFolders(
        local: List<FolderEntity>,
        remote: List<FolderEntity>
    ): List<FolderEntity> {
        val map = local.filter { it.id != null }.associateBy { it.id!! }.toMutableMap()
        remote.filter { it.id != null }.forEach { folder ->
            map[folder.id!!] = folder
        }
        return map.values.toList()
    }

    private fun mergeNotes(
        local: List<NoteEntity>,
        remote: List<NoteEntity>
    ): List<NoteEntity> {
        val map = local.filter { it.id != null }.associateBy { it.id!! }.toMutableMap()
        remote.filter { it.id != null }.forEach { note ->
            val id = note.id!!
            val existing = map[id]
            when {
                existing == null -> map[id] = note
                note.timestamp > existing.timestamp -> map[id] = note
                else -> { /* 保留 existing（本地更新） */ }
            }
        }
        return map.values.toList()
    }
}
