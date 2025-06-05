package com.il76.playlistmaker.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.il76.playlistmaker.R

//val FontFamilyYandex100 = FontFamily(
//    Font(R.font.ys_display_thin)
//)
//val FontFamilyYandex300 = FontFamily(
//    Font(R.font.ys_display_light)
//)
val FontFamilyYandex400 = FontFamily(
    Font(R.font.ys_display_regular)
)
val FontFamilyYandex500 = FontFamily(
    Font(R.font.ys_display_medium)
)
//val FontFamilyYandex600 = FontFamily(
//    Font(R.font.ys_display_bold)
//)
val FontFamilyYandex700 = FontFamily(
    Font(R.font.ys_display_heavy)
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyMedium = TextStyle(
        fontFamily = FontFamilyYandex400,
        fontSize = 16.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamilyYandex500,
        //fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        //lineHeight = 24.sp,
        //letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamilyYandex500,
        fontSize = 22.sp,
    ),
    // подписи под сообщениями об ошибках
    labelSmall = TextStyle(
        fontFamily = FontFamilyYandex400,
        fontSize = 12.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamilyYandex400,
        fontSize = 14.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamilyYandex400,
        fontSize = 16.sp,
    ),
)