package com.example.focuswellbeing.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


private val Forest = Color(0xFF176B55)
private val MutedText = Color(0xFF737A76)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar() {
    TopAppBar(
        title = {
            Column() {
                Text(
                    text = "FocusForge",
                    fontWeight = FontWeight.Bold,
                    color = Forest
                )
                Text(
                    text = "BUILD BETTER HABITS",
                    fontSize = 10.sp,
                    letterSpacing = 1.5.sp,
                    color = MutedText
                )
            }
        }
    )
}