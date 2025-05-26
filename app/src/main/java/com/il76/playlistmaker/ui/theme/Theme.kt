package com.il76.playlistmaker.ui.theme

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
    // Primary colors
    primary = Color(0xFF3772E7),  // Акцентный цвет (switcherThumb, progressbarTint, etc.)
    primaryContainer = Color(0xFF5A6887),  // Темный вариант primary (switcherTrack)

    // Secondary colors
    secondary = Color(0xFFFFFFFF),  // Белый (settingsText, mainHeader, etc.)
    secondaryContainer = Color(0xFFAEAFB4),  // Серый (playerInfoName, playlistCreateIconFill)

    // Background colors
    background = Color(0xFF1A1B22),  // Основной темный фон (backgroundMain, backgroundSecondary)
    surface = Color(0xFF1A1B22),  // Поверхности (совпадает с фоном)

    // Surface variants
    surfaceVariant = Color(0xFF535459),  // Темно-серый (playerIconBg)
    surfaceTint = Color(0xFFFFFFFF),  // Белый (searchEditBg, bottomSheetHandle)

    // On colors
    onPrimary = Color(0xFFFFFFFF),  // Белый на primary (mainHeader)
    onSecondary = Color(0xFF1A1B22),  // Темный на secondary (mainIconFill)
    onBackground = Color(0xFFFFFFFF),  // Белый на фоне (settingsText)
    onSurface = Color(0xFFFFFFFF),  // Белый на поверхности (backIconFill)
    onSurfaceVariant = Color(0xFFAEAFB4),  // Серый (playlistCreateIconFill)

    // Additional colors
    outline = Color(0xFFE6E8EB),  // Светло-серый (bottomNavigationBorder)
    inverseSurface = Color(0xFFFFFFFF),  // Белый (playerIconPlay)
    inverseOnSurface = Color(0xFF000000),  // Черный (playerIconPlayInner)

    // State colors
    //disabled = Color(0xFFAEAFB4),  // Серый (playlistCreateDisabled)
    //enabled = Color(0xFF3772E7)   // Синий (playlistCreateEnabled)
)

private val LightColorScheme = lightColorScheme(
    // Primary colors
    primary = Color(0xFF3772E7),  // Основной синий (backgroundMain, progressbarTint, bottomNavigationItemActive, etc.)
    primaryContainer = Color(0xFFE6E8EB),  // Светло-серый (searchEditBg, bottomNavigationBorder, bottomSheetHandle)

    // Secondary colors
    secondary = Color(0xFF1A1B22),  // Тёмно-серый/почти чёрный (playerInfoName, mainIconFill, backIconFill, etc.)
    secondaryContainer = Color(0xFFAEAFB4),  // Серый (clearIconFill, settingsIconFill, playlistCreateIconFill)

    // Background colors
    background = Color(0xFFFFFFFF),  // Белый (white, backgroundSecondary, mainHeader)
    surface = Color(0xFFF0F1F3),  // Очень светлый серый (switcherTrack)

    // Surface variants
    surfaceVariant = Color(0xFFCECFD2),  // Серый (switcherThumb)
    surfaceTint = Color(0xFFDADADA),  // Светло-серый (playlistCreateEmptyField)

    // On colors (for text/icons on top of other colors)
    onPrimary = Color(0xFFFFFFFF),  // Белый (mainHeader)
    onSecondary = Color(0xFF000000),  // Чёрный (settingsText, playerIconPlay, dialogColor)
    onBackground = Color(0xFF1A1B22),  // Тёмно-серый (playerInfoName)
    onSurface = Color(0xFF1A1B22),  // Тёмно-серый (mainIconFill)
    onSurfaceVariant = Color(0xFFAEAFB4),  // Серый (settingsIconFill)

    // Additional colors
    outline = Color(0xFFE6E8EB),  // Светло-серый (searchEditBg)
    inverseSurface = Color(0xFFC5C6C7),  // Серый (playerIconBg)
    inverseOnSurface = Color(0xFFFFFFFF),  // Белый (playerIconPlayInner)

// State colors
//open val disabled: Color = Color(0xFFAEAFB4),  // Серый (playlistCreateDisabled)
//open val enabled: Color = Color(0xFF3772E7)  // Основной синий (playlistCreateEnabled)
)

@Composable
fun PlaylistMakerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
