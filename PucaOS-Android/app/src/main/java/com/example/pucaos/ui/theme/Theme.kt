package com.example.pucaos.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFC0EEC1),
    onPrimary = Color(0xFF071807),

    background = Color(0xFF071807),
    onBackground = Color(0xFFEAF5E9),

    surface = Color(0xFF102710),
    onSurface = Color(0xFFEAF5E9),

    surfaceVariant = Color(0xFF18351B),
    onSurfaceVariant = Color(0xFF8FAF91),

    outline = Color(0xFF29482B),

    secondary = Color(0xFF8FAF91),
    onSecondary = Color(0xFF071807),

    tertiary = Color(0xFFF2C879),
    onTertiary = Color(0xFF071807),
)

private val LightColorScheme = lightColorScheme(

    primary = Color(0xFFC0EEC1),
    onPrimary = Color(0xFF071807),

    background = Color(0xFF071807),
    onBackground = Color(0xFFEAF5E9),

    surface = Color(0xFF102710),
    onSurface = Color(0xFFEAF5E9),

    surfaceVariant = Color(0xFF18351B),
    onSurfaceVariant = Color(0xFF8FAF91),

    outline = Color(0xFF29482B),

    secondary = Color(0xFF8FAF91),
    onSecondary = Color(0xFF071807),

    tertiary = Color(0xFFF2C879),
    onTertiary = Color(0xFF071807),
)

@Composable
fun PucaOSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}