//package com.example.focuswellbeing.features.dashboard.ui
//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.runtime.Composable
//
//import androidx.compose.animation.core.animateFloatAsState
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontFamily
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.focuswellbeing.features.dashboard.ui.presentation.viewModel.WellbeingViewModel
//
//// ScreenTimeAnalyticsScreen.kt (Top Used Apps with Brand Icons)
//@Composable
//fun TopUsedAppRow(app: AppUsageItemUi) {
//    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                // Branded App Icon Badge
//                Box(
//                    modifier = Modifier
//                        .size(40.dp)
//                        .clip(RoundedCornerShape(12.dp))
//                        .background(Brush.linearGradient(app.brandGradient)),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        imageVector = app.iconVector,
//                        contentDescription = app.appName,
//                        tint = Color.White,
//                        modifier = Modifier.size(20.dp)
//                    )
//                }
//                Column {
//                    Text(
//                        text = app.appName,
//                        style = MaterialTheme.typography.bodyMedium,
//                        fontWeight = FontWeight.Bold,
//                        color = Color.White
//                    )
//                    Text(
//                        text = app.packageSubtitle,
//                        style = MaterialTheme.typography.labelSmall,
//                        color = Color(0xFF94A3B8)
//                    )
//                }
//            }
//            Column(horizontalAlignment = Alignment.End) {
//                Text(
//                    text = app.formattedTime,
//                    style = MaterialTheme.typography.bodyMedium,
//                    fontWeight = FontWeight.ExtraBold,
//                    color = Color.White
//                )
//                Text(
//                    text = "${app.percentage}% of total",
//                    style = MaterialTheme.typography.labelSmall,
//                    fontWeight = FontWeight.SemiBold,
//                    color = app.accentColor
//                )
//            }
//        }
//        // Smooth Brand-Matched Progress Bar
//        LinearProgressIndicator(
//            progress = { app.percentage / 100f },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(6.dp)
//                .clip(RoundedCornerShape(3.dp)),
//            color = app.accentColor,
//            trackColor = Color(0xFF0F172A)
//        )
//    }
//}