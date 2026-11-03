package com.saroj.prismscan

import com.saroj.prismscan.domain.model.SafetyChecker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SafetyCheckerTest {

    @Test
    fun `https url is marked safe`() {
        val result = SafetyChecker.analyzeUrl("https://example.com/login")
        assertTrue(result.isHttps)
        assertFalse(result.isDangerous)
        assertEquals("example.com", result.domain)
        assertTrue(result.warnings.isEmpty())
    }

    @Test
    fun `http url is flagged as insecure`() {
        val result = SafetyChecker.analyzeUrl("http://insecure-site.com/form")
        assertFalse(result.isHttps)
        assertTrue(result.isDangerous)
        assertTrue(result.warnings.any { it.contains("Insecure Connection") })
    }

    @Test
    fun `punycode domain is flagged as dangerous`() {
        val result = SafetyChecker.analyzeUrl("https://xn--pple-43d.com")
        assertTrue(result.isPunycode)
        assertTrue(result.isDangerous)
        assertTrue(result.warnings.any { it.contains("Punycode") })
    }

    @Test
    fun `ip address domain is flagged as dangerous`() {
        val result = SafetyChecker.analyzeUrl("http://192.168.1.1/admin")
        assertTrue(result.isIpAddress)
        assertTrue(result.isDangerous)
        assertTrue(result.warnings.any { it.contains("IP Address Host") })
    }

    @Test
    fun `raw non-ascii look-alike domain is flagged`() {
        // Cyrillic "a" in place of Latin "a": java.net.URI returns a null host for this.
        val result = SafetyChecker.analyzeUrl("https://аpple.com/login")
        assertTrue(result.hasSuspiciousChars)
        assertTrue(result.isDangerous)
        assertEquals("аpple.com", result.domain)
    }

    @Test
    fun `embedded login before at sign is flagged and real host shown`() {
        val result = SafetyChecker.analyzeUrl("https://google.com@evil.com/")
        assertTrue(result.isDangerous)
        assertEquals("evil.com", result.domain)
        assertTrue(result.warnings.any { it.contains("Embedded Login") })
    }
}
