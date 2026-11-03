package com.example.domain.parser

import com.example.domain.model.BarcodeType
import com.example.domain.model.ParsedBarcode
import com.example.domain.model.SafetyChecker
import com.google.mlkit.vision.barcode.common.Barcode

object BarcodeParser {

    fun parse(barcode: Barcode): ParsedBarcode {
        val raw = barcode.rawValue ?: ""
        val display = barcode.displayValue ?: raw
        val formatName = getFormatName(barcode.format)

        return when (barcode.valueType) {
            Barcode.TYPE_URL -> {
                val urlString = barcode.url?.url ?: raw
                val safety = SafetyChecker.analyzeUrl(urlString)
                ParsedBarcode(
                    rawValue = raw,
                    displayValue = display,
                    formatName = formatName,
                    type = BarcodeType.URL,
                    safetyInfo = safety,
                    url = urlString
                )
            }
            Barcode.TYPE_WIFI -> {
                val wifi = barcode.wifi
                val encType = when (wifi?.encryptionType) {
                    Barcode.WiFi.TYPE_WPA -> "WPA/WPA2"
                    Barcode.WiFi.TYPE_WEP -> "WEP"
                    Barcode.WiFi.TYPE_OPEN -> "Open"
                    else -> "Unknown"
                }
                ParsedBarcode(
                    rawValue = raw,
                    displayValue = wifi?.ssid ?: display,
                    formatName = formatName,
                    type = BarcodeType.WIFI,
                    wifiSsid = wifi?.ssid,
                    wifiPassword = wifi?.password,
                    wifiType = encType
                )
            }
            Barcode.TYPE_CONTACT_INFO -> {
                val contact = barcode.contactInfo
                val primaryPhone = contact?.phones?.firstOrNull()?.number
                val primaryEmail = contact?.emails?.firstOrNull()?.address
                val fullName = contact?.name?.formattedName?.takeIf { it.isNotBlank() }
                    ?: listOfNotNull(contact?.name?.first, contact?.name?.last).joinToString(" ")
                ParsedBarcode(
                    rawValue = raw,
                    displayValue = fullName.ifBlank { display },
                    formatName = formatName,
                    type = BarcodeType.CONTACT,
                    contactName = fullName,
                    contactPhone = primaryPhone,
                    contactEmail = primaryEmail,
                    contactOrg = contact?.organization,
                    contactTitle = contact?.title
                )
            }
            Barcode.TYPE_EMAIL -> {
                val email = barcode.email
                ParsedBarcode(
                    rawValue = raw,
                    displayValue = email?.address ?: display,
                    formatName = formatName,
                    type = BarcodeType.EMAIL,
                    emailAddress = email?.address,
                    emailSubject = email?.subject,
                    emailBody = email?.body
                )
            }
            Barcode.TYPE_PHONE -> {
                val phone = barcode.phone
                ParsedBarcode(
                    rawValue = raw,
                    displayValue = phone?.number ?: display,
                    formatName = formatName,
                    type = BarcodeType.PHONE,
                    phoneNumber = phone?.number
                )
            }
            Barcode.TYPE_SMS -> {
                val sms = barcode.sms
                ParsedBarcode(
                    rawValue = raw,
                    displayValue = sms?.phoneNumber ?: display,
                    formatName = formatName,
                    type = BarcodeType.SMS,
                    smsNumber = sms?.phoneNumber,
                    smsMessage = sms?.message
                )
            }
            Barcode.TYPE_GEO -> {
                val geo = barcode.geoPoint
                ParsedBarcode(
                    rawValue = raw,
                    displayValue = "${geo?.lat ?: 0.0}, ${geo?.lng ?: 0.0}",
                    formatName = formatName,
                    type = BarcodeType.GEO,
                    geoLat = geo?.lat,
                    geoLng = geo?.lng
                )
            }
            Barcode.TYPE_CALENDAR_EVENT -> {
                val event = barcode.calendarEvent
                val start = event?.start?.rawValue?.toLongOrNull()
                val end = event?.end?.rawValue?.toLongOrNull()
                ParsedBarcode(
                    rawValue = raw,
                    displayValue = event?.summary ?: display,
                    formatName = formatName,
                    type = BarcodeType.CALENDAR,
                    calendarTitle = event?.summary,
                    calendarDescription = event?.description,
                    calendarStart = start,
                    calendarEnd = end,
                    calendarLocation = event?.location
                )
            }
            Barcode.TYPE_PRODUCT, Barcode.TYPE_ISBN -> {
                ParsedBarcode(
                    rawValue = raw,
                    displayValue = display,
                    formatName = formatName,
                    type = BarcodeType.PRODUCT
                )
            }
            else -> {
                // Secondary heuristic parsing for text-encoded barcodes
                parseRawText(raw, display, formatName)
            }
        }
    }

    private fun parseRawText(raw: String, display: String, formatName: String): ParsedBarcode {
        val trimmed = raw.trim()
        if (trimmed.startsWith("http://", ignoreCase = true) || trimmed.startsWith("https://", ignoreCase = true)) {
            val safety = SafetyChecker.analyzeUrl(trimmed)
            return ParsedBarcode(
                rawValue = raw,
                displayValue = display,
                formatName = formatName,
                type = BarcodeType.URL,
                safetyInfo = safety,
                url = trimmed
            )
        }

        if (trimmed.startsWith("WIFI:", ignoreCase = true)) {
            val ssid = Regex("S:([^;]+)").find(trimmed)?.groupValues?.getOrNull(1)
            val pass = Regex("P:([^;]+)").find(trimmed)?.groupValues?.getOrNull(1)
            val type = Regex("T:([^;]+)").find(trimmed)?.groupValues?.getOrNull(1) ?: "WPA"
            return ParsedBarcode(
                rawValue = raw,
                displayValue = ssid ?: display,
                formatName = formatName,
                type = BarcodeType.WIFI,
                wifiSsid = ssid,
                wifiPassword = pass,
                wifiType = type
            )
        }

        if (trimmed.startsWith("tel:", ignoreCase = true)) {
            val num = trimmed.removePrefix("tel:").removePrefix("TEL:")
            return ParsedBarcode(
                rawValue = raw,
                displayValue = num,
                formatName = formatName,
                type = BarcodeType.PHONE,
                phoneNumber = num
            )
        }

        if (trimmed.startsWith("smsto:", ignoreCase = true)) {
            val parts = trimmed.substring(6).split(":", limit = 2)
            val num = parts.getOrNull(0) ?: ""
            val msg = parts.getOrNull(1) ?: ""
            return ParsedBarcode(
                rawValue = raw,
                displayValue = num,
                formatName = formatName,
                type = BarcodeType.SMS,
                smsNumber = num,
                smsMessage = msg
            )
        }

        if (trimmed.startsWith("mailto:", ignoreCase = true)) {
            val email = trimmed.removePrefix("mailto:").removePrefix("MAILTO:")
            return ParsedBarcode(
                rawValue = raw,
                displayValue = email,
                formatName = formatName,
                type = BarcodeType.EMAIL,
                emailAddress = email
            )
        }

        // Standard text fallback
        return ParsedBarcode(
            rawValue = raw,
            displayValue = display,
            formatName = formatName,
            type = BarcodeType.TEXT
        )
    }

    fun getFormatName(format: Int): String {
        return when (format) {
            Barcode.FORMAT_QR_CODE -> "QR Code"
            Barcode.FORMAT_AZTEC -> "Aztec"
            Barcode.FORMAT_DATA_MATRIX -> "Data Matrix"
            Barcode.FORMAT_PDF417 -> "PDF417"
            Barcode.FORMAT_CODE_128 -> "Code 128"
            Barcode.FORMAT_CODE_39 -> "Code 39"
            Barcode.FORMAT_CODE_93 -> "Code 93"
            Barcode.FORMAT_CODABAR -> "Codabar"
            Barcode.FORMAT_EAN_13 -> "EAN-13"
            Barcode.FORMAT_EAN_8 -> "EAN-8"
            Barcode.FORMAT_ITF -> "ITF"
            Barcode.FORMAT_UPC_A -> "UPC-A"
            Barcode.FORMAT_UPC_E -> "UPC-E"
            else -> "Barcode"
        }
    }
}
