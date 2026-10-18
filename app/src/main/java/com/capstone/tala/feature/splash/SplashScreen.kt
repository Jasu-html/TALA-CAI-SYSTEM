package com.capstone.tala.feature.splash

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capstone.tala.core.designsystem.components.StarrySky
import com.capstone.tala.core.designsystem.components.LocalReducedMotion
import com.capstone.tala.core.designsystem.components.TalaCharacterView
import com.capstone.tala.core.designsystem.components.TalaPrimaryButton
import com.capstone.tala.core.designsystem.components.TalaSecondaryButton
import com.capstone.tala.core.ui.theme.TALAColor

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit = {},
    onParentGate: () -> Unit = {},
    reducedMotion: Boolean = LocalReducedMotion.current,
    viewModel: SplashViewModel = viewModel()
) {
    val animationsComplete = viewModel.animationsComplete
    val transition = rememberInfiniteTransition(label = "tala-idle")
    val bob by transition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "tala-bob"
    )

    LaunchedEffect(Unit) {
        viewModel.startAnimation()
    }

    StarrySky(modifier = Modifier.fillMaxSize(), reducedMotion = reducedMotion) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val compactLandscape = maxHeight < 480.dp
            if (maxWidth > maxHeight) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(40.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TalaCharacterView(
                        characterName = "Tala",
                        currentState = "wave",
                        modifier = Modifier
                            .size(180.dp)
                            .graphicsLayer { translationY = if (reducedMotion) 0f else bob }
                    )
                    SplashActions(
                        animationsComplete = animationsComplete,
                        compact = compactLandscape,
                        onReady = {
                            viewModel.onReady()
                            onNavigateToHome()
                        },
                        onParentGate = onParentGate,
                        reducedMotion = reducedMotion,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                val compact = maxHeight < 700.dp
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    TalaCharacterView(
                        characterName = "Tala",
                        currentState = "wave",
                        modifier = Modifier
                            .size(if (compact) 128.dp else 184.dp)
                            .graphicsLayer { translationY = if (reducedMotion) 0f else bob }
                    )
                    Spacer(Modifier.height(8.dp))
                    SplashActions(
                        animationsComplete = animationsComplete,
                        compact = compact,
                        onReady = {
                            viewModel.onReady()
                            onNavigateToHome()
                        },
                        onParentGate = onParentGate,
                        reducedMotion = reducedMotion
                    )
                }
            }
        }
    }
}

@Composable
private fun SplashActions(
    animationsComplete: Boolean,
    compact: Boolean,
    onReady: () -> Unit,
    onParentGate: () -> Unit,
    reducedMotion: Boolean,
    modifier: Modifier = Modifier
) {
    val pulseTransition = rememberInfiniteTransition(label = "ready-pulse")
    val pulse by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.025f,
        animationSpec = infiniteRepeatable(tween(1700), RepeatMode.Reverse),
        label = "ready-button-pulse"
    )
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "TALA",
            style = (if (compact) MaterialTheme.typography.displaySmall else MaterialTheme.typography.displayLarge).copy(
                shadow = Shadow(TALAColor.StarYellow.copy(alpha = 0.38f), Offset.Zero, 18f)
            ),
            color = TALAColor.StarYellow,
            textAlign = TextAlign.Center
        )
        Text(
            text = "A little starlight for every learner",
            style = if (compact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge,
            color = TALAColor.BackgroundCream,
            textAlign = TextAlign.Center
        )
        if (!animationsComplete) {
            CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                color = TALAColor.StarYellow,
                trackColor = TALAColor.BackgroundCream.copy(alpha = 0.28f)
            )
        }
        TalaPrimaryButton(
            text = "I'm ready!",
            onClick = onReady,
            enabled = animationsComplete,
            modifier = Modifier.fillMaxWidth().graphicsLayer {
                val scale = if (reducedMotion) 1f else pulse
                scaleX = scale
                scaleY = scale
            },
            icon = Icons.Rounded.PlayArrow,
            contentDescription = "Begin learning"
        )
        TalaSecondaryButton(
            text = "For grown-ups",
            onClick = onParentGate,
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Outlined.Lock,
            contentDescription = "Open parent gate"
        )
    }
}
