package com.example.focuswellbeing.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.edit

object FocusModeUtil {

    private const val PREFS_NAME = "focus_mode_prefs"
    private const val KEY_IS_FOCUS_ACTIVE = "is_focus_active"
    private const val KEY_PRE_FOCUS_FROZEN = "pre_focus_frozen_packages"

    fun isFocusActive(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_FOCUS_ACTIVE, false)
    }

    /**
     * Freezes every launchable app (except this app / launcher / System UI)
     * for the duration of a deep-focus session.
     */
    fun startDeepFocus(context: Context) {
        val appContext = context.applicationContext
        val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        prefs.edit {
            putBoolean(KEY_IS_FOCUS_ACTIVE, true)
            remove(KEY_PRE_FOCUS_FROZEN)
        }

        val allLaunchable = getLaunchablePackageNames(appContext)
            .filterNot { it == appContext.packageName }
        FrozenAppsStore.setFrozenApps(appContext, allLaunchable.toSet())
    }

    /**
     * Ends focus mode and unfreezes every app (including top-used apps).
     */
    fun stopDeepFocus(context: Context) {
        val appContext = context.applicationContext
        val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        FrozenAppsStore.setFrozenApps(appContext, emptySet())

        prefs.edit {
            putBoolean(KEY_IS_FOCUS_ACTIVE, false)
            remove(KEY_PRE_FOCUS_FROZEN)
        }
    }

    fun getLaunchablePackageNames(context: Context): List<String> {
        val pm = context.packageManager
        val ownPackage = context.packageName
        val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfos = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(
                launcherIntent,
                PackageManager.ResolveInfoFlags.of(0L)
            )
        } else {
            @Suppress("DEPRECATION")
            pm.queryIntentActivities(launcherIntent, 0)
        }

        return resolveInfos
            .mapNotNull { it.activityInfo?.packageName }
            .distinct()
            .filter { pkg ->
                pkg != ownPackage &&
                    pkg != "com.android.systemui" &&
                    !pkg.contains("launcher", ignoreCase = true)
            }
    }
}
