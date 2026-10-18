package com.capstone.tala.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.capstone.tala.core.designsystem.components.TalaPrimaryButton
import com.capstone.tala.core.designsystem.components.TalaSecondaryButton
import com.capstone.tala.core.ui.theme.TALAColor

/**
 * Level popup dialog shown when a level node is tapped.
 *
 * Displays:
 * - Level title and lesson text from content JSON
 * - Illustration area (character + level number tile)
 * - Stars earned (if completed)
 * - Play button (only for CURRENT or COMPLETED levels)
 *
 * @param levelNumber The level index (1-based)
 * @param levelTitle Title from LevelContent
 * @param lessonText Description from LevelContent
 * @param guideName Guide character name (e.g., "Titik")
 * @param accent Module accent color
 * @param bestStars Stars earned (0-5)
 * @param isLocked True if level is locked (no Play button)
 * @param onDismiss Callback to close dialog
 * @param onPlay Callback to launch level
 */
@Composable
fun LevelPopup(
    levelNumber: Int,
    levelTitle: String,
    lessonText: String,
    guideName: String,
    accent: androidx.compose.ui.graphics.Color,
    bestStars: Int,
    isLocked: Boolean,
    onDismiss: () -> Unit,
    onPlay: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = TALAColor.SurfaceElevated
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with accent
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = accent.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Level $levelNumber",
                        style = MaterialTheme.typography.titleLarge,
                        color = TALAColor.NightBlue,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = guideName,
                        style = MaterialTheme.typography.labelLarge,
                        color = TALAColor.NightBlue.copy(alpha = 0.7f)
                    )
                }

                // Level title
                Text(
                    text = levelTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    color = TALAColor.NightBlue,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                // Illustration area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(accent.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        com.capstone.tala.core.designsystem.components.TalaCharacterView(
                            characterName = guideName,
                            currentState = "wave",
                            modifier = Modifier.size(100.dp)
                        )
                        Text(
                            text = "$levelNumber",
                            style = MaterialTheme.typography.displayMedium,
                            color = accent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Lesson text
                if (lessonText.isNotBlank()) {
                    Text(
                        text = lessonText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TALAColor.OnSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                // Star rating (if completed)
                if (bestStars > 0) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = if (index < bestStars) {
                                    Icons.Default.Star
                                } else {
                                    Icons.Default.StarBorder
                                },
                                contentDescription = null,
                                tint = if (index < bestStars) {
                                    TALAColor.StarYellow
                                } else {
                                    TALAColor.NightBlue.copy(alpha = 0.25f)
                                },
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                // Buttons
                if (isLocked) {
                    Text(
                        text = "Finish level ${levelNumber - 1} first!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TALAColor.OnSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    TalaSecondaryButton(
                        text = "Okay",
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    TalaPrimaryButton(
                        text = "Play",
                        onClick = onPlay,
                        modifier = Modifier.fillMaxWidth()
                    )
                    TalaSecondaryButton(
                        text = "Cancel",
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
