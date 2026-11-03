package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrismPrimaryDark,
    onPrimary = PrismOnPrimaryDark,
    primaryContainer = PrismPrimaryContainerDark,
    onPrimaryContainer = PrismOnPrimaryContainerDark,
    secondary = PrismSecondaryDark,
    onSecondary = PrismOnSecondaryDark,
    secondaryContainer = PrismSecondaryContainerDark,
    onSecondaryContainer = PrismOnSecondaryContainerDark,
    tertiary = PrismTertiaryDark,
    onTertiary = PrismOnTertiaryDark,
    tertiaryContainer = PrismTertiaryContainerDark,
    onTertiaryContainer = PrismOnTertiaryContainerDark,
    background = PrismBackgroundDark,
    onBackground = PrismOnBackgroundDark,
    surface = PrismSurfaceDark,
    onSurface = PrismOnSurfaceDark,
    surfaceVariant = PrismSurfaceVariantDark,
    onSurfaceVariant = PrismOnSurfaceVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = PrismPrimaryLight,
    onPrimary = PrismOnPrimaryLight,
    primaryContainer = PrismPrimaryContainerLight,
    onPrimaryContainer = PrismOnPrimaryContainerLight,
    secondary = PrismSecondaryLight,
    onSecondary = PrismOnSecondaryLight,
    secondaryContainer = PrismSecondaryContainerLight,
    onSecondaryContainer = PrismOnSecondaryContainerLight,
    tertiary = PrismTertiaryLight,
    onTertiary = PrismOnTertiaryLight,
    tertiaryContainer = PrismTertiaryContainerLight,
    onTertiaryContainer = PrismOnTertiaryContainerLight,
    background = PrismBackgroundLight,
    onBackground = PrismOnBackgroundLight,
    surface = PrismSurfaceLight,
    onSurface = PrismOnSurfaceLight,
    surfaceVariant = PrismSurfaceVariantLight,
    onSurfaceVariant = PrismOnSurfaceVariantLight
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PrismScanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        motionScheme = MotionScheme.expressive(),
        typography = Typography,
        content = content
    )
}
