package com.amirulsyafi.note.data.assignment

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AssignmentDao {
    @Insert
    suspend fun insertAll(assignments: List<Assignment>)

    @Query("delete from Assignment")
    suspend fun deleteAll()

    @Query("select * from Assignment")
    fun getAssignmentsFlow(): Flow<List<Assignment>>
}