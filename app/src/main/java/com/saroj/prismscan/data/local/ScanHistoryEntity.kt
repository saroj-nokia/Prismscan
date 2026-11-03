package com.saroj.prismscan.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rawValue: String,
    val displayValue: String,
    val formatName: String,
    val typeName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val url: String? = null,
    val isHttps: Boolean = false,
    val isDangerous: Boolean = false,
    val extraDetails: String? = null
)
