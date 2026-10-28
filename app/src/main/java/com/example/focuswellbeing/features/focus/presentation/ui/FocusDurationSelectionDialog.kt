
package com.example.focuswellbeing.features.focus.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.focuswellbeing.ui.theme.FocusBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusDurationSelectionBottomSheet(
    onDismiss: () -> Unit,
    onStartFocus: (Int) -> Unit
) {
    val durations = listOf(15, 25, 45, 60)

    var selectedMinutes by remember { mutableStateOf(25) }
    var customMinutes by remember { mutableStateOf("") }

    val isCustomEntered = customMinutes.isNotBlank()
    val durationToStart = if (isCustomEntered) {
        customMinutes.toIntOrNull()
    } else {
        selectedMinutes
    }

    val isValidDuration = durationToStart != null &&
            durationToStart in 1..180

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        ),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = RoundedCornerShape(
            topStart = 28.dp,
            topEnd = 28.dp
        ),
        dragHandle = {
            androidx.compose.material3.BottomSheetDefaults.DragHandle(
                color = MaterialTheme.colorScheme.onSurfaceVariant
                    .copy(alpha = 0.4f)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "🎯  Deep Focus",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = FocusBlue
                )

                Text(
                    text = "Choose how long you want to focus.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "QUICK DURATIONS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    durations.forEach { minutes ->
                        FilterChip(
                            selected = !isCustomEntered &&
                                    selectedMinutes == minutes,
                            onClick = {
                                selectedMinutes = minutes
                                customMinutes = ""
                            },
                            label = {
                                Text("${minutes}m")
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FocusBlue,
                                selectedLabelColor = Color.White,
                                containerColor = MaterialTheme
                                    .colorScheme.surface,
                                labelColor = MaterialTheme
                                    .colorScheme.onSurface
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = !isCustomEntered &&
                                        selectedMinutes == minutes,
                                borderColor = MaterialTheme.colorScheme
                                    .outlineVariant,
                                selectedBorderColor = FocusBlue
                            )
                        )
                    }
                }
            }

            OutlinedTextField(
                value = customMinutes,
                onValueChange = { value ->
                    if (value.length <= 3 &&
                        value.all { it.isDigit() }
                    ) {
                        customMinutes = value
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Custom duration (minutes)")
                },
                placeholder = {
                    Text("Enter 1–180 minutes")
                },
                supportingText = {
                    Text(
                        if (isCustomEntered && !isValidDuration) {
                            "Enter a value between 1 and 180."
                        } else {
                            "Set your own focus duration."
                        }
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = isCustomEntered && !isValidDuration,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FocusBlue,
                    focusedLabelColor = FocusBlue,
                    cursorColor = FocusBlue
                )
            )

            Button(
                onClick = {
                    durationToStart?.let(onStartFocus)
                },
                enabled = isValidDuration,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FocusBlue,
                    contentColor = Color.White,
                    disabledContainerColor = FocusBlue.copy(alpha = 0.35f),
                    disabledContentColor = Color.White.copy(alpha = 0.8f)
                )
            ) {
                Text(
                    text = "Start ${durationToStart ?: selectedMinutes}-Min Focus",
                    modifier = Modifier.padding(vertical = 6.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
