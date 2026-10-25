package com.example.focuswellbeing.features.dashboard.presentation.viewModel

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Process
import android.provider.Settings
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focuswellbeing.features.dashboard.model.AppUsageItemUi
import com.example.focuswellbeing.utils.AccessibilityPermission
import com.example.focuswellbeing.utils.FrozenAppsStore
import com.example.focuswellbeing.utils.usage.queryAppForegroundUsage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

data class ChartBarModel(
    val label: String,
    val hours: Float,
    val formattedTime: String
)


fun Drawable.toComposeImageBitmap(width: Int = 120, height: Int = 120): ImageBitmap {
    if (this is BitmapDrawable && this.bitmap != null) {
        return this.bitmap.asImageBitmap()
    }
    val bmpWidth = if (intrinsicWidth > 0) intrinsicWidth else width
    val bmpHeight = if (intrinsicHeight > 0) intrinsicHeight else height
    val bitmap = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap.asImageBitmap()
}

data class AnalyticsUiState(
    val hasPermission: Boolean = false,
    val selectedTab: Int = 0,
    val primaryMetricTitle: String = "Daily Average Screen Time",
    val primaryMetricValue: String = "3h 48m",
    val trendDescription: String = "-14% vs previous week",
    val chartData: List<ChartBarModel> = emptyList(),
    val topUsedApps: List<AppUsageItemUi> = emptyList(),
    val focusScore: Int = 89,
    val frozenAppsMap: Set<String> = emptySet() // Stores
)

class WellbeingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    fun checkPermissionAndFetchData(context: Context) {
        val appContext = context.applicationContext
        val granted = hasUsageStatsPermission(appContext)
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
        _uiState.update { it.copy(selectedTab = tabIndex) }
        loadAnalyticsForTab(context.applicationContext, tabIndex)
    }

    fun openUsageAccessSettings(context: Context) {
        context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
    }

    private fun hasUsageStatsPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun loadAnalyticsForTab(context: Context, tabIndex: Int) {
        viewModelScope.launch {
            val hasPermission = hasUsageStatsPermission(context)
            val endTime = System.currentTimeMillis()
            val startTime = rangeStartMillis(tabIndex)

            val usageByPackage: Map<String, Long> = withContext(Dispatchers.IO) {
                if (!hasPermission) emptyMap()
                else queryAppForegroundUsage(context, startTime, endTime)
            }

            val topAppsList = withContext(Dispatchers.IO) {
                buildTopUsedApps(context.packageManager, usageByPackage)
            }

            val chartBars = when (tabIndex) {
                0 -> listOf(
                    ChartBarModel("Mon", 4.2f, "4h 12m"),
                    ChartBarModel("Tue", 5.5f, "5h 30m"),
                    ChartBarModel("Wed", 3.1f, "3h 06m"),
                    ChartBarModel("Thu", 4.8f, "4h 48m"),
                    ChartBarModel("Fri", 3.5f, "3h 30m"),
                    ChartBarModel("Sat", 2.1f, "2h 06m"),
                    ChartBarModel("Sun", 3.4f, "3h 24m")
                )

                1 -> listOf(
                    ChartBarModel("W 1", 31.0f, "31h 00m"),
                    ChartBarModel("W 2", 29.5f, "29h 30m"),
                    ChartBarModel("W 3", 28.2f, "28h 12m"),
                    ChartBarModel("W 4", 26.4f, "26h 40m")
                )

                else -> listOf(
                    ChartBarModel("Jan", 135f, "135h"),
                    ChartBarModel("Feb", 122f, "122h"),
                    ChartBarModel("Mar", 115f, "115h"),
                    ChartBarModel("Apr", 108f, "108h")
                )
            }

            val (metricTitle, metricVal, trendText, score) = when (tabIndex) {
                0 -> Quadruple("Daily Average Screen Time", "3h 48m", "-14% vs previous week", 89)
                1 -> Quadruple("Weekly Total Screen Time", "26h 40m", "-18% vs previous week", 92)
                else -> Quadruple("Monthly Total Screen Time", "108h 15m", "-22% vs last month", 94)
            }

            _uiState.update {
                it.copy(
                    hasPermission = hasPermission,
                    selectedTab = tabIndex,
                    primaryMetricTitle = metricTitle,
                    primaryMetricValue = metricVal,
                    trendDescription = trendText,
                    chartData = chartBars,
                    topUsedApps = topAppsList,
                    focusScore = score
                )
            }
        }
    }

    private fun rangeStartMillis(tabIndex: Int): Long {
        val calendar = Calendar.getInstance()
        when (tabIndex) {
            0 -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.add(Calendar.DAY_OF_YEAR, -6)
            }

            1 -> calendar.add(Calendar.WEEK_OF_YEAR, -4)
            else -> calendar.add(Calendar.MONTH, -4)
        }
        return calendar.timeInMillis
    }

    private fun buildTopUsedApps(
        pm: PackageManager,
        usageByPackage: Map<String, Long>
    ): List<AppUsageItemUi> {
        fun loadInstalledIcon(pkg: String): Pair<Drawable?, ImageBitmap?> = try {
            val drawable = pm.getApplicationIcon(pkg)
            drawable to drawable.toComposeImageBitmap()
        } catch (_: Exception) {
            null to null
        }

        fun loadInstalledAppName(pkg: String, fallback: String): String = try {
            val appInfo = pm.getApplicationInfo(pkg, 0)
            pm.getApplicationLabel(appInfo).toString()
        } catch (_: Exception) {
            fallback
        }

        val topUsage = usageByPackage.entries
            .sortedByDescending { it.value }
            .take(TOP_USED_APPS_LIMIT)

        if (topUsage.isEmpty()) return emptyList()

        val totalTopMillis = topUsage.sumOf { it.value }.coerceAtLeast(1L)
        val palette = listOf(
            Triple(
                listOf(Color(0xFFF59E0B), Color(0xFFEC4899), Color(0xFF9333EA)),
                Color(0xFFF472B6),
                "📱"
            ),
            Triple(listOf(Color(0xFFDC2626), Color(0xFFF43F5E)), Color(0xFFF87171), "▶️"),
            Triple(listOf(Color(0xFF06B6D4), Color(0xFFEC4899)), Color(0xFF22D3EE), "🎵"),
            Triple(listOf(Color(0xFF059669), Color(0xFF2DD4BF)), Color(0xFF34D399), "💬"),
            Triple(listOf(Color(0xFF2563EB), Color(0xFF38BDF8)), Color(0xFF38BDF8), "🌐")
        )

        return topUsage.mapIndexed { index, (pkgName, fgMillis) ->
            val (drawable, bitmap) = loadInstalledIcon(pkgName)
            val resolvedName = loadInstalledAppName(pkgName, pkgName.substringAfterLast('.'))
            val hours = fgMillis / (1000 * 60 * 60)
            val minutes = (fgMillis / (1000 * 60)) % 60
            val formatted = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
            val pct = ((fgMillis * 100f) / totalTopMillis).toInt().coerceIn(1, 100)
            val (grad, accent, emoji) = palette[index % palette.size]

            AppUsageItemUi(
                packageName = pkgName,
                appName = resolvedName,
                packageSubtitle = pkgName,
                iconEmoji = emoji,
                appIconDrawable = drawable,
                appIconBitmap = bitmap,
                brandGradient = grad,
                accentColor = accent,
                formattedTime = formatted,
                percentage = pct,
                isShieldActive = index == 0 || index == 2
            )
        }
    }
    /**
     * Freezes or unfreezes [packageName]. When freezing, prompts for the
     * Accessibility service if it is not enabled — that service is required
     * to block the app once the user leaves FocusWellbeing.
     */
    fun toggleFreezeApp(context: Context, packageName: String) {
        val appContext = context.applicationContext
        val wasFrozen = FrozenAppsStore.isFrozen(appContext, packageName)
        val updatedSet = FrozenAppsStore.toggle(appContext, packageName)
        _uiState.update { it.copy(frozenAppsMap = updatedSet) }

        // Freezing requires the blocker service; guide the user to enable it
        if (!wasFrozen && updatedSet.contains(packageName) &&
            !AccessibilityPermission.isAppBlockerEnabled(appContext)
        ) {
            AccessibilityPermission.openAccessibilitySettings(appContext)
        }
    }
}

private const val TOP_USED_APPS_LIMIT = 30

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
