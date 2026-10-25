package com.example.focuswellbeing.utils

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object AppMonitoringUtils {

    /**
     * Instantly queries live foreground transition events using queryEvents()
     */
    fun getCurrentForegroundApp(context: Context): String? {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            ?: return null

        val endTime = System.currentTimeMillis()
        // Look far enough back that a long-running foreground app is still detected
        val beginTime = endTime - 1000L * 60 * 60 * 2

        val usageEvents = usageStatsManager.queryEvents(beginTime, endTime)
        val event = UsageEvents.Event()

        var lastForegroundPkg: String? = null

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            val isForeground = event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                    event.eventType == UsageEvents.Event.ACTIVITY_RESUMED)
            if (isForeground) {
                lastForegroundPkg = event.packageName
            }
        }

        // Exclude your own app from blocking itself
        if (lastForegroundPkg == context.packageName) {
            return null
        }

        return lastForegroundPkg
    }

    fun monitorFrozenApps(context: Context, frozenApps: Set<String>): Flow<String?> = flow {
        if (frozenApps.isEmpty()) {
            emit(null)
            return@flow
        }

        while (true) {
            val activePackage = getCurrentForegroundApp(context)
            if (activePackage != null && frozenApps.contains(activePackage)) {
                emit(activePackage)
            } else {
                emit(null)
            }
            delay(500L)
        }
    }
}
