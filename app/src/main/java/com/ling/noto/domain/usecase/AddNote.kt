package com.ling.noto.domain.usecase

import com.ling.noto.data.local.entity.NoteEntity
import com.ling.noto.domain.repository.NoteRepository

class AddNote(
    private val repository: NoteRepository
) {

    suspend operator fun invoke(note: NoteEntity): Long {
        return repository.insertNote(note)
    }
}