package com.example.focuswellbeing.utils.usage

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import java.util.Calendar
data class DeviceWellbeingStats(
    val totalScreenTimeMillis: Long,
    val pickupCount: Int
)

fun getTodayWellbeingStats(context: Context): DeviceWellbeingStats {
    val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }
    val startTime = calendar.timeInMillis
    val endTime = System.currentTimeMillis()

    val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
    val event = UsageEvents.Event()

    var pickups = 0
    var totalScreenTime = 0L
    var lastResumeTimestamp = 0L

    while (usageEvents.hasNextEvent()) {
        usageEvents.getNextEvent(event)

        when (event.eventType) {
            // App came to the foreground (started use)
            UsageEvents.Event.ACTIVITY_RESUMED -> {
                lastResumeTimestamp = event.timeStamp
                // Count a pickup if it's a launcher or standard user interaction
                pickups++
            }
            // App went to the background (stopped use)
            UsageEvents.Event.ACTIVITY_PAUSED -> {
                if (lastResumeTimestamp > 0) {
                    val duration = event.timeStamp - lastResumeTimestamp
                    if (duration > 0 && duration < 12 * 60 * 60 * 1000) { // filter anomalies
                        totalScreenTime += duration
                    }
                    lastResumeTimestamp = 0L
                }
            }
        }
    }

    return DeviceWellbeingStats(
        totalScreenTimeMillis = totalScreenTime,
        pickupCount = pickups / 2 // Approximate unique device unlocks/sessions
    )
}