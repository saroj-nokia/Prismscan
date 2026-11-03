package com.example

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.local.HistoryRepository
import com.example.data.local.PreferencesRepository

class PrismApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}

interface AppContainer {
    val historyRepository: HistoryRepository
    val preferencesRepository: PreferencesRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val database: AppDatabase by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "prism_scan.db"
        ).fallbackToDestructiveMigration().build()
    }

    override val preferencesRepository: PreferencesRepository by lazy {
        PreferencesRepository(context.applicationContext)
    }

    override val historyRepository: HistoryRepository by lazy {
        HistoryRepository(database.scanHistoryDao(), preferencesRepository)
    }
}
