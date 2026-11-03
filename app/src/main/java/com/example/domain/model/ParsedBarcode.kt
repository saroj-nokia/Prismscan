package com.example.domain.model

data class ParsedBarcode(
    val rawValue: String,
    val displayValue: String,
    val formatName: String,
    val type: BarcodeType,
    val timestamp: Long = System.currentTimeMillis(),
    val safetyInfo: SafetyInfo = SafetyInfo(isUrl = false),
    // URL
    val url: String? = null,
    // Wi-Fi
    val wifiSsid: String? = null,
    val wifiPassword: String? = null,
    val wifiType: String? = null,
    val isWifiHidden: Boolean = false,
    // Contact
    val contactName: String? = null,
    val contactPhone: String? = null,
    val contactEmail: String? = null,
    val contactOrg: String? = null,
    val contactTitle: String? = null,
    // Email
    val emailAddress: String? = null,
    val emailSubject: String? = null,
    val emailBody: String? = null,
    // Phone
    val phoneNumber: String? = null,
    // SMS
    val smsNumber: String? = null,
    val smsMessage: String? = null,
    // Geo
    val geoLat: Double? = null,
    val geoLng: Double? = null,
    // Calendar
    val calendarTitle: String? = null,
    val calendarDescription: String? = null,
    val calendarStart: Long? = null,
    val calendarEnd: Long? = null,
    val calendarLocation: String? = null
)
