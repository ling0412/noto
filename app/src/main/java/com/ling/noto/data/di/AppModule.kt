package com.ling.noto.data.di

import android.content.Context
import androidx.room.Room
import com.ling.noto.data.local.Database
import com.ling.noto.presentation.util.WebDavAutoSync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import com.ling.noto.data.local.MIGRATION_1_2
import com.ling.noto.data.repository.AppDataStoreRepositoryImpl
import com.ling.noto.data.repository.FolderRepositoryImpl
import com.ling.noto.data.repository.NoteRepositoryImpl
import com.ling.noto.data.repository.WidgetDataStoreRepositoryImpl
import com.ling.noto.domain.usecase.AddFolder
import com.ling.noto.domain.repository.NoteRepository
import com.ling.noto.domain.usecase.AddNote
import com.ling.noto.domain.usecase.DeleteFolder
import com.ling.noto.domain.usecase.DeleteNote
import com.ling.noto.domain.usecase.DeleteNotesByFolderId
import com.ling.noto.domain.usecase.GetFolders
import com.ling.noto.domain.usecase.GetNotes
import com.ling.noto.domain.usecase.UseCases
import com.ling.noto.domain.usecase.SearchNotes
import com.ling.noto.domain.usecase.UpdateFolder
import com.ling.noto.domain.usecase.UpdateNote
import com.ling.noto.domain.repository.AppDataStoreRepository
import com.ling.noto.domain.repository.FolderRepository
import com.ling.noto.domain.repository.WidgetDataStoreRepository
import com.ling.noto.domain.usecase.GetNoteById
import com.ling.noto.domain.usecase.GetNotesCountByFolderId
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAppDataStoreRepository(@ApplicationContext context: Context): AppDataStoreRepository =
        AppDataStoreRepositoryImpl(context)

    @Singleton
    @Provides
    fun provideWidgetDataStoreRepository(@ApplicationContext context: Context): WidgetDataStoreRepository =
        WidgetDataStoreRepositoryImpl(context)

    @Provides
    @Singleton
    fun provideNoteDatabase(@ApplicationContext context: Context): Database =
        Room.databaseBuilder(
            context,
            Database::class.java,
            Database.NAME
        ).addMigrations(MIGRATION_1_2).build()

    @Provides
    @Singleton
    fun provideNoteRepository(database: Database): NoteRepository =
        NoteRepositoryImpl(dao = database.noteDao)

    @Provides
    @Singleton
    fun provideFolderRepository(database: Database): FolderRepository =
        FolderRepositoryImpl(dao = database.folderDao)

    @Provides
    @Singleton
    fun provideNoteUseCases(
        noteRepository: NoteRepository,
        folderRepository: FolderRepository
    ): UseCases = UseCases(
        getNotes = GetNotes(noteRepository),
        getNoteById = GetNoteById(noteRepository),
        deleteNote = DeleteNote(noteRepository),
        addNote = AddNote(noteRepository),
        searchNotes = SearchNotes(noteRepository),
        updateNote = UpdateNote(noteRepository),
        deleteNotesByFolderId = DeleteNotesByFolderId(noteRepository),
        addFolder = AddFolder(folderRepository),
        updateFolder = UpdateFolder(folderRepository),
        deleteFolder = DeleteFolder(folderRepository),
        getFolders = GetFolders(folderRepository),
        getNotesCountByFolderId = GetNotesCountByFolderId(noteRepository)
    )

    @Singleton
    @Provides
    fun provideApplicationScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Singleton
    @Provides
    fun provideWebDavAutoSync(
        appDataStoreRepository: AppDataStoreRepository,
        useCases: UseCases,
        scope: CoroutineScope
    ): WebDavAutoSync = WebDavAutoSync(
        appDataStoreRepository = appDataStoreRepository,
        useCases = useCases,
        scope = scope
    )
}
