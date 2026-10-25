package com.example.focuswellbeing.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.example.focuswellbeing.features.dashboard.presentation.ui.ShieldActivity
import com.example.focuswellbeing.utils.FrozenAppsStore

class AppBlockerService : AccessibilityService() {

    private var lastBlockedPackage: String? = null
    private var lastBlockedAtMs: Long = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val packageName = event.packageName?.toString() ?: return
        // Never block our own UI (dashboard / shield)
        if (packageName == applicationContext.packageName) return
        if (packageName == "com.android.systemui") return
        if (packageName.contains("launcher", ignoreCase = true)) return

        if (!FrozenAppsStore.isFrozen(applicationContext, packageName)) return

        val now = System.currentTimeMillis()
        if (packageName == lastBlockedPackage && now - lastBlockedAtMs < 1_500L) return
        lastBlockedPackage = packageName
        lastBlockedAtMs = now

        val intent = Intent(this, ShieldActivity::class.java).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            )
            putExtra(ShieldActivity.EXTRA_BLOCKED_PACKAGE, packageName)
        }
        startActivity(intent)
    }

    override fun onInterrupt() = Unit
}
