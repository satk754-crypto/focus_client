package com.example.focuswellbeing.features.dashboard.presentation.viewModel

import com.example.focuswellbeing.features.dashboard.model.AppUsageItemUi

data class AnalyticsUiState(
    val hasPermission: Boolean = false,
    val selectedTab: Int = 0,
    val primaryMetricTitle: String = "Daily Average Screen Time",
    val primaryMetricValue: String = "3h 48m",
    val trendDescription: String = "-14% vs previous week",
    val chartData: List<ChartBarModel> = emptyList(),
    val topUsedApps: List<AppUsageItemUi> = emptyList(),
    val focusScore: Int = 89,
    val frozenAppsMap: Set<String> = emptySet() ,
    val selectedDailyAnchorMillis: Long = System.currentTimeMillis(),
    val formattedDailyDate: String = ""
)

data class ChartBarModel(
    val label: String,
    val hours: Float,
    val formattedTime: String
)