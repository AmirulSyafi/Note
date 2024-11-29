package com.amirulsyafi.note.data.note

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.amirulsyafi.note.data.setting.SettingKey
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM note ORDER BY id DESC")
    fun getAllNotesLiveData(): LiveData<List<Note>>

    @Query("SELECT * FROM note WHERE title LIKE :query or description LIKE :query")
    fun searchNotesLiveData(query: String?): LiveData<List<Note>>

    @Query(
        """
    SELECT * FROM note
    WHERE (title LIKE :query OR description LIKE :query)
    AND date BETWEEN :startTime AND :endTime
    AND priority IN (:priorities)
    AND status IN (:statuses)
    ORDER BY priority
    """
    )
    fun searchNotesFlow(
        query: String?,
        startTime: Long,
        endTime: Long,
        priorities: List<Priority>,
        statuses: List<Boolean>
    ): Flow<List<Note>>


}