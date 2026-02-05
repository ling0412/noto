package com.ling.noto.presentation.event

import android.content.Context
import android.net.Uri
import com.ling.noto.data.local.entity.NoteEntity
import com.ling.noto.presentation.component.dialog.ExportType

sealed interface FileDataEvent {

    data class ExportFiles(
        val context: Context,
        val notes: List<NoteEntity>,
        val type: ExportType
    ) : FileDataEvent

    data class ImportImages(
        val context: Context,
        val uriList: List<Uri>
    ) : FileDataEvent

    data class ImportVideo(
        val context: Context,
        val uri: Uri
    ) : FileDataEvent
}