package com.capstone.tala.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.capstone.tala.R

@OptIn(ExperimentalTextApi::class)
private val Fredoka = FontFamily(
    Font(R.font.fredoka_variable, FontWeight.Normal, variationSettings = FontVariation.Settings(FontVariation.weight(400))),
    Font(R.font.fredoka_variable, FontWeight.Medium, variationSettings = FontVariation.Settings(FontVariation.weight(500))),
    Font(R.font.fredoka_variable, FontWeight.SemiBold, variationSettings = FontVariation.Settings(FontVariation.weight(600))),
    Font(R.font.fredoka_variable, FontWeight.Bold, variationSettings = FontVariation.Settings(FontVariation.weight(700)))
)

@OptIn(ExperimentalTextApi::class)
private val Nunito = FontFamily(
    Font(R.font.nunito_variable, FontWeight.Normal, variationSettings = FontVariation.Settings(FontVariation.weight(400))),
    Font(R.font.nunito_variable, FontWeight.Medium, variationSettings = FontVariation.Settings(FontVariation.weight(500))),
    Font(R.font.nunito_variable, FontWeight.SemiBold, variationSettings = FontVariation.Settings(FontVariation.weight(600))),
    Font(R.font.nunito_variable, FontWeight.Bold, variationSettings = FontVariation.Settings(FontVariation.weight(700)))
)

val TalaTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Fredoka,
        fontWeight = FontWeight.Bold,
        fontSize = 52.sp,
        lineHeight = 60.sp,
        letterSpacing = 0.sp
    ),
    displayMedium = TextStyle(
        fontFamily = Fredoka,
        fontWeight = FontWeight.Bold,
        fontSize = 44.sp,
        lineHeight = 52.sp
    ),
    displaySmall = TextStyle(
        fontFamily = Fredoka,
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = Fredoka,
        fontWeight = FontWeight.SemiBold,
        fontSize = 34.sp,
        lineHeight = 42.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Fredoka,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = Fredoka,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Fredoka,
        fontWeight = FontWeight.SemiBold,
        fontSize = 23.sp,
        lineHeight = 30.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Fredoka,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 27.sp
    ),
    titleSmall = TextStyle(
        fontFamily = Fredoka,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 26.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 23.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Fredoka,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
)
