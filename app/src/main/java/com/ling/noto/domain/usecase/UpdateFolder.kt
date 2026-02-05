package com.ling.noto.domain.usecase

import com.ling.noto.data.local.entity.FolderEntity
import com.ling.noto.domain.repository.FolderRepository

class UpdateFolder(
    private val repository: FolderRepository
) {

    suspend operator fun invoke(folderEntity: FolderEntity) {
        repository.updateFolder(folderEntity)
    }
}