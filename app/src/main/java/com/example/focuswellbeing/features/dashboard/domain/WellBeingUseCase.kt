package com.example.focuswellbeing.features.dashboard.domain

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.focuswellbeing.features.dashboard.model.AppUsageItemUi
import com.example.focuswellbeing.features.dashboard.presentation.viewModel.ChartBarModel
import com.example.focuswellbeing.ui.theme.AppAmber
import com.example.focuswellbeing.ui.theme.AppBlue
import com.example.focuswellbeing.ui.theme.AppCyan
import com.example.focuswellbeing.ui.theme.AppCyanLight
import com.example.focuswellbeing.ui.theme.AppEmerald
import com.example.focuswellbeing.ui.theme.AppPink
import com.example.focuswellbeing.ui.theme.AppPinkLight
import com.example.focuswellbeing.ui.theme.AppPurple
import com.example.focuswellbeing.ui.theme.AppRed
import com.example.focuswellbeing.ui.theme.AppRose
import com.example.focuswellbeing.ui.theme.AppRoseLight
import com.example.focuswellbeing.ui.theme.AppTeal
import com.example.focuswellbeing.ui.theme.FocusBlueLight
import com.example.focuswellbeing.ui.theme.SuccessGreenLight
import com.example.focuswellbeing.utils.usage.queryAppForegroundUsage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

class WellBeingUseCase {

    companion object {
        private const val TOP_USED_APPS_LIMIT = 30
    }

    /**
     * Calculates the start time in milliseconds for data queries based on the active tab and navigation offsets.
     *
     * @param tabIndex Current active tab (0: Daily, 1: Weekly, 2: Yearly).
     * @param dailyAnchorMillis Anchor timestamp for the daily calendar view.
     * @param weeklyOffset Month offset for viewing historical weekly charts.
     * @param yearlyOffset Year offset for viewing historical annual charts.
     * @return Start timestamp in epoch milliseconds.
     */
    fun calculateRangeStartMillis(
        tabIndex: Int,
        dailyAnchorMillis: Long,
        weeklyOffset: Int,
        yearlyOffset: Int
    ): Long {
        val calendar = Calendar.getInstance()
        try {
            when (tabIndex) {
                0 -> {
                    calendar.timeInMillis = dailyAnchorMillis
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    calendar.add(Calendar.DAY_OF_YEAR, -6)
                }
                1 -> {
                    calendar.add(Calendar.MONTH, weeklyOffset)
                    calendar.set(Calendar.DAY_OF_MONTH, 1)
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                }
                else -> {
                    calendar.add(Calendar.YEAR, yearlyOffset)
                    calendar.set(Calendar.MONTH, Calendar.JANUARY)
                    calendar.set(Calendar.DAY_OF_MONTH, 1)
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return calendar.timeInMillis
    }

    /**
     * Calculates the end time in milliseconds for data queries based on the active tab and navigation offsets.
     *
     * @param tabIndex Current active tab (0: Daily, 1: Weekly, 2: Yearly).
     * @param dailyAnchorMillis Anchor timestamp for the daily calendar view.
     * @param weeklyOffset Month offset for viewing historical weekly charts.
     * @param yearlyOffset Year offset for viewing historical annual charts.
     * @return End timestamp in epoch milliseconds.
     */
    fun calculateRangeEndMillis(
        tabIndex: Int,
        dailyAnchorMillis: Long,
        weeklyOffset: Int,
        yearlyOffset: Int
    ): Long {
        val calendar = Calendar.getInstance()
        try {
            when (tabIndex) {
                0 -> {
                    calendar.timeInMillis = dailyAnchorMillis
                    calendar.set(Calendar.HOUR_OF_DAY, 23)
                    calendar.set(Calendar.MINUTE, 59)
                    calendar.set(Calendar.SECOND, 59)
                    calendar.set(Calendar.MILLISECOND, 999)
                }
                1 -> {
                    calendar.add(Calendar.MONTH, weeklyOffset)
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                    calendar.set(Calendar.HOUR_OF_DAY, 23)
                    calendar.set(Calendar.MINUTE, 59)
                    calendar.set(Calendar.SECOND, 59)
                    calendar.set(Calendar.MILLISECOND, 999)
                }
                else -> {
                    calendar.add(Calendar.YEAR, yearlyOffset)
                    calendar.set(Calendar.MONTH, Calendar.DECEMBER)
                    calendar.set(Calendar.DAY_OF_MONTH, 31)
                    calendar.set(Calendar.HOUR_OF_DAY, 23)
                    calendar.set(Calendar.MINUTE, 59)
                    calendar.set(Calendar.SECOND, 59)
                    calendar.set(Calendar.MILLISECOND, 999)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return calendar.timeInMillis
    }

    /**
     * Generates dynamic chart bar models (Daily, Weekly, or Yearly breakdown) based on usage queries.
     *
     * @param context Application context to query UsageStats.
     * @param tabIndex Active dashboard tab.
     * @param hasPermission Whether usage permissions are granted.
     * @param dailyAnchorMillis Selected anchor date for daily tab.
     * @param weeklyOffset Selected month offset for weekly tab.
     * @param yearlyOffset Selected year offset for yearly tab.
     * @return List of [ChartBarModel] ready to render in analytics charts.
     */
    suspend fun generateDynamicChartBars(
        context: Context,
        tabIndex: Int,
        hasPermission: Boolean,
        dailyAnchorMillis: Long,
        weeklyOffset: Int,
        yearlyOffset: Int
    ): List<ChartBarModel> {
        if (!hasPermission) return emptyList()

        return try {
            when (tabIndex) {
                // Daily View: Past 7 days leading up to anchor
                0 -> {
                    (6 downTo 0).map { daysAgo ->
                        val cal = Calendar.getInstance().apply {
                            timeInMillis = dailyAnchorMillis
                            add(Calendar.DAY_OF_YEAR, -daysAgo)
                        }
                        val dayStart = (cal.clone() as Calendar).apply {
                            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                        }
                        val dayEnd = (dayStart.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }

                        val dailyUsage = queryAppForegroundUsage(context, dayStart.timeInMillis, dayEnd.timeInMillis)
                        val totalMillis = dailyUsage.values.sum()
                        val hours = totalMillis / (1000f * 60f * 60f)
                        val label = SimpleDateFormat("E", Locale.getDefault()).format(cal.time)
                        val h = (totalMillis / (1000 * 60 * 60)).toInt()
                        val m = ((totalMillis / (1000 * 60)) % 60).toInt()

                        ChartBarModel(label, hours, if (h > 0) "${h}h ${m}m" else "${m}m")
                    }
                }
                // Weekly View: Weeks safely chunked within the target month
                1 -> {
                    val cal = Calendar.getInstance().apply {
                        add(Calendar.MONTH, weeklyOffset)
                    }
                    val targetMonth = cal.get(Calendar.MONTH)
                    val targetYear = cal.get(Calendar.YEAR)

                    val weekCal = Calendar.getInstance().apply {
                        clear()
                        set(targetYear, targetMonth, 1)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }

                    val weeksList = mutableListOf<ChartBarModel>()
                    for (w in 1..4) {
                        val weekStart = weekCal.clone() as Calendar
                        weekCal.add(Calendar.WEEK_OF_MONTH, 1)
                        val weekEnd = weekCal.clone() as Calendar

                        val weeklyUsage = queryAppForegroundUsage(context, weekStart.timeInMillis, weekEnd.timeInMillis)
                        val totalMillis = weeklyUsage.values.sum()
                        val hours = totalMillis / (1000f * 60f * 60f)
                        val h = (totalMillis / (1000 * 60 * 60)).toInt()
                        val m = ((totalMillis / (1000 * 60)) % 60).toInt()

                        weeksList.add(ChartBarModel("W $w", hours, if (h > 0) "${h}h ${m}m" else "${m}m"))
                    }
                    weeksList
                }
                // Yearly View: 12 individual months (Jan to Dec)
                else -> {
                    val targetYearCalendar = Calendar.getInstance().apply {
                        add(Calendar.YEAR, yearlyOffset)
                    }
                    val year = targetYearCalendar.get(Calendar.YEAR)

                    (0..11).map { monthIndex ->
                        val monthStart = Calendar.getInstance().apply {
                            clear()
                            set(year, monthIndex, 1, 0, 0, 0)
                        }
                        val monthEnd = (monthStart.clone() as Calendar).apply {
                            add(Calendar.MONTH, 1)
                        }

                        val monthlyUsage = queryAppForegroundUsage(context, monthStart.timeInMillis, monthEnd.timeInMillis)
                        val totalMillis = monthlyUsage.values.sum()
                        val hours = totalMillis / (1000f * 60f * 60f)
                        val label = SimpleDateFormat("MMM", Locale.getDefault()).format(monthStart.time)
                        val h = (totalMillis / (1000 * 60 * 60)).toInt()

                        ChartBarModel(label, hours, "${h}h")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Computes dashboard analytics metrics (titles, screen time totals, trends, and focus scores).
     */
    fun calculateDynamicMetrics(
        tabIndex: Int,
        chartBars: List<ChartBarModel>,
        usageByPackage: Map<String, Long>
    ): Quadruple<String, String, String, Int> {
        val totalMillis = usageByPackage.values.sum()
        val h = (totalMillis / (1000 * 60 * 60)).toInt()
        val m = ((totalMillis / (1000 * 60)) % 60).toInt()

        return when (tabIndex) {
            0 -> {
                val avgHours = if (chartBars.isNotEmpty()) chartBars.map { it.hours }.average().toFloat() else 0f
                val avgH = avgHours.toInt()
                val avgM = ((avgHours - avgH) * 60).toInt()
                Quadruple("Daily Average Screen Time", "${avgH}h ${avgM}m", "Selected Day Range", 89)
            }
            1 -> {
                Quadruple("Monthly Weeks Total", if (h > 0) "${h}h ${m}m" else "${m}m", "Selected Month breakdown", 92)
            }
            else -> {
                Quadruple("Full Year Screen Time", "${h}h", "Annual Overview", 94)
            }
        }
    }

    /**
     * Builds and formats the list of most used applications sorted by foreground time.
     */
    fun buildTopUsedApps(
        pm: PackageManager,
        usageByPackage: Map<String, Long>
    ): List<AppUsageItemUi> {
        fun Drawable.toImageBitmap(): ImageBitmap {
            val bitmap = if (this is BitmapDrawable && bitmap != null) {
                bitmap
            } else {
                val width = intrinsicWidth.coerceAtLeast(1)
                val height = intrinsicHeight.coerceAtLeast(1)
                Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also { bmp ->
                    val canvas = Canvas(bmp)
                    setBounds(0, 0, canvas.width, canvas.height)
                    draw(canvas)
                }
            }
            return bitmap.asImageBitmap()
        }

        fun loadInstalledIcon(pkg: String): Pair<Drawable?, ImageBitmap?> = try {
            val drawable = pm.getApplicationIcon(pkg)
            drawable to drawable.toImageBitmap()
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
            Triple(listOf(AppAmber, AppPink, AppPurple), AppPinkLight, "📱"),
            Triple(listOf(AppRed, AppRose), AppRoseLight, "▶️"),
            Triple(listOf(AppCyan, AppPink), AppCyanLight, "🎵"),
            Triple(listOf(AppEmerald, AppTeal), SuccessGreenLight, "💬"),
            Triple(listOf(AppBlue, FocusBlueLight), FocusBlueLight, "🌐")
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
}