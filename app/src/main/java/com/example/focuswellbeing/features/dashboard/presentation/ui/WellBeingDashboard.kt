package com.example.focuswellbeing.features.dashboard.presentation.ui

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.focuswellbeing.features.dashboard.components.ActivateFocusCard
import com.example.focuswellbeing.features.dashboard.components.VisualAnalytics
import com.example.focuswellbeing.features.dashboard.presentation.viewModel.WellbeingViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WellBeingDashboard(
    viewModel: WellbeingViewModel,
    context: Context,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val tabs = listOf("Daily", "Weekly")
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    // Date picker state dialog visibility & offset controls
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedMonthOffset by remember { mutableStateOf(0) }

    // Reload when returning from Usage Access settings (or any resume).
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkPermissionAndFetchData(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Material 3 Date Picker Dialog for Daily tab custom calendar selection (restricted up to today)
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val currentTime = System.currentTimeMillis()
                            val validMillis = if (millis > currentTime) currentTime else millis
                            viewModel.updateDailyAnchor(context, validMillis)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            // Material 3 Date Picker Dialog for Daily tab custom calendar selection (restricted up to today)
            if (showDatePicker) {
                // Use the current anchor millis from your view model/state as the initial selection
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = state.selectedDailyAnchorMillis, // <-- Update this line to use your current date state
                    yearRange = 2020..(Calendar.getInstance().get(Calendar.YEAR))
                )

                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    val currentTime = System.currentTimeMillis()
                                    val validMillis = if (millis > currentTime) currentTime else millis
                                    viewModel.updateDailyAnchor(context, validMillis)
                                }
                                showDatePicker = false
                            }
                        ) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics Suite", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Segmented Interactive Pill Tabs (Daily / Weekly)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    tabs.forEachIndexed { index, title ->
                        val isSelected = state.selectedTab == index
                        val tabBlue = Color(0xFF4F46E5)
                        val backgroundColor = if (isSelected) tabBlue else Color.Transparent
                        val textColor =
                            if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(backgroundColor)
                                .clickable { viewModel.setTab(context, index) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = textColor
                            )
                        }
                    }
                }
            }

            // Sub-Filter Header Controls (Calendar Picker or Week Offset Switcher with strict future restriction)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (state.selectedTab) {
                    0 -> {
                        OutlinedButton(
                            onClick = { showDatePicker = true },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Pick Date",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(state.formattedDailyDate, fontSize = 13.sp) // Clean and formatted in ViewModel!
                        }
                    }
                    else -> {
                        // Weekly view: Range selector switcher with strict month comparison
                        val targetCalendar = Calendar.getInstance().apply { add(Calendar.MONTH, selectedMonthOffset) }
                        val monthLabel = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(targetCalendar.time)

                        // Check if the target month is strictly before the current month/year
                        val currentCalendar = Calendar.getInstance()
                        val canGoForward = targetCalendar.get(Calendar.YEAR) < currentCalendar.get(Calendar.YEAR) ||
                                (targetCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                                        targetCalendar.get(Calendar.MONTH) < currentCalendar.get(Calendar.MONTH))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                selectedMonthOffset--
                                viewModel.shiftWeeklyOffset(context, -1)
                            }) {
                                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous Period")
                            }

                            Text(monthLabel, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)

                            IconButton(
                                onClick = {
                                    if (canGoForward) {
                                        selectedMonthOffset++
                                        viewModel.shiftWeeklyOffset(context, 1)
                                    }
                                },
                                enabled = canGoForward
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = "Next Period",
                                    tint = if (canGoForward) LocalContentColor.current else LocalContentColor.current.copy(alpha = 0.38f)
                                )
                            }
                        }
                    }
                }
            }

            VisualAnalytics(state)
            ActivateFocusCard(state, viewModel)
        }
    }
}