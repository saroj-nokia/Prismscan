package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("prism_scan_prefs", Context.MODE_PRIVATE)

    private val _historyEnabled = MutableStateFlow(prefs.getBoolean(KEY_HISTORY_ENABLED, true))
    val historyEnabled: StateFlow<Boolean> = _historyEnabled.asStateFlow()

    private val _vibrateEnabled = MutableStateFlow(prefs.getBoolean(KEY_VIBRATE_ENABLED, true))
    val vibrateEnabled: StateFlow<Boolean> = _vibrateEnabled.asStateFlow()

    private val _soundEnabled = MutableStateFlow(prefs.getBoolean(KEY_SOUND_ENABLED, false))
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _keepScreenOn = MutableStateFlow(prefs.getBoolean(KEY_KEEP_SCREEN_ON, true))
    val keepScreenOn: StateFlow<Boolean> = _keepScreenOn.asStateFlow()

    fun setHistoryEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HISTORY_ENABLED, enabled).apply()
        _historyEnabled.value = enabled
    }

    fun setVibrateEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_VIBRATE_ENABLED, enabled).apply()
        _vibrateEnabled.value = enabled
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
        _soundEnabled.value = enabled
    }

    fun setKeepScreenOn(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_KEEP_SCREEN_ON, enabled).apply()
        _keepScreenOn.value = enabled
    }

    companion object {
        private const val KEY_HISTORY_ENABLED = "key_history_enabled"
        private const val KEY_VIBRATE_ENABLED = "key_vibrate_enabled"
        private const val KEY_SOUND_ENABLED = "key_sound_enabled"
        private const val KEY_KEEP_SCREEN_ON = "key_keep_screen_on"
    }
}
