package com.ling.noto.domain.usecase

import com.ling.noto.data.local.entity.NoteEntity
import com.ling.noto.domain.repository.NoteRepository

class GetNoteById(
    private val repository: NoteRepository
) {

    suspend operator fun invoke(id: Long): NoteEntity? {
        return repository.getNoteById(id)
    }
}