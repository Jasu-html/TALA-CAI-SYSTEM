package com.capstone.tala.feature.parent

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.capstone.tala.core.designsystem.components.TalaSecondaryButton
import com.capstone.tala.core.designsystem.components.TalaCard
import com.capstone.tala.core.designsystem.components.TalaShapes
import com.capstone.tala.core.ui.theme.TALAColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ParentGateScreen(
    onUnlock: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val challengeSaver: Saver<MathChallenge, Any> = remember {
        listSaver(
            save = { listOf(it.prompt, it.correctAnswer) },
            restore = { MathChallenge(it[0] as String, it[1] as Int) }
        )
    }
    var challenge by rememberSaveable(stateSaver = challengeSaver) {
        mutableStateOf(ParentAccessLogic.createMathChallenge())
    }
    var answerText by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf(false) }
    var isUnlocking by rememberSaveable { mutableStateOf(false) }
    val shake by animateFloatAsState(
        targetValue = if (error) 1f else 0f,
        animationSpec = tween(260),
        label = "parent-gate-retry-shake"
    )
    val holdProgress by animateFloatAsState(
        targetValue = if (isUnlocking) 1f else 0f,
        animationSpec = tween(durationMillis = 3000),
        label = "parent-gate-hold-progress"
    )

    val holdInteraction = remember { MutableInteractionSource() }
    val isHolding by holdInteraction.collectIsPressedAsState()
    LaunchedEffect(isHolding, answerText) {
        if (isHolding && answerText.isBlank()) {
            isUnlocking = true
            delay(3000)
            if (isHolding) onUnlock()
        } else {
            isUnlocking = false
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(TALAColor.BackgroundCream),
        contentAlignment = Alignment.Center
    ) {
        val compactLayout = maxHeight < 440.dp
        TalaCard(
            modifier = Modifier
                .fillMaxWidth(if (maxWidth > 840.dp) 0.58f else 0.94f)
                .heightIn(max = maxHeight - 32.dp)
                .padding(horizontal = 16.dp, vertical = if (compactLayout) 8.dp else 16.dp),
            tonal = true
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(if (compactLayout) 16.dp else 24.dp),
                verticalArrangement = Arrangement.spacedBy(if (compactLayout) 8.dp else 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Outlined.Lock, contentDescription = "Parent gate", tint = TALAColor.NightBlue, modifier = Modifier.size(36.dp))
                Text("Parent gate", style = MaterialTheme.typography.headlineLarge, color = TALAColor.NightBlue)

                Text(
                    text = "Hold the button below for 3 seconds, or solve the quick check.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TALAColor.NightBlue.copy(alpha = 0.8f)
                )

                Text(
                    text = challenge.prompt,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TALAColor.NightBlue,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.graphicsLayer {
                        translationX = if (error) kotlin.math.sin(shake * 6f) * 7f else 0f
                    }
                )

                OutlinedTextField(
                    value = answerText,
                    onValueChange = { answerText = it },
                    label = { Text("Your answer") },
                    singleLine = true,
                    shape = TalaShapes.input,
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                NumericKeypad(
                    onDigit = { digit -> answerText = (answerText + digit).take(3); error = false },
                    onBackspace = { answerText = answerText.dropLast(1); error = false },
                    compact = compactLayout
                )

                if (error) {
                    Text(
                        text = "Let's try another answer together.",
                        color = TALAColor.OnSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        interactionSource = holdInteraction,
                        onClick = {
                            if (answerText.isNotBlank()) {
                                if (ParentAccessLogic.isMathAnswerCorrect(challenge, answerText.toIntOrNull() ?: -1)) {
                                    onUnlock()
                                } else {
                                    error = true
                                    answerText = ""
                                    challenge = ParentAccessLogic.createMathChallenge()
                                }
                            }
                        },
                        modifier = Modifier.size(152.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TALAColor.StarYellow,
                            contentColor = TALAColor.NightBlue
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = holdProgress,
                                modifier = Modifier.fillMaxSize(),
                                color = TALAColor.NightBlue,
                                trackColor = TALAColor.NightBlue.copy(alpha = 0.14f),
                                strokeWidth = 5.dp
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Outlined.Lock, contentDescription = null, modifier = Modifier.size(30.dp))
                                Text(if (isUnlocking) "Hold..." else "Hold 3 sec", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                    TalaSecondaryButton(text = "Back", onClick = onBack, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun NumericKeypad(
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    compact: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(if (compact) 4.dp else 8.dp)) {
        listOf(listOf("1", "2", "3"), listOf("4", "5", "6"), listOf("7", "8", "9")).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { digit ->
                    Button(
                        onClick = { onDigit(digit) },
                        modifier = Modifier.weight(1f).heightIn(min = 72.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TALAColor.SurfaceVariant, contentColor = TALAColor.NightBlue)
                    ) { Text(digit, style = MaterialTheme.typography.titleMedium) }
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onBackspace,
                modifier = Modifier.weight(1f).heightIn(min = 72.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TALAColor.SurfaceVariant, contentColor = TALAColor.NightBlue)
            ) { Icon(Icons.Outlined.Backspace, contentDescription = "Remove last digit") }
            Button(
                onClick = { onDigit("0") },
                modifier = Modifier.weight(1f).heightIn(min = 72.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TALAColor.SurfaceVariant, contentColor = TALAColor.NightBlue)
            ) { Text("0", style = MaterialTheme.typography.titleMedium) }
            Button(
                onClick = onBackspace,
                modifier = Modifier.weight(1f).heightIn(min = 72.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TALAColor.SurfaceVariant, contentColor = TALAColor.NightBlue)
            ) { Text("Clear", style = MaterialTheme.typography.labelMedium) }
        }
    }
}
