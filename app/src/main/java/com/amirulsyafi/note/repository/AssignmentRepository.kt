package com.amirulsyafi.note.repository

import com.amirulsyafi.note.api.CoreApi
import com.amirulsyafi.note.data.assignment.Assignment
import com.amirulsyafi.note.data.assignment.AssignmentDao
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import retrofit2.Retrofit
import javax.inject.Inject


class AssignmentRepository @Inject constructor(
    private val assignmentDao: AssignmentDao,
    retrofit: Retrofit
) {
    private val coreApi: CoreApi = retrofit.create(CoreApi::class.java)

    suspend fun getAssignmentApi(int: Int) {
        val assignments = coreApi.getAssignments(int)
        assignmentDao.deleteAll()
        assignmentDao.insertAll(assignments)
    }

    fun getAssignmentsFlow(): Flow<List<Assignment>>  = assignmentDao.getAssignmentsFlow()
}