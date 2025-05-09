package com.oza.editnote.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color

val PrimaryBlue = Color(0xFF4CB3F8)
val DarkBackground = Color(0xFFC8E6FA)

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = DarkBackground,
    surface    = DarkBackground
)

private val LightColorScheme = lightColorScheme(
    primary   = PrimaryBlue,
    secondary = PurpleGrey40,
    tertiary  = Pink40,

    background = Color.White,
    surface    = Color.White
)
@Composable
fun EditNoteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // 全文フォントを NotoSansJP に差し替えた Typography を生成
    val typography = Typography().run {
        copy(
            displayLarge   = displayLarge.copy(fontFamily = NotoSansJP),
            displayMedium  = displayMedium.copy(fontFamily = NotoSansJP),
            displaySmall   = displaySmall.copy(fontFamily = NotoSansJP),
            headlineLarge  = headlineLarge.copy(fontFamily = NotoSansJP),
            headlineMedium = headlineMedium.copy(fontFamily = NotoSansJP),
            headlineSmall  = headlineSmall.copy(fontFamily = NotoSansJP),
            titleLarge     = titleLarge.copy(fontFamily = NotoSansJP),
            titleMedium    = titleMedium.copy(fontFamily = NotoSansJP),
            titleSmall     = titleSmall.copy(fontFamily = NotoSansJP),
            bodyLarge      = bodyLarge.copy(fontFamily = NotoSansJP),
            bodyMedium     = bodyMedium.copy(fontFamily = NotoSansJP),
            bodySmall      = bodySmall.copy(fontFamily = NotoSansJP),
            labelLarge     = labelLarge.copy(fontFamily = NotoSansJP),
            labelMedium    = labelMedium.copy(fontFamily = NotoSansJP),
            labelSmall     = labelSmall.copy(fontFamily = NotoSansJP),
        )
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = typography,
        content     = content
    )
}