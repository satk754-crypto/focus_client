package com.example.focuswellbeing.utils.usage

import android.app.usage.UsageStatsManager
import android.content.Context
import java.util.Calendar

data class AppUsageInfo(val packageName: String, val timeInForeground: Long)

fun getTodayAppUsage(context: Context): List<AppUsageInfo> {
    val usageStatsManager =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }
    val startTime = calendar.timeInMillis
    val endTime = System.currentTimeMillis()

    val statsMap = usageStatsManager.queryAndAggregateUsageStats(startTime, endTime)
    if (statsMap.isEmpty()) return emptyList()

    return statsMap.values
        .filter { it.totalTimeInForeground > 0 }
        .map {
            AppUsageInfo(
                packageName = it.packageName,
                timeInForeground = it.totalTimeInForeground
            )
        }
        .sortedByDescending { it.timeInForeground }
}
