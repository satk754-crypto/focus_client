package com.example.focuswellbeing.features.dashboard.presentation.viewModel

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focuswellbeing.features.dashboard.domain.WellBeingUseCase
import com.example.focuswellbeing.utils.AccessibilityPermission
import com.example.focuswellbeing.utils.FrozenAppsStore
import com.example.focuswellbeing.utils.usage.UsageActivityPermission
import com.example.focuswellbeing.utils.usage.queryAppForegroundUsage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WellbeingViewModel : ViewModel() {

    val accessibilityPermission = UsageActivityPermission()
    private val wellBeingUseCase = WellBeingUseCase()

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    private var selectedDailyAnchorMillis: Long = System.currentTimeMillis()
    private var selectedWeeklyOffset: Int = 0

    fun checkPermissionAndFetchData(context: Context) {
        val appContext = context.applicationContext
        val granted = accessibilityPermission.hasUsageStatsPermission(appContext)
        _uiState.update {
            it.copy(
                hasPermission = granted,
                frozenAppsMap = FrozenAppsStore.getFrozenApps(appContext)
            )
        }
        loadAnalyticsForTab(appContext, _uiState.value.selectedTab)
    }

    fun syncFrozenApps(context: Context) {
        _uiState.update {
            it.copy(frozenAppsMap = FrozenAppsStore.getFrozenApps(context.applicationContext))
        }
    }

    fun setTab(context: Context, tabIndex: Int) {
        val clampedTab = tabIndex.coerceIn(0, 1) // Restricts to Daily (0) and Weekly (1)
        _uiState.update { it.copy(selectedTab = clampedTab) }
        loadAnalyticsForTab(context.applicationContext, clampedTab)
    }

    fun updateDailyAnchor(context: Context, millis: Long) {
        if (millis <= 0) return
        selectedDailyAnchorMillis = millis
        loadAnalyticsForTab(context.applicationContext, 0)
    }

    fun shiftWeeklyOffset(context: Context, delta: Int) {
        selectedWeeklyOffset += delta
        loadAnalyticsForTab(context.applicationContext, 1)
    }

    fun openUsageAccessSettings(context: Context) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    private fun loadAnalyticsForTab(context: Context, tabIndex: Int) {
        viewModelScope.launch {
            try {
                val appContext = context.applicationContext
                val hasPermission = accessibilityPermission.hasUsageStatsPermission(appContext)

                val endTime = runCatching {
                    wellBeingUseCase.calculateRangeEndMillis(
                        tabIndex, selectedDailyAnchorMillis, selectedWeeklyOffset, 0
                    )
                }.getOrDefault(System.currentTimeMillis())

                val startTime = runCatching {
                    wellBeingUseCase.calculateRangeStartMillis(
                        tabIndex, selectedDailyAnchorMillis, selectedWeeklyOffset, 0
                    )
                }.getOrDefault(System.currentTimeMillis() - 86400000L)

                val usageByPackage: Map<String, Long> = withContext(Dispatchers.IO) {
                    if (!hasPermission || startTime >= endTime || startTime < 0) {
                        emptyMap()
                    } else {
                        runCatching {
                            queryAppForegroundUsage(appContext, startTime, endTime)
                        }.getOrDefault(emptyMap())
                    }
                }

                val topAppsList = withContext(Dispatchers.IO) {
                    runCatching {
                        wellBeingUseCase.buildTopUsedApps(appContext.packageManager, usageByPackage)
                    }.getOrDefault(emptyList())
                }

                val chartBars = withContext(Dispatchers.IO) {
                    runCatching {
                        wellBeingUseCase.generateDynamicChartBars(
                            appContext, tabIndex, hasPermission,
                            selectedDailyAnchorMillis, selectedWeeklyOffset, 0
                        )
                    }.getOrDefault(emptyList())
                }

                val metrics = runCatching {
                    wellBeingUseCase.calculateDynamicMetrics(tabIndex, chartBars, usageByPackage)
                }.getOrNull()

                _uiState.update {
                    it.copy(
                        hasPermission = hasPermission,
                        selectedTab = tabIndex,
                        primaryMetricTitle = metrics?.first ?: "",
                        primaryMetricValue = metrics?.second ?: "",
                        trendDescription = metrics?.third ?: "",
                        chartData = chartBars,
                        topUsedApps = topAppsList,
                        focusScore = metrics?.fourth ?: 0
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleFreezeApp(context: Context, packageName: String) {
        val appContext = context.applicationContext
        val wasFrozen = FrozenAppsStore.isFrozen(appContext, packageName)
        val updatedSet = FrozenAppsStore.toggle(appContext, packageName)
        _uiState.update { it.copy(frozenAppsMap = updatedSet) }

        if (!wasFrozen && updatedSet.contains(packageName) &&
            !AccessibilityPermission.isAppBlockerEnabled(appContext)
        ) {
            AccessibilityPermission.openAccessibilitySettings(appContext)
        }
    }
}