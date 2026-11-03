package com.saroj.prismscan

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.saroj.prismscan.data.local.AppDatabase
import com.saroj.prismscan.data.local.HistoryRepository
import com.saroj.prismscan.data.local.PreferencesRepository

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
        ).fallbackToDestructiveMigration(dropAllTables = true).build()
    }

    override val preferencesRepository: PreferencesRepository by lazy {
        PreferencesRepository(context.applicationContext)
    }

    override val historyRepository: HistoryRepository by lazy {
        HistoryRepository(database.scanHistoryDao(), preferencesRepository)
    }
}
