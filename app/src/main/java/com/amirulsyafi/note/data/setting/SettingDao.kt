package com.amirulsyafi.note.data.setting

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.amirulsyafi.note.data.note.Note
import com.amirulsyafi.note.data.note.Priority

@Dao
interface SettingDao {

    @Query("update setting set text = :value where `key` = :key")
    suspend fun updatePrioritySetting(key: SettingKey, value: Priority?)

    @Query("SELECT text FROM Setting where `key` = :key")
    suspend fun getPrioritySetting(key: SettingKey): Priority?

    @Query("update setting set integer = :value where `key` = :key")
    suspend fun updateBoolSetting(key: SettingKey, value: Boolean?)

    @Query("SELECT integer FROM Setting where `key` = :key")
    suspend fun getBoolSetting(key: SettingKey): Boolean?
}