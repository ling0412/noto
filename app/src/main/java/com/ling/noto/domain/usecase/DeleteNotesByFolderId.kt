package com.ling.noto.domain.usecase

import com.ling.noto.domain.repository.NoteRepository

class DeleteNotesByFolderId(
    private val repository: NoteRepository
) {

    suspend operator fun invoke(folderId: Long?) {
        repository.deleteNotesByFolderId(folderId)
    }
}