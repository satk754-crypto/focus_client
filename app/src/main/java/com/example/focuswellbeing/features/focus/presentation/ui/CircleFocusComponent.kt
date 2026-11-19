package com.example.focuswellbeing.features.focus.presentation.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focuswellbeing.components.FocusDurationSelectionDialog
import com.example.focuswellbeing.ui.theme.IndigoLight
import com.example.focuswellbeing.ui.theme.IndigoPrimary
import java.util.Locale

@Composable
fun CenterFocusButton(
    isFocusActive: Boolean,
    remainingSeconds: Int,
    onStartFocus: (Int) -> Unit,
    onStopFocus: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 148.dp
) {
    val haptics = LocalHapticFeedback.current
    var showDurationDialog by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "focusButtonScale"
    )

    val glowColor by animateColorAsState(
        targetValue = if (isFocusActive) Color(0xFF0EA5E9) else IndigoPrimary,
        label = "focusGlowColor"
    )

    if (showDurationDialog) {
        FocusDurationSelectionDialog(
            onDismiss = { showDurationDialog = false },
            onSelectDuration = { totalMinutes ->
                showDurationDialog = false
                onStartFocus(totalMinutes)
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            modifier = Modifier
                .size(size)
                .shadow(
                    elevation = if (isFocusActive) 20.dp else 14.dp,
                    shape = CircleShape,
                    ambientColor = glowColor,
                    spotColor = glowColor
                ),
            shape = CircleShape,
            color = Color.Transparent,
            border = BorderStroke(
                width = 3.dp,
                color = if (isFocusActive) {
                    Color(0xFF38BDF8).copy(alpha = 0.55f)
                } else {
                    IndigoLight.copy(alpha = 0.4f)
                }
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp)
                    .clip(CircleShape)
                    .background(
                        brush = if (isFocusActive) {
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF38BDF8), Color(0xFF0EA5E9))
                            )
                        } else {
                            Brush.verticalGradient(
                                colors = listOf(IndigoLight, IndigoPrimary)
                            )
                        }
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        if (isFocusActive) {
                            onStopFocus()
                        } else {
                            showDurationDialog = true
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = if (isFocusActive) "⏳" else "🎯",
                        fontSize = 26.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    if (isFocusActive) {
                        val safeSeconds = remainingSeconds.coerceAtLeast(0)
                        val minutes = safeSeconds / 60
                        val seconds = safeSeconds % 60
                        Text(
                            text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White,
                            maxLines = 1
                        )
                        Text(
                            text = "Tap to Stop",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    } else {
                        Text(
                            text = "Start\nFocus",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        Text(
            text = if (isFocusActive) {
                "Deep Focus Active · Other apps locked"
            } else {
                "Tap to choose duration & lock other apps"
            },
            style = MaterialTheme.typography.labelMedium,
            color = if (isFocusActive) Color(0xFF38BDF8) else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}