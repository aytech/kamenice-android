package com.mlyn.kamenice.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DarkColorPalette = darkColors(
    primary = B400,
    primaryVariant = B400,
    secondary = N800,
    background = DN20,
    surface = DN40,
    onPrimary = N20,
    onSecondary = N0,
    onBackground = DN800,
)

private val LightColorPalette = lightColors(
    primary = B400,
    primaryVariant = B400,
    secondary = N800,
    background = N30,
    surface = N0,
    onPrimary = N20,
    onSecondary = N0,
    onBackground = N800,
)

// Set of Material typography styles to start with
val Typography = Typography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
    /* Other default text styles to override
      button = TextStyle(
          fontFamily = FontFamily.Default,
          fontWeight = FontWeight.W500,
          fontSize = 14.sp
      ),
      caption = TextStyle(
          fontFamily = FontFamily.Default,
          fontWeight = FontWeight.Normal,
          fontSize = 12.sp
      )
      */
)

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(0.dp)
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}