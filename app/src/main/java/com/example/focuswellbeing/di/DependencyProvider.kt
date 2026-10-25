package com.example.focuswellbeing.di

import com.example.focuswellbeing.utils.usage.UsageActivityPermission
class DependencyProvider {
    private var userActivityPermission: UsageActivityPermission? = null
    fun provideUsageActivityPermission(): UsageActivityPermission {
        return userActivityPermission ?: UsageActivityPermission().also {
            userActivityPermission = it
        }
    }
}
