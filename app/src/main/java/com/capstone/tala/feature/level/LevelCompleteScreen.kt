package com.capstone.tala.feature.level

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.capstone.tala.core.designsystem.components.TalaCharacterView
import com.capstone.tala.core.designsystem.components.TalaPrimaryButton
import com.capstone.tala.core.designsystem.components.TalaSecondaryButton
import com.capstone.tala.core.ui.theme.TALAColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Level Complete celebration screen.
 * Shows confetti, stars popping in one by one, guide cheering, and navigation buttons.
 *
 * Phase 6 implementation per TALA_PROGRAMMING_GUIDE.md section 8, Phase 6.
 */
@Composable
fun LevelCompleteScreen(
    starsEarned: Int,
    guideName: String,
    onNextLevel: () -> Unit,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animation states
    var starsVisible by remember { mutableStateOf(0) }
    var showButtons by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Start star animation on mount
    LaunchedEffect(starsEarned) {
        scope.launch {
            repeat(starsEarned) { index ->
                delay(400) // 400ms between each star pop
                starsVisible = index + 1
            }
            delay(600)
            showButtons = true
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TALAColor.BackgroundCream)
    ) {
        // Confetti overlay
        ConfettiOverlay(visible = true)

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Cheering guide character
            TalaCharacterView(
                characterName = guideName,
                currentState = "cheer",
                modifier = Modifier.size(120.dp)
            )

            Spacer(Modifier.height(24.dp))

            // Congratulations message
            Text(
                text = "Level Complete!",
                style = MaterialTheme.typography.headlineLarge,
                color = TALAColor.NightBlue,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = when (starsEarned) {
                    5 -> "Perfect! You earned 5 stars!"
                    4 -> "Great job! You earned 4 stars!"
                    3 -> "Good work! You earned 3 stars!"
                    2 -> "Nice try! You earned 2 stars!"
                    else -> "You earned 1 star! Keep practicing!"
                },
                style = MaterialTheme.typography.titleMedium,
                color = TALAColor.OnSurface,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            // Stars row with pop-in animation
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(starsEarned) { index ->
                    AnimatedStar(
                        visible = index < starsVisible,
                        delay = index * 400
                    )
                }
            }

            Spacer(Modifier.height(48.dp))

            // Navigation buttons (fade in after stars)
            if (showButtons) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TalaPrimaryButton(
                        text = "Next Level",
                        onClick = onNextLevel,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TalaSecondaryButton(
                            text = "Play Again",
                            onClick = onPlayAgain,
                            icon = Icons.Default.Replay,
                            modifier = Modifier.weight(1f)
                        )

                        TalaSecondaryButton(
                            text = "Home",
                            onClick = onHome,
                            icon = Icons.Default.Home,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Single star with pop-in animation.
 */
@Composable
private fun AnimatedStar(
    visible: Boolean,
    delay: Int,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "star-pop-$delay"
    )

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "star-alpha-$delay"
    )

    Icon(
        imageVector = Icons.Default.Star,
        contentDescription = "Star",
        tint = TALAColor.StarYellow,
        modifier = modifier
            .size(56.dp)
            .scale(scale)
            .alpha(alpha)
    )
}

/**
 * Simple confetti overlay with falling particles.
 * Phase 6: Basic implementation with colored circles.
 */
@Composable
private fun ConfettiOverlay(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    val infiniteTransition = rememberInfiniteTransition(label = "confetti")

    // Create multiple confetti particles
    Box(modifier = modifier.fillMaxSize()) {
        repeat(20) { index ->
            val offsetY by infiniteTransition.animateFloat(
                initialValue = -100f,
                targetValue = 1000f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 3000 + (index * 200),
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                ),
                label = "confetti-$index-y"
            )

            val offsetX by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 50f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 1000 + (index * 50),
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "confetti-$index-x"
            )

            val colors = listOf(
                TALAColor.StarYellow,
                TALAColor.MintGreen,
                TALAColor.SkyBlue,
                TALAColor.PhonicsAccent,
                TALAColor.ShapesAccent
            )

            Box(
                modifier = Modifier
                    .offset(
                        x = ((index * 40) % 300).dp + offsetX.dp,
                        y = offsetY.dp
                    )
                    .size(8.dp)
                    .background(
                        colors[index % colors.size],
                        androidx.compose.foundation.shape.CircleShape
                    )
            )
        }
    }
}
