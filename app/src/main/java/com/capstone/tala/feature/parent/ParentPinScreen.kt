package com.capstone.tala.feature.parent

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.capstone.tala.core.designsystem.components.TalaPrimaryButton
import com.capstone.tala.core.designsystem.components.TalaSecondaryButton
import com.capstone.tala.core.designsystem.components.TalaCard
import com.capstone.tala.core.ui.theme.TALAColor
import kotlinx.coroutines.delay
import kotlin.math.sin

@Composable
fun ParentPinScreen(
    onPinAccepted: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    var pin by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf(false) }
    val shake by animateFloatAsState(
        targetValue = if (error) 1f else 0f,
        animationSpec = tween(240),
        label = "pin-error-shake"
    )
    LaunchedEffect(error) {
        if (error) {
            delay(900)
            error = false
        }
    }

    val digits = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(TALAColor.BackgroundCream),
        contentAlignment = Alignment.Center
    ) {
        TalaCard(
            modifier = Modifier
                .fillMaxWidth(if (maxWidth > 840.dp) 0.55f else 0.94f)
                .heightIn(max = maxHeight - 32.dp)
                .padding(16.dp),
            tonal = true
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Grown-up access",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TALAColor.NightBlue,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Use the teacher PIN to continue.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TALAColor.NightBlue.copy(alpha = 0.8f)
                )

                Row(
                    modifier = Modifier.graphicsLayer {
                        translationX = if (error) sin(shake * 6f) * 8f else 0f
                    },
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    repeat(4) { index ->
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .background(
                                    if (index < pin.length) TALAColor.StarYellow else TALAColor.SurfaceVariant,
                                    RoundedCornerShape(50)
                                )
                                .border(2.dp, TALAColor.NightBlue.copy(alpha = 0.35f), RoundedCornerShape(50))
                        )
                    }
                }

                if (error) {
                    Text(
                        text = "Let's try those numbers again.",
                        color = TALAColor.OnSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                val rows = digits.chunked(3)
                rows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        row.forEach { digit ->
                            Button(
                                onClick = {
                                    if (pin.length < 4) pin += digit
                                },
                                modifier = Modifier.weight(1f).heightIn(min = 72.dp)
                                    .graphicsLayer { translationX = if (error) sin(shake * 6f) * 8f else 0f },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TALAColor.SurfaceVariant,
                                    contentColor = TALAColor.NightBlue
                                ),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(digit, style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = { pin = ""; error = false },
                        modifier = Modifier.weight(1f).heightIn(min = 72.dp)
                    ) { Text("Clear", style = MaterialTheme.typography.labelLarge) }

                    TalaPrimaryButton(
                        text = "Unlock",
                        onClick = {
                            if (ParentAccessLogic.isPinValid(pin)) {
                                onPinAccepted()
                            } else {
                                pin = ""
                                error = true
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TalaSecondaryButton(
                    text = "Back",
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
