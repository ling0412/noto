package com.ling.noto.data.local.entity

import kotlinx.serialization.Serializable

@Serializable
data class BackupData(
    val notes: List<NoteEntity>,
    val folders: List<FolderEntity>
)