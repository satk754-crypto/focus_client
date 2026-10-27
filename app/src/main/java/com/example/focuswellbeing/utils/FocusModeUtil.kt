package com.example.focuswellbeing.utils

import android.content.Context

object FocusModeUtil {

    private const val PREFS_NAME = "focus_mode_prefs"
    private const val KEY_IS_FOCUS_ACTIVE = "is_focus_active"

    fun isFocusActive(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_FOCUS_ACTIVE, false)
    }

    fun startDeepFocus(context: Context, appPackageNames: List<String>) {
        val appContext = context.applicationContext
        val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        prefs.edit().apply {
            putBoolean(KEY_IS_FOCUS_ACTIVE, true)
            apply()
        }

        // Freeze all targeted apps
        for (packageName in appPackageNames) {
            if (!FrozenAppsStore.isFrozen(appContext, packageName)) {
                FrozenAppsStore.toggle(appContext, packageName)
            }
        }
    }

    fun stopDeepFocus(context: Context) {
        val appContext = context.applicationContext
        val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        prefs.edit().apply {
            putBoolean(KEY_IS_FOCUS_ACTIVE, false)
            apply()
        }

        val currentlyFrozen = FrozenAppsStore.getFrozenApps(appContext)
        for (packageName in currentlyFrozen) {
            FrozenAppsStore.toggle(appContext, packageName)
        }
    }
}