package com.saroj.prismscan.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanHistoryDao {

    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<ScanHistoryEntity>>

    @Query("""
        SELECT * FROM scan_history 
        WHERE (:query = '' OR displayValue LIKE '%' || :query || '%' OR rawValue LIKE '%' || :query || '%')
        AND (:typeName IS NULL OR typeName = :typeName)
        ORDER BY timestamp DESC
    """)
    fun searchHistory(query: String, typeName: String?): Flow<List<ScanHistoryEntity>>

    @Query("SELECT * FROM scan_history WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ScanHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ScanHistoryEntity): Long

    @Query("DELETE FROM scan_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM scan_history WHERE id IN (:ids)")
    suspend fun deleteMultiple(ids: List<Long>)

    @Query("DELETE FROM scan_history")
    suspend fun clearAll()

    @Query("UPDATE scan_history SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)
}
