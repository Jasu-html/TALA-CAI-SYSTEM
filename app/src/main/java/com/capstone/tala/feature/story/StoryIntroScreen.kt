package com.capstone.tala.feature.story

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.capstone.tala.core.designsystem.components.TalaPrimaryButton
import com.capstone.tala.core.designsystem.components.TalaSecondaryButton
import com.capstone.tala.core.ui.theme.TALAColor

@Composable
fun StoryIntroScreen(
    onContinue: () -> Unit = {},
    onSkip: () -> Unit = {}
) {
    val panels = listOf(
        "Tala is floating in the sky and hears a gentle call.",
        "Likha, Bilang, and Titik welcome her to a bright new world.",
        "Together they are ready to learn, play, and grow."
    )
    var currentPanel by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TALAColor.BackgroundCream),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            color = TALAColor.SurfaceCream,
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "A new adventure begins",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TALAColor.NightBlue,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    color = TALAColor.StarYellow.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = panels[currentPanel],
                            style = MaterialTheme.typography.bodyLarge,
                            color = TALAColor.NightBlue,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TalaPrimaryButton(
                    text = if (currentPanel == panels.lastIndex) "Start learning" else "Next",
                    onClick = {
                        if (currentPanel == panels.lastIndex) {
                            onContinue()
                        } else {
                            currentPanel += 1
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                TalaSecondaryButton(
                    text = "Skip",
                    onClick = onSkip,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
