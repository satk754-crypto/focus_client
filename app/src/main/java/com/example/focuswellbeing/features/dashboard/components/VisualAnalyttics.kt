package com.example.focuswellbeing.features.dashboard.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focuswellbeing.features.dashboard.presentation.viewModel.AnalyticsUiState
import com.example.focuswellbeing.ui.theme.*;

@Composable
fun VisualAnalytics(state: AnalyticsUiState) {
    val maxVal = state.chartData.maxOfOrNull { it.hours } ?: 1f
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)
        ),
        border = BorderStroke(1.dp, DividerSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.primaryMetricTitle.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = state.primaryMetricValue,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Trend Pill
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = SuccessGreen.copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.2f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("📉", fontSize = 10.sp)
                            Text(
                                text = state.trendDescription,
                                style = MaterialTheme.typography.labelSmall,
                                color = SuccessGreenLight,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = DividerSubtle)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Visual Breakdown & Trends",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = when (state.selectedTab) {
                            0 -> "DAILY"
                            1 -> "WEEKLY"
                            else -> "MONTHLY"
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animated bar chart — shared chart area so all bars share one baseline
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                state.chartData.forEachIndexed { idx, item ->
                    val heightFraction = (item.hours / maxVal).coerceIn(0.15f, 1f)
                    val animatedHeight by animateFloatAsState(
                        targetValue = heightFraction,
                        animationSpec = tween(durationMillis = 600),
                        label = "barAnim"
                    )
                    val isSelectedBar = idx == 3 || idx == state.chartData.size - 1

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = item.formattedTime.replace(" ", ""),
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            softWrap = false,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(18.dp)
                                    .fillMaxHeight(animatedHeight)
                                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                    .background(
                                        if (isSelectedBar) {
                                            Brush.verticalGradient(
                                                listOf(IndigoLight, IndigoPrimary)
                                            )
                                        } else {
                                            Brush.verticalGradient(
                                                listOf(
                                                    IndigoPrimary.copy(alpha = 0.7f),
                                                    IndigoDark.copy(alpha = 0.7f)
                                                )
                                            )
                                        }
                                    )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            softWrap = false,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
