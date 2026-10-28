package com.example.focuswellbeing.features.dashboard.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun DashboardDateFilterWidget(
    selectedTab: Int,
    formattedDailyDate: String,
    onPickDateClick: () -> Unit,
    selectedMonthOffset: Int,
    onMonthOffsetChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        when (selectedTab) {
            0 -> {
                // Daily View: Calendar Date Picker Button
                OutlinedButton(
                    onClick = onPickDateClick,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Pick Date",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(formattedDailyDate, fontSize = 12.sp)
                }
            }
            else -> {
                // Weekly View: Month Shift Switcher
                val targetCalendar = Calendar.getInstance().apply { add(Calendar.MONTH, selectedMonthOffset) }
                val monthLabel = SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(targetCalendar.time)

                val currentCalendar = Calendar.getInstance()
                val canGoForward = targetCalendar.get(Calendar.YEAR) < currentCalendar.get(Calendar.YEAR) ||
                        (targetCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                                targetCalendar.get(Calendar.MONTH) < currentCalendar.get(Calendar.MONTH))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    IconButton(
                        onClick = { onMonthOffsetChanged(-1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous", modifier = Modifier.size(18.dp))
                    }

                    Text(monthLabel, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)

                    IconButton(
                        onClick = { if (canGoForward) onMonthOffsetChanged(1) },
                        enabled = canGoForward,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Next",
                            modifier = Modifier.size(18.dp),
                            tint = if (canGoForward) LocalContentColor.current else LocalContentColor.current.copy(alpha = 0.38f)
                        )
                    }
                }
            }
        }
    }
}