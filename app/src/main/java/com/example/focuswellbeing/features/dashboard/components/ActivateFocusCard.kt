package com.example.focuswellbeing.features.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focuswellbeing.features.dashboard.presentation.viewModel.AnalyticsUiState
import com.example.focuswellbeing.features.dashboard.presentation.viewModel.WellbeingViewModel


@Composable
fun ActivateFocusCard(state: AnalyticsUiState, viewModel: WellbeingViewModel) {
    val context = LocalContext.current
    // Top Used Applications Breakdown Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(
                alpha = 0.75f
            )
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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

            // 👉 ADD / REPLACE THE LOOP HERE:
            state.topUsedApps.forEach { app ->
                val isFrozen = state.frozenAppsMap.contains(app.packageName)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isFrozen) Color(0xFF0EA5E9).copy(alpha = 0.12f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        )
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isFrozen) Color(0xFF0EA5E9).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isFrozen) {
                                Text("❄️", fontSize = 20.sp)
                            } else if (app.appIconBitmap != null) {
                                Image(
                                    bitmap = app.appIconBitmap,
                                    contentDescription = app.appName,
                                    modifier = Modifier.size(32.dp)
                                )
                            } else {
                                Text(app.iconEmoji, fontSize = 20.sp)
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
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (isFrozen) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFF0EA5E9).copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "FROZEN",
                                            modifier = Modifier.padding(
                                                horizontal = 4.dp,
                                                vertical = 2.dp
                                            ),
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF38BDF8)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = if (isFrozen) "App locked & suspended" else app.formattedTime,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isFrozen) Color(0xFF38BDF8) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Freeze / Unfreeze Button
                    Button(
                        onClick = { viewModel.toggleFreezeApp(context, app.packageName) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFrozen) Color(0xFF0EA5E9).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                            contentColor = if (isFrozen) Color(0xFF38BDF8) else Color(0xFF0EA5E9)
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isFrozen) "Unfreeze" else "Freeze",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}