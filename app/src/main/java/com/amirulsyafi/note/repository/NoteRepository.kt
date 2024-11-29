package com.amirulsyafi.note.repository

import com.amirulsyafi.note.data.note.Note
import com.amirulsyafi.note.data.note.NoteDao
import com.amirulsyafi.note.data.note.Priority
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteDao: NoteDao
) {
    suspend fun insertNote(note: Note) = noteDao.insertNote(note)
    suspend fun updateNote(note: Note) = noteDao.updateNote(note)
    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)
    fun getAllNotesLiveData() = noteDao.getAllNotesLiveData()
    fun searchNotesLiveData(query: String?) = noteDao.searchNotesLiveData(query)
    fun searchNotesFlow(
        query: String?,
        startTime: Long,
        endTime: Long,
        priority: Priority?,
        done: Boolean?
    ): Flow<List<Note>> {
        // Handle null values for priority and done
        val priorities = when (priority) {
            null -> listOf(Priority.LOW, Priority.MEDIUM, Priority.HIGH, Priority.URGENT)
            else -> listOf(priority)
        }

        val statuses = when (done) {
            null -> listOf(false, true) // Allow all values of done if it's null
            else -> listOf(done)
        }

        // Pass the lists to the DAO query
        return noteDao.searchNotesFlow(query, startTime, endTime, priorities, statuses)
    }
}