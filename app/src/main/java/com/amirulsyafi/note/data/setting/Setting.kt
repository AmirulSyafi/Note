package com.amirulsyafi.note.data.setting

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Setting(
    @PrimaryKey val key: SettingKey,
    val text: String?,
    val integer: Long?
)