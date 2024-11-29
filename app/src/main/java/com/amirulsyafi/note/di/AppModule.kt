package com.amirulsyafi.note.di

import android.app.Application
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.RoomDatabase.Callback
import androidx.sqlite.db.SupportSQLiteDatabase
import com.amirulsyafi.note.App
import com.amirulsyafi.note.BuildConfig
import com.amirulsyafi.note.data.Database
import com.amirulsyafi.note.data.Database.Companion.DATABASE_NAME
import com.amirulsyafi.note.data.assignment.AssignmentDao
import com.amirulsyafi.note.data.note.NoteDao
import com.amirulsyafi.note.data.setting.SettingDao
import com.amirulsyafi.note.data.setting.SettingKey
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideApp(application: Application): App {
        return application as App
    }

    @Provides
    @Singleton
    fun createDatabase(app: App): Database {
        return androidx.room.Room.databaseBuilder(
            app.applicationContext, Database::class.java, DATABASE_NAME
        ).addCallback(object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                for (key in SettingKey.entries) {
                    val cv = ContentValues().apply {
                        put("key", key.name)
                    }
                    db.insert("Setting", SQLiteDatabase.CONFLICT_NONE, cv)
                }
            }
        }).addMigrations(Database.MIGRATION_1_2).build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val gson =
            GsonBuilder().setLenient()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create()

        val client = OkHttpClient.Builder().connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES).writeTimeout(1, TimeUnit.MINUTES)
            .retryOnConnectionFailure(false).cache(null).build()

        return Retrofit.Builder().baseUrl(BuildConfig.URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client).build()
    }

    @Provides
    @Singleton
    fun provideNoteDao(database: Database): NoteDao {
        return database.noteDao()
    }

    @Provides
    @Singleton
    fun provideSettingDao(database: Database): SettingDao {
        return database.settingDao()
    }

    @Provides
    @Singleton
    fun provideAssignmentDao(database: Database): AssignmentDao {
        return database.assignmentDao()
    }
}