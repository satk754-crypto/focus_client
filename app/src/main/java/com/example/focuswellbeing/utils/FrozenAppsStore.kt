package com.example.focuswellbeing.utils

import android.content.Context
import kotlinx.coroutines.flow.update

/**
 * Persists frozen package names so both the UI and [AppBlockerService]
 * (which runs outside the Activity) can read the same Focus Mode list.
 */
object FrozenAppsStore {
    private const val PREFS_NAME = "frozen_apps_prefs"
    private const val KEY_FROZEN = "frozen_packages"

    fun getFrozenApps(context: Context): Set<String> {
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_FROZEN, emptySet())?.toSet() ?: emptySet()
    }

    fun setFrozenApps(context: Context, packages: Set<String>) {
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putStringSet(KEY_FROZEN, packages.toSet()).apply()
    }

    fun toggle(context: Context, packageName: String): Set<String> {
        val updated = getFrozenApps(context).toMutableSet()
        if (!updated.add(packageName)) {
            updated.remove(packageName)
        }
        setFrozenApps(context, updated)
        return updated
    }

    fun unfreeze(context: Context, packageName: String): Set<String> {
        val updated = getFrozenApps(context).toMutableSet().apply { remove(packageName) }
        setFrozenApps(context, updated)
        return updated
    }
    fun isFrozen(context: Context, packageName: String): Boolean =
        getFrozenApps(context).contains(packageName)
}
