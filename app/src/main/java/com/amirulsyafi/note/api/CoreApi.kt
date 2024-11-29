package com.amirulsyafi.note.api

import com.amirulsyafi.note.data.assignment.Assignment
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CoreApi {
    @GET("GetServerDateTime.ashx")
    suspend fun getServerDateTime(): String

    @GET("ReloadAssignMOOp.ashx")
    suspend fun getAssignments(
        @Query("gabletid") gabletId: Int
    ): List<Assignment>
}