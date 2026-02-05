package com.ling.noto.presentation.event

import com.ling.noto.data.local.entity.FolderEntity

sealed interface FolderEvent {

    data class AddFolder(val folder: FolderEntity) : FolderEvent

    data class UpdateFolder(val folder: FolderEntity) : FolderEvent

    data class DeleteFolder(val folder: FolderEntity) : FolderEvent
}