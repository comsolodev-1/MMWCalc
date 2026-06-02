package com.solodev.mmwcalc.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary             = Primary40,
    onPrimary           = NeutralLight,
    primaryContainer    = Primary80,
    onPrimaryContainer  = Primary10,
    secondary           = Secondary40,
    onSecondary         = NeutralLight,
    secondaryContainer  = Secondary80,
    onSecondaryContainer = Secondary10,
    tertiary            = Tertiary40,
    onTertiary          = NeutralLight,
    tertiaryContainer   = Tertiary80,
    onTertiaryContainer = Tertiary10,
    error               = Error40,
    errorContainer      = Error80,
    background          = NeutralLight,
    surface             = NeutralLight,
)

private val DarkColorScheme = darkColorScheme(
    primary             = Primary80,
    onPrimary           = Primary10,
    primaryContainer    = Primary40,
    onPrimaryContainer  = Primary80,
    secondary           = Secondary80,
    onSecondary         = Secondary10,
    secondaryContainer  = Secondary40,
    onSecondaryContainer = Secondary80,
    tertiary            = Tertiary80,
    onTertiary          = Tertiary10,
    tertiaryContainer   = Tertiary40,
    onTertiaryContainer = Tertiary80,
    error               = Error80,
    errorContainer      = Error40,
    background          = NeutralDark,
    surface             = NeutralDark,
)

@Composable
fun MMWCalcTheme(
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
        typography  = MMWTypography,
        content     = content
    )
}