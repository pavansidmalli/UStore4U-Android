package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Westside-Inspired Dark Color Scheme
 */
val LuxuryDarkColorScheme = darkColorScheme(
    primary = PureWhite,
    onPrimary = ObsidianBlack,
    primaryContainer = CharcoalSurface,
    onPrimaryContainer = PureWhite,
    inversePrimary = ObsidianBlack,

    secondary = ChampagneGold,
    onSecondary = ObsidianBlack,
    secondaryContainer = Color(0xFF2B2518),
    onSecondaryContainer = PureWhite,

    tertiary = WestsideTerracotta,
    onTertiary = PureWhite,
    tertiaryContainer = CharcoalSurface,
    onTertiaryContainer = PureWhite,

    background = DarkBackground,
    onBackground = TextPrimaryDark,

    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    surfaceTint = PureWhite,

    outline = CharcoalBorder,
    outlineVariant = Color(0xFF24252A),

    error = LuxuryRed,
    onError = PureWhite,
    errorContainer = CharcoalSurface,
    onErrorContainer = LuxuryRed,

    scrim = ObsidianBlack
)

/**
 * Westside-Inspired Light Color Scheme
 *
 * Crisp, gallery-grade white surfaces, deep jet black primary branding,
 * subtle stone gray backgrounds, fine borders and warm terracotta accents.
 */
val LuxuryLightColorScheme = lightColorScheme(
    primary = ObsidianBlack,
    onPrimary = PureWhite,
    primaryContainer = LiteAshBg,
    onPrimaryContainer = ObsidianBlack,
    inversePrimary = PureWhite,

    secondary = WestsideTerracotta,
    onSecondary = PureWhite,
    secondaryContainer = SoftGrayBg,
    onSecondaryContainer = ObsidianBlack,

    tertiary = WestsideOchre,
    onTertiary = PureWhite,
    tertiaryContainer = SoftGoldBg,
    onTertiaryContainer = ObsidianBlack,

    background = LiteAshWhite,
    onBackground = TextPrimaryLight,

    surface = PureWhite,
    onSurface = TextPrimaryLight,
    surfaceVariant = SoftGrayBg,
    onSurfaceVariant = TextSecondaryLight,
    surfaceTint = ObsidianBlack,

    outline = GrayBorder,
    outlineVariant = SubtleDivider,

    error = LuxuryRed,
    onError = PureWhite,
    errorContainer = SoftGrayBg,
    onErrorContainer = LuxuryRed,

    scrim = ObsidianBlack
)

/**
 * Extended color accessors for bespoke elements.
 */
object LuxuryThemeDefaults {
    val goldAccent: Color
        @Composable
        @ReadOnlyComposable
        get() = WestsideTerracotta

    val cardBackground: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.surface

    val subtleBorder: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.outline

    val chipBackground: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.surfaceVariant
}

/**
 * Global LuxuryTheme helper
 */
object LuxuryTheme {
    val isDark: Boolean
        @Composable
        @ReadOnlyComposable
        get() = false

    val cardBackground: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.surface

    val background: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.background

    val subtleBorder: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.outline

    val subtleBg: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.surfaceVariant

    val textPrimary: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.onSurface

    val textSecondary: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.onSurfaceVariant

    val buttonBackground: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.primary

    val buttonContent: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.onPrimary

    val goldAccent: Color
        @Composable
        @ReadOnlyComposable
        get() = WarmGold
}

/**
 * UStore4U Material 3 Theme Composable with Westside styling.
 */
@Composable
fun UStore4UTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> LuxuryDarkColorScheme
        else -> LuxuryLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

/**
 * Compatibility alias for MyApplicationTheme
 */
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    UStore4UTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}

