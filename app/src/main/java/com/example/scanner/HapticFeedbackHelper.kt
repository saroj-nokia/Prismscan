package com.example.scanner

import android.content.Context
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.VibratorManager

object HapticFeedbackHelper {

    fun performScanSuccess(context: Context, isEnabled: Boolean) {
        if (!isEnabled) return

        try {
            val vibratorManager = context.getSystemService(VibratorManager::class.java)
            val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
            vibratorManager?.vibrate(CombinedVibration.createParallel(effect))
        } catch (_: Exception) {
            // Gracefully ignore if vibrator is unavailable
        }
    }
}
