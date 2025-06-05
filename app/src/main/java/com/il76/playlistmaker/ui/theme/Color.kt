package com.il76.playlistmaker.ui.theme

import android.content.res.Resources.Theme
import androidx.compose.ui.graphics.Color

open class ThemeColors(
    open val black: Color = Color(0xFF000000),
    open val white: Color = Color(0xFFFFFFFF),
    open val playerInfoName: Color = Color(0xFF1A1B22),
    open val settingsText: Color = Color(0xFF000000),
    open val backgroundMain: Color = Color(0xFF3772E7),
    open val backgroundSecondary: Color = Color(0xFFFFFFFF),
    open val switcherThumb: Color = Color(0xFFCECFD2),
    open val switcherTrack: Color = Color(0xFFF0F1F3),
    open val clearIconFill: Color = Color(0xFFAEAFB4),
    open val mainHeader: Color = Color(0xFFFFFFFF),
    open val mainIconFill: Color = Color(0xFF1A1B22),
    open val settingsIconFill: Color = Color(0xFFAEAFB4),
    open val backIconFill: Color = Color(0xFF1A1B22),
    open val searchEditMain: Color = Color(0xFFAEAFB4),
    open val searchEditBg: Color = Color(0xFFE6E8EB),
    open val playerIconBg: Color = Color(0xFFC5C6C7),
    open val playerIconPlay: Color = Color(0xFF000000),
    open val playerIconPlayInner: Color = Color(0xFFFFFFFF),
    open val progressbarTint: Color = Color(0xFF3772E7),
    open val bottomNavigationBorder: Color = Color(0xFFE6E8EB),
    open val bottomNavigationItemActive: Color = Color(0xFF3772E7),
    open val bottomNavigationItemDefault: Color = Color(0xFF1A1B22),
    open val bottomSheetHandle: Color = Color(0xFFE6E8EB),
    open val playlistCreateIconFill: Color = Color(0xFFAEAFB4),
    open val playlistCreateDisabled: Color = Color(0xFFAEAFB4),
    open val playlistCreateEnabled: Color = Color(0xFF3772E7),
    open val playlistCreateEmptyField: Color = Color(0xFFDADADA),
    open val playlistCreateFilledField: Color = Color(0xFF3772E7),
    open val dialogColor: Color = Color(0xFF000000),
    open val playlistIconFill: Color = Color(0xFF1A1B22)
)

object ThemeDarkColors : ThemeColors() {
    override val black = Color(0xFF000000)
    override val white = Color(0xFFFFFFFF)
    override val playerInfoName = Color(0xFFAEAFB4)
    override val settingsText = Color(0xFFFFFFFF)
    override val backgroundMain = Color(0xFF1A1B22)
    override val backgroundSecondary = Color(0xFF1A1B22)
    override val switcherThumb = Color(0xFF3772E7)
    override val switcherTrack = Color(0xFF5A6887)
    override val clearIconFill = Color(0xFF1A1B22)
    override val mainHeader = Color(0xFFFFFFFF)
    override val mainIconFill = Color(0xFF1A1B22)
    override val settingsIconFill = Color(0xFFFFFFFF)
    override val backIconFill = Color(0xFFFFFFFF)
    override val searchEditMain = Color(0xFF1A1B22)
    override val searchEditBg = Color(0xFFFFFFFF)
    override val playerIconBg = Color(0xFF535459)
    override val playerIconPlay = Color(0xFFFFFFFF)
    override val playerIconPlayInner = Color(0xFF000000)
    override val progressbarTint = Color(0xFF3772E7)
    override val bottomNavigationBorder = Color(0xFFE6E8EB)
    override val bottomNavigationItemActive = Color(0xFF3772E7)
    override val bottomNavigationItemDefault = Color(0xFFFFFFFF)
    override val bottomSheetHandle = Color(0xFFFFFFFF)
    override val playlistCreateIconFill = Color(0xFFAEAFB4)
    override val playlistCreateDisabled = Color(0xFFAEAFB4)
    override val playlistCreateEnabled = Color(0xFF3772E7)
    override val playlistCreateEmptyField = Color(0xFFFFFFFF)
    override val playlistCreateFilledField = Color(0xFF3772E7)
    override val dialogColor = Color(0xFFFFFFFF)
    override val playlistIconFill = Color(0xFF1A1B22)
}