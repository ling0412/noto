package com.ling.noto.domain.usecase

import com.ling.noto.data.local.entity.NoteEntity
import com.ling.noto.domain.repository.NoteRepository

class DeleteNote(
    private val repository: NoteRepository
) {

    suspend operator fun invoke(noteEntity: NoteEntity) {
        repository.deleteNote(noteEntity)
    }
}