package com.example.focuswellbeing.utils.usage

import android.app.usage.UsageStatsManager
import android.content.Context

fun getCurrentForegroundApp(context: Context): String? {
    val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager ?: return null
    val endTime = System.currentTimeMillis()
    val beginTime = endTime - 10000 * 60 // last 10 minutes

    val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginTime, endTime)
    if (stats.isNullOrEmpty()) return null

    // Sort by last time used to find the most recent foreground app
    val recentStat = stats.maxByOrNull { it.lastTimeUsed }
    return recentStat?.packageName
}