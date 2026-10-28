package com.example.focuswellbeing.features.focus.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun FocusDurationSelectionDialog(
    onDismiss: () -> Unit,
    onSelectDuration: (Int) -> Unit // returns total minutes selected
) {
    var showCustomInput by remember { mutableStateOf(false) }
    var customMinutesInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "🎯")
                Text(text = "Start Deep Focus", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (!showCustomInput) {
                    Text(text = "Choose your focus duration to lock selected apps:")

                    // 👉 Preset Option Buttons: 30, 45, 60 mins
                    Button(
                        onClick = { onSelectDuration(30) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9).copy(alpha = 0.15f))
                    ) {
                        Text("30 Minutes (Quick Sprint)", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { onSelectDuration(45) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9).copy(alpha = 0.15f))
                    ) {
                        Text("45 Minutes (Standard Block)", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { onSelectDuration(60) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9).copy(alpha = 0.15f))
                    ) {
                        Text("60 Minutes (Deep Work Hour)", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = { showCustomInput = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Custom Duration...")
                    }
                } else {
                    // 👉 Custom Duration Input Field
                    Text(text = "Enter custom focus duration in minutes:")
                    OutlinedTextField(
                        value = customMinutesInput,
                        onValueChange = { customMinutesInput = it.filter { char -> char.isDigit() } },
                        label = { Text("Duration (Minutes)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        },
        confirmButton = {
            if (showCustomInput) {
                Button(
                    onClick = {
                        val mins = customMinutesInput.toIntOrNull() ?: 0
                        if (mins > 0) {
                            onSelectDuration(mins)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9))
                ) {
                    Text("Start Focus")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = {
                if (showCustomInput) showCustomInput = false else onDismiss()
            }) {
                Text(if (showCustomInput) "Back" else "Cancel")
            }
        }
    )
}