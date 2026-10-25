package com.example.focuswellbeing.features.appTracker.presentation.ui

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object AppLifecycleTracker : DefaultLifecycleObserver {
    private val _isAppInForeground = MutableStateFlow(true)
    val isAppInForeground: StateFlow<Boolean> = _isAppInForeground

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        _isAppInForeground.value = true
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        _isAppInForeground.value = false
    }
}