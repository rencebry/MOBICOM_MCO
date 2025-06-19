package com.mobicom.s17.group8.mobicom_mco.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mobicom.s17.group8.mobicom_mco.R

val BricolageFontFamily = FontFamily(
    Font(R.font.bg)
)

val InterFontFamily = FontFamily(
    Font(R.font.inter)
)

val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = BricolageFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    titleMedium =TextStyle(
        fontFamily = BricolageFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    titleSmall =TextStyle(
        fontFamily = BricolageFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodySmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
)
