package com.ling.noto.presentation.event

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.ling.noto.data.local.entity.NoteEntity
import com.ling.noto.presentation.component.dialog.ExportType

sealed interface DatabaseEvent {

    data class RemoveUselessFiles(val context: Context) : DatabaseEvent

    data class ImportFiles(
        val context: Context,
        val folderId: Long?,
        val uriList: List<Uri>
    ) : DatabaseEvent

    data class ExportFiles(
        val context: Context,
        val notes: List<NoteEntity>,
        val type: ExportType
    ) : DatabaseEvent

    data class Backup(val context: Context) : DatabaseEvent
    data class Recovery(val contentResolver: ContentResolver, val uri: Uri) : DatabaseEvent
    data object Reset : DatabaseEvent

    data class ImportImages(
        val context: Context,
        val uriList: List<Uri>
    ) : DatabaseEvent

    data class ImportVideo(
        val context: Context,
        val uri: Uri
    ) : DatabaseEvent

    /** WebDAV 测试连接：验证配置是否正确 */
    data class WebDavTest(
        val url: String,
        val username: String,
        val password: String
    ) : DatabaseEvent

    /** WebDAV 双向同步：先拉取合并，再上传 */
    data class WebDavSync(
        val url: String,
        val username: String,
        val password: String
    ) : DatabaseEvent
}