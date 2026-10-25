package com.example.focuswellbeing.utils

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.AppOpsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import com.example.focuswellbeing.service.AppBlockerService

object AccessibilityPermission {
    fun isAppBlockerEnabled(context: Context): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
            ?: return false
        val enabled =
            am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        val expected = ComponentName(context, AppBlockerService::class.java).flattenToString()
        return enabled.any { info ->
            val id = info.resolveInfo?.serviceInfo?.let { si ->
                ComponentName(si.packageName, si.name).flattenToString()
            } ?: info.id
            id.equals(
                expected,
                ignoreCase = true
            ) || id.contains(AppBlockerService::class.java.name)
        }
    }

    fun openAccessibilitySettings(context: Context) {
        context.startActivity(
            Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }

}
