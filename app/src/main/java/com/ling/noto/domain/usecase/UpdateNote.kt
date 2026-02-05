package com.ling.noto.domain.usecase

import com.ling.noto.data.local.entity.NoteEntity
import com.ling.noto.domain.repository.NoteRepository

class UpdateNote(
    private val repository: NoteRepository
) {

    suspend operator fun invoke(note: NoteEntity) {
        repository.updateNote(note)
    }
}