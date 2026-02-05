package com.ling.noto.data.repository

import com.ling.noto.data.local.dao.NoteDao
import com.ling.noto.data.local.entity.NoteEntity
import com.ling.noto.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class NoteRepositoryImpl(
    private val dao: NoteDao
) : NoteRepository {

    override fun getAllNotes(): Flow<List<NoteEntity>> {
        return dao.getAllNotes()
    }

    override fun getAllDeletedNotes(): Flow<List<NoteEntity>> {
        return dao.getAllDeletedNotes()
    }

    override fun getNotesByFolderId(folderId: Long?): Flow<List<NoteEntity>> {
        return dao.getNotesByFolderId(folderId)
    }

    override suspend fun getNoteById(id: Long): NoteEntity? {
        return dao.getNoteById(id)
    }

    override suspend fun insertNote(note: NoteEntity): Long {
        return dao.insertNote(note)
    }

    override suspend fun deleteNote(noteEntity: NoteEntity) {
        dao.deleteNote(noteEntity)
    }

    override suspend fun deleteNotesByFolderId(folderId: Long?) {
        dao.deleteNotesByFolderId(folderId)
    }

    override suspend fun updateNote(note: NoteEntity) {
        dao.updateNote(note)
    }

    override fun getNotesByKeyWord(keyWord: String): Flow<List<NoteEntity>> {
        return dao.getNotesByKeyWord(keyWord)
    }
}