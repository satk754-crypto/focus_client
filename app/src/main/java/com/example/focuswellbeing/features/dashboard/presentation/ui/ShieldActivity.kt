package com.example.focuswellbeing.features.dashboard.presentation.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.addCallback
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.Modifier
import com.example.focuswellbeing.features.dashboard.ui.presentation.AppBlockOverlay
import com.example.focuswellbeing.utils.FrozenAppsStore

class ShieldActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val blockedPackage = intent.getStringExtra(EXTRA_BLOCKED_PACKAGE).orEmpty()
        if (blockedPackage.isEmpty() || !FrozenAppsStore.isFrozen(this, blockedPackage)) {
            finish()
            return
        }

        // Back should not reveal the frozen app — send user home instead
        onBackPressedDispatcher.addCallback(this) {
            goHomeAndFinish()
        }

        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppBlockOverlay(
                        appName = resolveAppName(blockedPackage),
                        onUnfreezeClick = {
                            FrozenAppsStore.unfreeze(this, blockedPackage)
                            // Notify dashboard if it is alive
                            sendBroadcast(
                                Intent(ACTION_FROZEN_APPS_CHANGED).setPackage(packageName)
                            )
                            finish()
                        },
                        onDismissClick = { goHomeAndFinish() }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val blockedPackage = intent.getStringExtra(EXTRA_BLOCKED_PACKAGE).orEmpty()
        if (blockedPackage.isEmpty() || !FrozenAppsStore.isFrozen(this, blockedPackage)) {
            finish()
        }
    }

    private fun goHomeAndFinish() {
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(homeIntent)
        finish()
    }

    private fun resolveAppName(packageName: String): String {
        return try {
            val pm = packageManager
            val info = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(info).toString()
        } catch (_: Exception) {
            packageName.substringAfterLast('.').replaceFirstChar { it.uppercase() }
        }
    }

    companion object {
        const val EXTRA_BLOCKED_PACKAGE = "BLOCKED_PACKAGE"
        const val ACTION_FROZEN_APPS_CHANGED = "com.example.focuswellbeing.FROZEN_APPS_CHANGED"
    }
}
