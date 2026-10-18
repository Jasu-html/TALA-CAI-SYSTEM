package com.capstone.tala.core.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary = TALAColor.Primary,
    onPrimary = TALAColor.OnPrimary,
    secondary = TALAColor.Secondary,
    onSecondary = TALAColor.OnSecondary,
    tertiary = TALAColor.Tertiary,
    onTertiary = TALAColor.OnTertiary,
    error = TALAColor.Red,
    background = TALAColor.BackgroundCream,
    onBackground = TALAColor.OnSurface,
    surface = TALAColor.SurfaceCream,
    onSurface = TALAColor.OnSurface,
    surfaceVariant = TALAColor.SurfaceVariant,
    onSurfaceVariant = TALAColor.OnSurfaceVariant
)

private val DarkColorScheme = darkColorScheme(
    primary = TALAColor.Primary,
    onPrimary = TALAColor.OnPrimary,
    secondary = TALAColor.Secondary,
    onSecondary = TALAColor.OnSecondary,
    tertiary = TALAColor.StarYellow,
    onTertiary = TALAColor.NightBlue,
    error = TALAColor.Red,
    background = TALAColor.NightBlue,
    onBackground = TALAColor.BackgroundCream,
    surface = Color(0xFF1A2942),
    onSurface = TALAColor.BackgroundCream
)

@Composable
fun TalaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
        typography = TalaTypography,
        content = content
    )
}
