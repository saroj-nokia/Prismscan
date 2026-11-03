package com.example.domain.model

import java.net.URI
import java.util.Locale

data class SafetyInfo(
    val isUrl: Boolean,
    val isHttps: Boolean = false,
    val domain: String = "",
    val pathAndQuery: String = "",
    val isPunycode: Boolean = false,
    val isIpAddress: Boolean = false,
    val hasSuspiciousChars: Boolean = false,
    val warnings: List<String> = emptyList()
) {
    val isDangerous: Boolean
        get() = isPunycode || isIpAddress || hasSuspiciousChars || (!isHttps && isUrl)
}

object SafetyChecker {

    private val IP_PATTERN = Regex("^(\\d{1,3}\\.){3}\\d{1,3}$")

    fun analyzeUrl(rawUrl: String): SafetyInfo {
        val trimmed = rawUrl.trim()
        val warnings = mutableListOf<String>()

        val uri = try {
            val candidate = if (!trimmed.startsWith("http://", ignoreCase = true) &&
                !trimmed.startsWith("https://", ignoreCase = true)
            ) {
                "https://$trimmed"
            } else {
                trimmed
            }
            URI(candidate)
        } catch (_: Exception) {
            null
        }

        val isHttps = trimmed.startsWith("https://", ignoreCase = true)
        if (!isHttps) {
            warnings.add("Insecure Connection: This link uses unencrypted HTTP. Data sent or received may be intercepted.")
        }

        val host = uri?.host?.lowercase(Locale.ROOT) ?: ""
        val pathAndQuery = buildString {
            uri?.path?.let { append(it) }
            uri?.query?.let { append("?").append(it) }
        }

        val isPunycode = host.contains("xn--")
        if (isPunycode) {
            warnings.add("Internationalized Domain Name (Punycode): This domain uses special characters which can disguise look-alike letters (phishing risk).")
        }

        val isIpAddress = IP_PATTERN.matches(host)
        if (isIpAddress) {
            warnings.add("IP Address Host: The link points directly to a raw numerical IP address instead of a verified domain name.")
        }

        val hasSuspiciousChars = host.any { c -> c.code > 127 || c in "@%&;:!*+~" }
        if (hasSuspiciousChars && !isPunycode) {
            warnings.add("Suspicious Characters: The domain contains unusual symbols or non-standard characters.")
        }

        return SafetyInfo(
            isUrl = true,
            isHttps = isHttps,
            domain = host.ifEmpty { trimmed },
            pathAndQuery = pathAndQuery,
            isPunycode = isPunycode,
            isIpAddress = isIpAddress,
            hasSuspiciousChars = hasSuspiciousChars,
            warnings = warnings
        )
    }
}
