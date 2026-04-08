package com.example.barbershopcompose.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    secondary = SurfaceGray,
    tertiary = LightBlue,
    background = BackgroundBlack,
    surface = BackgroundBlack,
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
)

// Deixamos o Light igual ao Dark para o app manter o estilo premium em qualquer modo
private val LightColorScheme = DarkColorScheme

@Composable
fun BarberShopComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}