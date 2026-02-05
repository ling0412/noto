package com.ling.noto.domain.usecase

import com.ling.noto.data.local.entity.NoteEntity
import com.ling.noto.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class SearchNotes(
    private val repository: NoteRepository
) {

    operator fun invoke(keyWord: String): Flow<List<NoteEntity>> =
        repository.getNotesByKeyWord(keyWord)
}
