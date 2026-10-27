package com.example.focuswellbeing.di

import com.example.focuswellbeing.features.dashboard.domain.WellBeingUseCase
import com.example.focuswellbeing.utils.usage.UsageActivityPermission

class DependencyProvider {
    private var userActivityPermission: UsageActivityPermission? = null
    private var wellBeingUseCase: WellBeingUseCase? = null;
    fun provideUsageActivityPermission(): UsageActivityPermission {
        return userActivityPermission ?: UsageActivityPermission().also {
            userActivityPermission = it
        }
    }

    fun provideWellBeingUseCase(): WellBeingUseCase {
        return wellBeingUseCase ?: WellBeingUseCase().also {
            wellBeingUseCase = it
        }
    }
}
