package com.opencode.chat.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

// ألوان مستوحاة من واجهة opencode داخل الطرفية (خلفية سوداء تقريبًا، نص أبيض مائل للرمادي)
val TermBackground = Color(0xFF0B0B0B)
val TermSurface = Color(0xFF141414)
val TermSurfaceVariant = Color(0xFF1C1C1C)
val TermTextPrimary = Color(0xFFE6E6E6)
val TermTextDim = Color(0xFF8A8A8A)
val TermAccentBlue = Color(0xFF5FB3F0)
val TermAccentOrange = Color(0xFFD69A52)
val TermBorder = Color(0xFF2A2A2A)

val TerminalColorScheme = darkColorScheme(
    background = TermBackground,
    surface = TermSurface,
    surfaceVariant = TermSurfaceVariant,
    primary = TermAccentBlue,
    onPrimary = Color.Black,
    onBackground = TermTextPrimary,
    onSurface = TermTextPrimary,
    onSurfaceVariant = TermTextDim,
    outline = TermBorder,
    error = Color(0xFFE06C6C)
)

private val MonoFamily = FontFamily.Monospace

val TerminalTypography = Typography(
    bodyLarge = TextStyle(fontFamily = MonoFamily, fontSize = 15.sp, color = TermTextPrimary),
    bodyMedium = TextStyle(fontFamily = MonoFamily, fontSize = 14.sp, color = TermTextPrimary),
    bodySmall = TextStyle(fontFamily = MonoFamily, fontSize = 12.sp, color = TermTextDim),
    labelSmall = TextStyle(fontFamily = MonoFamily, fontSize = 11.sp, color = TermTextDim),
    titleMedium = TextStyle(fontFamily = MonoFamily, fontSize = 16.sp, color = TermTextPrimary)
)
