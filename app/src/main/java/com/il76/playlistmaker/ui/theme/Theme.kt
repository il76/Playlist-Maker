package com.il76.playlistmaker.ui.theme

import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

//private val LightColorPalette = lightColorScheme(
//    primary = Color.White,
//    onPrimary = Color.Black,
//)
//
//private val DarkColorPalette = darkColorScheme(
//    primary = Color.Black,
//    onPrimary = Color.White,
//)
//val LocalColors = staticCompositionLocalOf { LightThemeColors }
enum class ThemeType {
    LIGHT, DARK
}

object PlaylistMakerTheme{
    private val lightColors = ThemeColors()
    private val darkColors = ThemeDarkColors

    var currentColors: ThemeColors = lightColors
        private set

    fun setTheme(darkTheme: Boolean) {
        currentColors = if (darkTheme) darkColors else lightColors
    }
}
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )

)

