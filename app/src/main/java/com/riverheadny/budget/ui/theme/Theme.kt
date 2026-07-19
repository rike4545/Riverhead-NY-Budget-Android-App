package com.riverheadny.budget.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Extracted directly from townofriverheadny.gov's own CSS variables, not approximated.
val BrandNavy = Color(0xFF284A69)
val BrandBlue = Color(0xFF4A7297)
val BrandSky = Color(0xFF4285A7)
val BrandTeal = Color(0xFF8DBABE)
val BrandGold = Color(0xFFBDAC34)
val BrandMint = Color(0xFF4A9885)
val BrandCoral = Color(0xFFC6503C)
val Page = Color(0xFFEBF1F4)
val CardSurface = Color(0xFFFBFCFE)

/**
 * Secondary/label text color. Compose's built-in `Color.Gray` (#888888) contrasts only ~3.5:1
 * against white/CardSurface — below the 4.5:1 WCAG AA minimum for normal-size text. This value
 * (#5F6368, Google's own Material "on-surface-variant" gray) contrasts ~4.6:1 while still reading
 * as a clearly secondary/muted tone next to BrandNavy body text.
 */
val MutedText = Color(0xFF5F6368)

@Composable
fun RiverheadTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = BrandBlue,
            secondary = BrandTeal,
            tertiary = BrandGold,
            background = Page,
            surface = CardSurface,
        ),
        content = content,
    )
}
