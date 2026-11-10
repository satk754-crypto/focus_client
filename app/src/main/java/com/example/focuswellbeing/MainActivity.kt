package com.example.focuswellbeing

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.focuswellbeing.components.FocusDurationSelectionDialog
import com.example.focuswellbeing.di.DependencyProvider
import com.example.focuswellbeing.features.appTracker.presentation.ui.AppLifecycleTracker
import com.example.focuswellbeing.features.auth.domain.AuthViewModel
import com.example.focuswellbeing.features.auth.ui.LoginScreen
import com.example.focuswellbeing.features.dashboard.presentation.ui.ShieldActivity
import com.example.focuswellbeing.features.dashboard.presentation.ui.WellBeingDashboard
import com.example.focuswellbeing.features.dashboard.presentation.viewModel.WellbeingViewModel
import com.example.focuswellbeing.features.navigation.ui.AppBottomBar
import com.example.focuswellbeing.model.Screen
import com.example.focuswellbeing.ui.theme.FocusWellbeingTheme
import com.example.focuswellbeing.utils.AppMonitoringUtils
import com.example.music.features.presentation.ui.PlaceHolder

class MainActivity : ComponentActivity() {
    val provider = DependencyProvider()
    private val authViewModel: AuthViewModel by viewModels()
    private val wellbeingViewModel: WellbeingViewModel by viewModels()

    val accessibilityPermission = provider.provideUsageActivityPermission()

    override fun onResume() {
        super.onResume()
        wellbeingViewModel.syncFrozenApps(applicationContext)
    }
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleTracker)

        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_SECURE,
            android.view.WindowManager.LayoutParams.FLAG_SECURE
        )
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            val context = LocalContext.current
            val wellbeingState by wellbeingViewModel.uiState.collectAsStateWithLifecycle()
            val authTokenState = authViewModel.getAuthToken(context).collectAsState(initial = null)

            // 👉 State for Focus duration popup dialog
            var showFocusDialog by remember { mutableStateOf(false) }

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

            FocusWellbeingTheme(darkTheme = true) {
                if (authTokenState.value == null) {
                    LoginScreen(onGoogleSinSequence = { email ->
                        authViewModel.saveSession(
                            context, "mock_jwt_token_xyz123", email, onComplete = {})
                    })
                } else {
                    // Backup monitor for frozen apps
                    LaunchedEffect(wellbeingState.frozenAppsMap) {
                        if (!accessibilityPermission.hasUsageStatsPermission(applicationContext)) {
                            accessibilityPermission.requestUsageStatsPermission(applicationContext)
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

                    // 👉 Show duration selection popup when Focus tab/action is triggered
                    if (showFocusDialog) {
                        FocusDurationSelectionDialog(
                            onDismiss = { showFocusDialog = false },
                            onSelectDuration = { totalMins ->
                                showFocusDialog = false
                                wellbeingViewModel.startFocusSession(totalMins)
                            }
                        )
                    }

                    Scaffold(
                        bottomBar = {
                            AppBottomBar(
                                navController = navController,
                            )
                        },
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding) // 👉 Fixed: Respects scaffold bottom bar padding
                        ) {
                            NavHost(navController = navController, startDestination = Screen.Focus.route) {
                                composable(Screen.Focus.route) {
                                    WellBeingDashboard(
                                        viewModel = wellbeingViewModel,
                                        context = context,
                                        onBackClick = {
                                            authViewModel.logout(context) {}
                                        }
                                    )
                                }
                                composable(Screen.Music.route) {
                                    PlaceHolder()
                                }
                                composable(Screen.WalkingTrack.route) {
                                    PlaceholderFeatureScreen("Walking Track 🚶", "Track steps, distance, and daily movement.")
                                }

                                composable(Screen.Health.route) {
                                    PlaceholderFeatureScreen("Heal Track 🚶", "Track steps, distance, and daily movement.")
                                }                            }

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceholderFeatureScreen(title: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}