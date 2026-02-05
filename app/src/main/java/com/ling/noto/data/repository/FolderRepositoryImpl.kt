package com.ling.noto.data.repository

import com.ling.noto.data.local.dao.FolderDao
import com.ling.noto.data.local.entity.FolderEntity
import com.ling.noto.domain.repository.FolderRepository
import kotlinx.coroutines.flow.Flow

class FolderRepositoryImpl(
    private val dao: FolderDao
) : FolderRepository {

    override fun getAllFolders(): Flow<List<FolderEntity>> {
        return dao.getAllFolders()
    }

    override suspend fun insertFolder(folderEntity: FolderEntity) {
        dao.insertFolder(folderEntity)
    }

    override suspend fun deleteFolder(folderEntity: FolderEntity) {
        dao.deleteFolder(folderEntity)
    }

    override suspend fun updateFolder(folderEntity: FolderEntity) {
        dao.updateFolder(folderEntity)
    }

}