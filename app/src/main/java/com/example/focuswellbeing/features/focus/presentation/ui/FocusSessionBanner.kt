
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focuswellbeing.ui.theme.FocusBlue

@Composable
fun FocusSessionBanner(
    isFocusActive: Boolean,
    remainingSeconds: Int,
    onStartFocus: (Int) -> Unit,
    onStopFocus: () -> Unit
) {
    var showDurationSheet by remember { mutableStateOf(false) }

    if (showDurationSheet) {
        FocusDurationSelectionBottomSheet(
            onDismiss = {
                showDurationSheet = false
            },
            onStartFocus = { minutes ->
                showDurationSheet = false
                onStartFocus(minutes)
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFocusActive) {
                FocusBlue.copy(alpha = 0.15f)
            } else {
                MaterialTheme.colorScheme.surface
            }
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🎯", fontSize = 22.sp)

                    Text(
                        text = if (isFocusActive) {
                            "Focus Mode Active"
                        } else {
                            "Deep Focus Timer"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (isFocusActive) {
                    val minutes = remainingSeconds / 60
                    val seconds = remainingSeconds % 60

                    Text(
                        text = "%02d:%02d".format(minutes, seconds),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = FocusBlue
                    )
                }
            }

            Text(
                text = if (isFocusActive) {
                    "Your selected apps are blocked until the focus session ends."
                } else {
                    "Choose a focus duration and take control of your screen time."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = {
                    if (isFocusActive) {
                        onStopFocus()
                    } else {
                        showDurationSheet = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFocusActive) {
                        MaterialTheme.colorScheme.error
                    } else {
                        FocusBlue
                    }
                )
            ) {
                Text(
                    text = if (isFocusActive) {
                        "End Focus Session Early"
                    } else {
                        "Start Focus Session"
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

