package com.ncautomation.notes.pro.interfaces

import androidx.room.*
import com.ncautomation.notes.pro.models.Note

@Dao
interface NotesDao {
    @Query("SELECT * FROM notes ORDER BY title COLLATE UNICODE ASC ")
    fun getNotes(): List<Note>

    @Query("SELECT * FROM notes WHERE id = :id")
    fun getNoteWithId(id: Long): Note?

    @Query("SELECT id FROM notes WHERE path = :path")
    fun getNoteIdWithPath(path: String): Long?

    @Query("SELECT id FROM notes WHERE title = :title COLLATE NOCASE")
    fun getNoteIdWithTitle(title: String): Long?

    @Query("SELECT id FROM notes WHERE title = :title")
    fun getNoteIdWithTitleCaseSensitive(title: String): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(note: Note): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(notes: List<Note>): List<Long>

    @Delete
    fun deleteNote(note: Note)
}
