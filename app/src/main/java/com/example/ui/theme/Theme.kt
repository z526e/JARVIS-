package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val JarvisColorScheme = darkColorScheme(
  primary = JarvisPrimaryGlow,
  secondary = JarvisSecondaryGlow,
  tertiary = JarvisSuccess,
  background = JarvisBackground,
  surface = JarvisSurface,
  onPrimary = JarvisBackground,
  onSecondary = JarvisBackground,
  onBackground = JarvisTextPrimary,
  onSurface = JarvisTextPrimary
)

private val DarkColorScheme =
  darkColorScheme(primary = Purple80, secondary = PurpleGrey80, tertiary = Pink80)

private val LightColorScheme =
  lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark theme by default for Jarvis sci-fi experience
  dynamicColor: Boolean = false, // Disable dynamic colors to keep JARVIS HUD blue
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> JarvisColorScheme
      else -> JarvisColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
