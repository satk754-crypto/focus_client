package com.example.focuswellbeing.features.focus.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FocusSessionBanner(
    isFocusActive: Boolean,
    remainingSeconds: Int,
    onStartFocus: (Int) -> Unit,
    onStopFocus: () -> Unit
) {
    var showDurationDialog by remember { mutableStateOf(false) }

    // 👉 Show duration selection popup when triggered
    if (showDurationDialog) {
        FocusDurationSelectionDialog(
            onDismiss = { showDurationDialog = false },
            onSelectDuration = { totalMins ->
                showDurationDialog = false
                onStartFocus(totalMins)
            }
        )
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFocusActive) Color(0xFF0EA5E9).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🎯", fontSize = 22.sp)
                    Text(
                        text = if (isFocusActive) "Focus Mode Active" else "Deep Focus Timer",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (isFocusActive) {
                    val minutes = remainingSeconds / 60
                    val seconds = remainingSeconds % 60
                    Text(
                        text = String.format("%02d:%02d", minutes, seconds),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0EA5E9)
                    )
                }
            }

            Text(
                text = if (isFocusActive)
                    "All selected frozen apps are completely locked and unresponsive until the timer runs out."
                else
                    "Start a timer session to strictly block all frozen apps and protect your productivity.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = {
                    if (isFocusActive) onStopFocus() else onStartFocus(25) // 25 min Pomodoro default
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFocusActive) MaterialTheme.colorScheme.error else Color(0xFF0EA5E9)
                )
            ) {
                Text(
                    text = if (isFocusActive) "End Focus Session Early" else "Start 25-Min Focus Session",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}