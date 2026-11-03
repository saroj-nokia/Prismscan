package com.saroj.prismscan.domain.model

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

        // URI.getHost() is null for non-ASCII or underscore hosts, so fall back to the raw authority.
        val host = (uri?.host ?: uri?.authority?.substringAfterLast('@')?.substringBefore(':'))
            ?.lowercase(Locale.ROOT) ?: ""
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

        val hostHasOddChars = host.any { c -> c.code > 127 || c in "@%&;:!*+~" }
        if (hostHasOddChars && !isPunycode) {
            warnings.add("Suspicious Characters: The domain contains unusual symbols or non-standard characters.")
        }

        val hasUserInfo = uri?.userInfo != null
        if (hasUserInfo) {
            warnings.add("Embedded Login: Text before the @ is not the destination. The link really goes to $host.")
        }
        val hasSuspiciousChars = hostHasOddChars || hasUserInfo

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
