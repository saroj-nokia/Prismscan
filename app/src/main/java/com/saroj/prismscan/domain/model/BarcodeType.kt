package com.saroj.prismscan.domain.model

enum class BarcodeType(val label: String) {
    URL("URL"),
    WIFI("Wi-Fi"),
    TEXT("Text"),
    CONTACT("Contact"),
    EMAIL("Email"),
    PHONE("Phone"),
    SMS("SMS"),
    GEO("Location"),
    CALENDAR("Calendar"),
    PRODUCT("Product Barcode")
}
