package com.example.proyfinal.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Blue       = Color(0xFF4A90D9)
val BlueLight  = Color(0xFFEBF5FF)
val GreenTaken = Color(0xFF27AE60)
val RedMissed  = Color(0xFFE74C3C)

private val LightColors = lightColorScheme(
    primary          = Blue,
    onPrimary        = Color.White,
    primaryContainer = BlueLight,
    background       = Color.White,
    surface          = Color.White
)

@Composable
fun MediAlertTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightColors, content = content)
}