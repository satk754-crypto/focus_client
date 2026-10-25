package com.example.focuswellbeing.features.dashboard.model

import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap

data class AppUsageItemUi(
    val packageName: String,
    val appName: String,
    val packageSubtitle: String,
    val iconEmoji: String,
    val appIconDrawable: Drawable? = null,
    val appIconBitmap: ImageBitmap? = null,
    val brandGradient: List<Color>,
    val accentColor: Color,
    val formattedTime: String,
    val percentage: Int,
    val isShieldActive: Boolean = false,
)