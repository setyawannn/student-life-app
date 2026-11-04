package com.example.student_life_app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = PrimaryBlueContainer,
    onPrimaryContainer = OnPrimaryBlueContainer,

    secondary = TextSecondary,
    onSecondary = Color.White,
    secondaryContainer = StatusInfoContainer,
    onSecondaryContainer = TextPrimary,

    tertiary = StatusWarning,
    onTertiary = Color.White,
    tertiaryContainer = StatusWarningContainer,
    onTertiaryContainer = OnStatusWarningContainer,

    error = StatusError,
    onError = Color.White,
    errorContainer = StatusErrorContainer,
    onErrorContainer = OnStatusErrorContainer,

    background = ScreenCanvasBackground,
    onBackground = TextPrimary,

    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantBlue,
    onSurfaceVariant = TextSecondary,

    outline = BorderOutline,
    outlineVariant = BorderOutlineVariant
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color(0xFF002A78),
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = Color(0xFFDBE1FF),

    secondary = DarkTextSecondary,
    onSecondary = Color(0xFF1E293B),
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = DarkTextPrimary,

    tertiary = Color(0xFFFFB95F),
    onTertiary = Color(0xFF452B00),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = DarkBackground,
    onBackground = DarkTextPrimary,

    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,

    outline = Color(0xFF475569)
)

@Composable
fun StudentlifeappTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set dynamicColor = false by default to strictly enforce the Figma brand palette
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
