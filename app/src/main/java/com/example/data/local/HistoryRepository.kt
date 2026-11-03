package com.example.data.local

import com.example.domain.model.ParsedBarcode
import kotlinx.coroutines.flow.Flow

class HistoryRepository(
    private val dao: ScanHistoryDao,
    private val prefs: PreferencesRepository
) {

    fun getAllHistory(): Flow<List<ScanHistoryEntity>> = dao.getAllHistory()

    fun searchHistory(query: String, typeName: String?): Flow<List<ScanHistoryEntity>> {
        val normalizedType = if (typeName.isNullOrBlank() || typeName.equals("All", ignoreCase = true)) null else typeName
        return dao.searchHistory(query.trim(), normalizedType)
    }

    suspend fun saveScan(barcode: ParsedBarcode): Long? {
        if (!prefs.historyEnabled.value) return null

        val entity = ScanHistoryEntity(
            rawValue = barcode.rawValue,
            displayValue = barcode.displayValue,
            formatName = barcode.formatName,
            typeName = barcode.type.label,
            timestamp = barcode.timestamp,
            url = barcode.url,
            isHttps = barcode.safetyInfo.isHttps,
            isDangerous = barcode.safetyInfo.isDangerous,
            extraDetails = buildExtraDetails(barcode)
        )
        return dao.insert(entity)
    }

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    suspend fun deleteMultiple(ids: List<Long>) = dao.deleteMultiple(ids)

    suspend fun clearAll() = dao.clearAll()

    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) = dao.updateFavorite(id, isFavorite)

    private fun buildExtraDetails(barcode: ParsedBarcode): String? {
        val parts = mutableListOf<String>()
        barcode.wifiSsid?.let { parts.add("SSID: $it") }
        barcode.wifiType?.let { parts.add("Sec: $it") }
        barcode.contactPhone?.let { parts.add("Phone: $it") }
        barcode.contactEmail?.let { parts.add("Email: $it") }
        barcode.phoneNumber?.let { parts.add("Tel: $it") }
        barcode.smsNumber?.let { parts.add("SMS: $it") }
        if (barcode.geoLat != null && barcode.geoLng != null) {
            parts.add("Lat: ${barcode.geoLat}, Lng: ${barcode.geoLng}")
        }
        return if (parts.isNotEmpty()) parts.joinToString(" • ") else null
    }
}
