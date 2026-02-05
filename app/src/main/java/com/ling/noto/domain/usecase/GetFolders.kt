package com.ling.noto.domain.usecase

import com.ling.noto.data.local.entity.FolderEntity
import com.ling.noto.domain.repository.FolderRepository
import kotlinx.coroutines.flow.Flow

class GetFolders(
    private val repository: FolderRepository
) {

    operator fun invoke(): Flow<List<FolderEntity>> =
        repository.getAllFolders()
}
