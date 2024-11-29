package com.amirulsyafi.note.repository

import com.amirulsyafi.note.api.CoreApi
import com.amirulsyafi.note.data.note.Priority
import com.amirulsyafi.note.data.setting.SettingDao
import com.amirulsyafi.note.data.setting.SettingKey
import retrofit2.Retrofit
import javax.inject.Inject


class SettingRepository @Inject constructor(
    private val settingDao: SettingDao,
    retrofit: Retrofit
) {
    private val coreApi: CoreApi = retrofit.create(CoreApi::class.java)

    suspend fun getServerDatetimeApi() = coreApi.getServerDateTime()

    suspend fun updatePrioritySetting(key: SettingKey, value: Priority?) =
        settingDao.updatePrioritySetting(key, value)

    suspend fun getPrioritySetting(key: SettingKey) = settingDao.getPrioritySetting(key)

    suspend fun updateBoolSetting(key: SettingKey, value: Boolean?) =
        settingDao.updateBoolSetting(key, value)

    suspend fun getBoolSetting(key: SettingKey) = settingDao.getBoolSetting(key)
}