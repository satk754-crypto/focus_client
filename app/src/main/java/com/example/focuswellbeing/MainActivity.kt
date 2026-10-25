package com.example.focuswellbeing

import com.example.focuswellbeing.features.AppLifecycleTracker.presentation.ui.AppLifecycleTracker
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.focuswellbeing.features.auth.domain.AuthViewModel
import com.example.focuswellbeing.features.auth.ui.LoginScreen
import com.example.focuswellbeing.features.dashboard.presentation.ui.ShieldActivity
import com.example.focuswellbeing.features.dashboard.presentation.ui.WellBeingDashboard
import com.example.focuswellbeing.features.dashboard.presentation.viewModel.WellbeingViewModel
import com.example.focuswellbeing.utils.AppMonitoringUtils
import com.example.focuswellbeing.utils.usage.UsageActivityPermission

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val wellbeingViewModel: WellbeingViewModel by viewModels()
    private val usagePermission = UsageActivityPermission()

    override fun onResume() {
        super.onResume()
        wellbeingViewModel.syncFrozenApps(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleTracker)

        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_SECURE,
            android.view.WindowManager.LayoutParams.FLAG_SECURE
        )
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val wellbeingState by wellbeingViewModel.uiState.collectAsStateWithLifecycle()
            val authTokenState = authViewModel.getAuthToken(context).collectAsState(initial = null)

            // Keep UI in sync when ShieldActivity unfreezes an app
            DisposableEffect(Unit) {
                val receiver = object : BroadcastReceiver() {
                    override fun onReceive(ctx: Context?, intent: Intent?) {
                        wellbeingViewModel.syncFrozenApps(applicationContext)
                    }
                }
                val filter = IntentFilter(ShieldActivity.ACTION_FROZEN_APPS_CHANGED)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    registerReceiver(receiver, filter, RECEIVER_NOT_EXPORTED)
                } else {
                    @Suppress("DEPRECATION")
                    registerReceiver(receiver, filter)
                }
                onDispose { unregisterReceiver(receiver) }
            }

            MaterialTheme(colorScheme = darkColorScheme()) {
                if (authTokenState.value == null) {
                    LoginScreen(onGoogleSinSequence = { email ->
                        authViewModel.saveSession(
                            context, "mock_jwt_token_xyz123", email, onComplete = {})
                    })
                } else {
                    // Backup monitor: if a frozen app is foregrounded while this process is alive,
                    // bring the shield over it (AccessibilityService is the primary blocker).
                    LaunchedEffect(wellbeingState.frozenAppsMap) {
                        if (!usagePermission.hasUsageStatsPermission(applicationContext)) {
                            usagePermission.requestUsageStatsPermission(applicationContext)
                        }
                        var lastLaunchedPkg: String? = null
                        var lastLaunchedAt = 0L
                        AppMonitoringUtils.monitorFrozenApps(
                            context,
                            wellbeingState.frozenAppsMap
                        ).collect { blockedPkg ->
                            if (blockedPkg == null) return@collect
                            val now = System.currentTimeMillis()
                            if (blockedPkg == lastLaunchedPkg && now - lastLaunchedAt < 2_000L) {
                                return@collect
                            }
                            val shield = Intent(context, ShieldActivity::class.java).apply {
                                addFlags(
                                    Intent.FLAG_ACTIVITY_NEW_TASK or
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                        Intent.FLAG_ACTIVITY_SINGLE_TOP
                                )
                                putExtra(ShieldActivity.EXTRA_BLOCKED_PACKAGE, blockedPkg)
                            }
                            context.startActivity(shield)
                        }
                    }

                    Box(modifier = Modifier.fillMaxSize()) {
                        WellBeingDashboard(
                            viewModel = wellbeingViewModel,
                            context = context,
                            onBackClick = {
                                authViewModel.logout(context) {}
                            }
                        )
                    }
                }
            }
        }
    }
}
