package com.example.focuswellbeing.features.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focuswellbeing.features.dashboard.presentation.viewModel.AnalyticsUiState
import com.example.focuswellbeing.features.dashboard.presentation.viewModel.WellbeingViewModel
import com.example.focuswellbeing.ui.theme.DividerSubtle
import com.example.focuswellbeing.ui.theme.FocusBlue
import com.example.focuswellbeing.ui.theme.FocusBlueLight
import com.example.focuswellbeing.ui.theme.ProgressTrack

@Composable
fun TopUsedApplication(state: AnalyticsUiState, viewModel: WellbeingViewModel) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }

    val displayedApps = if (isExpanded) {
        state.topUsedApps
    } else {
        state.topUsedApps.take(5)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(
                alpha = 0.75f
            )
        ),
        border = BorderStroke(1.dp, DividerSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Top Used Applications",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Active Usage",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            displayedApps.forEach { app ->
                val isFrozen = state.frozenAppsMap.contains(app.packageName)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isFrozen) FocusBlue.copy(alpha = 0.12f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        )
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isFrozen) FocusBlue.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isFrozen) {
                                    Text("❄️", fontSize = 16.sp)
                                } else if (app.appIconBitmap != null) {
                                    Image(
                                        bitmap = app.appIconBitmap,
                                        contentDescription = app.appName,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    Text(app.iconEmoji, fontSize = 16.sp)
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = app.appName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 13.sp
                                    )
                                    if (isFrozen) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = FocusBlue.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "FROZEN",
                                                modifier = Modifier.padding(
                                                    horizontal = 4.dp,
                                                    vertical = 1.dp
                                                ),
                                                fontSize = 7.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = FocusBlueLight
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = if (isFrozen) "App locked & suspended" else app.formattedTime,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isFrozen) FocusBlueLight else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Freeze / Unfreeze Button
                        Button(
                            onClick = { viewModel.toggleFreezeApp(context, app.packageName) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFrozen) FocusBlue.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                                contentColor = if (isFrozen) FocusBlueLight else FocusBlue
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(28.dp),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (isFrozen) "Unfreeze" else "Freeze",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                    // 👉 Progress bar color matches FocusBlueLight when frozen
                    LinearProgressIndicator(
                        progress = { (app.percentage / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = app.accentColor, // Always uses the app's original accent color
                        trackColor = ProgressTrack
                    )
                }
            }

            // Show More / Show Less CTA Button
            if (state.topUsedApps.size > 5) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(
                        onClick = { isExpanded = !isExpanded },
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        Text(
                            text = if (isExpanded) "Show Less" else "Show More (${state.topUsedApps.size - 5} more)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}