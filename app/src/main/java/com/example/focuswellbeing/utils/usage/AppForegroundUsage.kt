package com.example.focuswellbeing.utils.usage

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context

/**
 * Resolves per-package foreground time for a range.
 * Prefer aggregated stats; fall back to UsageEvents (more reliable on many OEM devices).
 */
fun queryAppForegroundUsage(
    context: Context,
    startTime: Long,
    endTime: Long
): Map<String, Long> {
    val appContext = context.applicationContext
    val usageStatsManager =
        appContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    val ownPackage = appContext.packageName
    val pm = appContext.packageManager

    val fromAggregate = usageStatsManager
        .queryAndAggregateUsageStats(startTime, endTime)
        .mapNotNull { (pkg, stats) ->
            val millis = stats.totalTimeInForeground
            if (millis > 0 && pkg != ownPackage) pkg to millis else null
        }
        .toMap()

    val raw = fromAggregate.ifEmpty {
        foregroundUsageFromEvents(usageStatsManager, startTime, endTime, ownPackage)
    }

    if (raw.isEmpty()) return emptyMap()

    val launchable = raw.filterKeys { pm.getLaunchIntentForPackage(it) != null }
    return launchable.ifEmpty { raw }
}

private fun foregroundUsageFromEvents(
    usageStatsManager: UsageStatsManager,
    startTime: Long,
    endTime: Long,
    ownPackage: String
): Map<String, Long> {
    val usage = mutableMapOf<String, Long>()
    val lastResume = mutableMapOf<String, Long>()
    val events = usageStatsManager.queryEvents(startTime, endTime)
    val event = UsageEvents.Event()

    while (events.hasNextEvent()) {
        events.getNextEvent(event)
        val pkg = event.packageName ?: continue
        if (pkg == ownPackage) continue

        // ACTIVITY_RESUMED == MOVE_TO_FOREGROUND (1), ACTIVITY_PAUSED == MOVE_TO_BACKGROUND (2)
        when (event.eventType) {
            UsageEvents.Event.ACTIVITY_RESUMED -> {
                lastResume[pkg] = event.timeStamp
            }
            UsageEvents.Event.ACTIVITY_PAUSED -> {
                val resumedAt = lastResume.remove(pkg) ?: continue
                val duration = event.timeStamp - resumedAt
                if (duration in 1 until MAX_SESSION_MILLIS) {
                    usage[pkg] = (usage[pkg] ?: 0L) + duration
                }
            }
        }
    }
    return usage
}

private const val MAX_SESSION_MILLIS = 12L * 60 * 60 * 1000
