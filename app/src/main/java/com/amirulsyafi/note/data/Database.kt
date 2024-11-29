package com.amirulsyafi.note.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.amirulsyafi.note.data.assignment.Assignment
import com.amirulsyafi.note.data.assignment.AssignmentDao
import com.amirulsyafi.note.data.note.Note
import com.amirulsyafi.note.data.note.NoteDao
import com.amirulsyafi.note.data.setting.Setting
import com.amirulsyafi.note.data.setting.SettingDao

@Database(
    entities = [Note::class, Setting::class, Assignment::class],
    version = 2,
    exportSchema = false
)
abstract class Database : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun settingDao(): SettingDao
    abstract fun assignmentDao(): AssignmentDao

    companion object {
        const val DATABASE_NAME = "Note"

        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS Assignment (
                        slotNo INTEGER PRIMARY KEY NOT NULL,
                        assignmentName TEXT NOT NULL,
                        mo TEXT NOT NULL,
                        op TEXT NOT NULL,
                        smv TEXT NOT NULL,
                        opRatio TEXT NOT NULL,
                        previousOp TEXT NOT NULL,
                        isGroupOp INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }
    }
}