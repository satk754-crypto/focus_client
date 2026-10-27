package com.example.focuswellbeing.features.focus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun DeepFocusBanner(
    isFocusActive: Boolean,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit
) {
    // 45 minutes session countdown timer in seconds (45 * 60)
    var remainingSeconds by remember { mutableLongStateOf(45 * 60L) }

    LaunchedEffect(isFocusActive) {
        if (isFocusActive) {
            while (remainingSeconds > 0) {
                delay(1000L)
                remainingSeconds--
            }
        } else {
            remainingSeconds = 45 * 60L // Reset when stopped
        }
    }

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = if (isFocusActive) {
                            listOf(Color(0xFF059669), Color(0xFF0D9488)) // Emerald/Teal when active
                        } else {
                            listOf(Color(0xFF4F46E5), Color(0xFF06B6D4)) // Indigo/Cyan default
                        }
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (isFocusActive) "🛡️ FOCUS SESSION ACTIVE" else "⚡ DEEP FOCUS MODE",
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = if (isFocusActive) timeFormatted else "Block distractions instantly",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = if (isFocusActive) 28.sp else 18.sp
                    )
                    Text(
                        text = if (isFocusActive) "Apps are frozen. Stay present." else "Zero interruptions. Strict app freezing activated.",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }

                Button(
                    onClick = if (isFocusActive) onStopClick else onStartClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = if (isFocusActive) Color(0xFF059669) else Color(0xFF4F46E5)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = if (isFocusActive) "Stop" else "Start",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}