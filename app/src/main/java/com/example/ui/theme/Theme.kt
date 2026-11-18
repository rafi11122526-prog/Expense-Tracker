package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Immutable
data class PocketBizExtendedColors(
    val isDark: Boolean,
    val canvasTopVeil: Color,
    val positive: Color,
    val positiveBright: Color,
    val positiveContainer: Color,
    val positiveBorder: Color,
    val negative: Color,
    val negativeBright: Color,
    val negativeContainer: Color,
    val negativeBorder: Color,
    val sky: Color,
    val skyBright: Color,
    val skyContainer: Color,
    val amber: Color,
    val amberBright: Color,
    val amberContainer: Color,
    val violet: Color,
    val violetBright: Color,
    val violetContainer: Color,
    val indigo: Color,
    val teal: Color,
    val tealContainer: Color,
    val peach: Color,
    val peachContainer: Color,
    val lime: Color,
    val limeContainer: Color,
    val heroStart: Color,
    val heroMid: Color,
    val heroEnd: Color,
    val cardBorder: Color,
    val chartPalette: List<Color>
)

private val LightExtendedColors = PocketBizExtendedColors(
    isDark = false,
    canvasTopVeil = SlateTopVeil,
    positive = PositiveEmerald,
    positiveBright = PositiveEmeraldBright,
    positiveContainer = PositiveEmeraldSoft,
    positiveBorder = PositiveEmeraldBorder,
    negative = NegativeCoral,
    negativeBright = NegativeCoralBright,
    negativeContainer = NegativeCoralSoft,
    negativeBorder = NegativeCoralBorder,
    sky = SkyAccent,
    skyBright = SkyBright,
    skyContainer = SkySoft,
    amber = AmberAccent,
    amberBright = AmberBright,
    amberContainer = AmberSoft,
    violet = VioletAccent,
    violetBright = VioletBright,
    violetContainer = VioletSoft,
    indigo = NavyDeep,
    teal = TealAccent,
    tealContainer = TealSoft,
    peach = PeachAccent,
    peachContainer = PeachSoft,
    lime = LimeAccent,
    limeContainer = LimeSoft,
    heroStart = HeroMidnight,
    heroMid = HeroIndigo,
    heroEnd = HeroEmeraldGlow,
    cardBorder = BorderSubtle,
    chartPalette = listOf(
        SkyChart,
        PositiveEmeraldBright,
        AmberChart,
        VioletChart,
        RoseChart,
        TealChart,
        PeachChart
    )
)

private val DarkExtendedColors = PocketBizExtendedColors(
    isDark = true,
    canvasTopVeil = DarkTopVeil,
    positive = PositiveEmeraldDark,
    positiveBright = PositiveEmeraldBright,
    positiveContainer = PositiveEmeraldSoftDark,
    positiveBorder = Color(0xFF155E47),
    negative = NegativeCoralDark,
    negativeBright = NegativeCoralBright,
    negativeContainer = NegativeCoralSoftDark,
    negativeBorder = Color(0xFF6B2133),
    sky = Color(0xFF60A5FA),
    skyBright = Color(0xFF3B82F6),
    skyContainer = Color(0xFF122C54),
    amber = Color(0xFFFBBF24),
    amberBright = Color(0xFFF59E0B),
    amberContainer = Color(0xFF422A0A),
    violet = Color(0xFFA78BFA),
    violetBright = Color(0xFF8B5CF6),
    violetContainer = Color(0xFF2B1C54),
    indigo = Color(0xFF93C5FD),
    teal = Color(0xFF22D3EE),
    tealContainer = Color(0xFF0E3A47),
    peach = Color(0xFFFB923C),
    peachContainer = Color(0xFF47250D),
    lime = Color(0xFF34D399),
    limeContainer = Color(0xFF0B382C),
    heroStart = Color(0xFF0E1B36),
    heroMid = Color(0xFF173162),
    heroEnd = Color(0xFF12415E),
    cardBorder = DarkBorder,
    chartPalette = listOf(
        Color(0xFF60A5FA),
        PositiveEmeraldDark,
        Color(0xFFFBBF24),
        Color(0xFFA78BFA),
        Color(0xFFFB7185),
        Color(0xFF22D3EE),
        Color(0xFFFB923C)
    )
)

val LocalPocketBizColors = staticCompositionLocalOf { LightExtendedColors }

private val LightColorScheme = lightColorScheme(
    primary = NavyDeep,
    onPrimary = PureWhite,
    primaryContainer = NavySoftTint,
    onPrimaryContainer = NavyDeep,
    secondary = PositiveEmerald,
    onSecondary = PureWhite,
    secondaryContainer = PositiveEmeraldSoft,
    onSecondaryContainer = Color(0xFF064E3B),
    tertiary = VioletAccent,
    onTertiary = PureWhite,
    tertiaryContainer = VioletSoft,
    onTertiaryContainer = Color(0xFF3B0764),
    error = NegativeCoral,
    onError = PureWhite,
    errorContainer = NegativeCoralSoft,
    onErrorContainer = Color(0xFF7F1D1D),
    background = SlateBackground,
    onBackground = TextPrimary,
    surface = PureWhite,
    onSurface = TextPrimary,
    surfaceVariant = SlateSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    outlineVariant = BorderSubtle
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF60A5FA),
    onPrimary = Color(0xFF081226),
    primaryContainer = Color(0xFF1C3059),
    onPrimaryContainer = DarkTextPrimary,
    secondary = PositiveEmeraldDark,
    onSecondary = Color(0xFF022C22),
    secondaryContainer = PositiveEmeraldSoftDark,
    onSecondaryContainer = Color(0xFFA7F3D0),
    tertiary = Color(0xFFA78BFA),
    onTertiary = Color(0xFF2E1065),
    tertiaryContainer = Color(0xFF2B1C54),
    onTertiaryContainer = Color(0xFFEDE9FE),
    error = NegativeCoralDark,
    onError = Color(0xFF450A0A),
    errorContainer = NegativeCoralSoftDark,
    onErrorContainer = Color(0xFFFECACA),
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    outlineVariant = DarkBorder
)

val PocketBizShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(26.dp)
)

@Composable
fun PocketBizTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(LocalPocketBizColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = PocketBizTypography,
            shapes = PocketBizShapes,
            content = content
        )
    }
}
