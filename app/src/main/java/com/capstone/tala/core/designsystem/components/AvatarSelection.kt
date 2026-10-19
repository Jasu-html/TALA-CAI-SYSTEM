package com.capstone.tala.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.capstone.tala.core.ui.theme.TALAColor
import com.capstone.tala.core.designsystem.components.TalaShapes

/**
 * Avatar selection grid with 8 presets for young children.
 *
 * Features:
 * - 4x2 grid of preset avatars (kid-friendly emoji-based illustrations)
 * - Press-to-select with highlight ring
 * - Checks displayed for selected avatars
 * - Disabled until voice name is captured
 */
@Composable
fun AvatarSelection(
    selectedAvatarId: String = "1",
    onAvatarSelected: (String) -> Unit,
    isButtonDisabled: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Placeholder avatars - using colored circles with cute icons
    // In a full implementation, these would be SVG/vector drawings
    val avatarOptions = listOf(
        AvatarOption(
            id = "1",
            name = "Bubbles",
            color = TALAColor.StarYellow,
            icon = "🎈"
        ),
        AvatarOption(
            id = "2",
            name = "Sunny",
            color = TALAColor.Coral,
            icon = "☀️"
        ),
        AvatarOption(
            id = "3",
            name = "Sky",
            color = TALAColor.SkyBlue,
            icon = "✨"
        ),
        AvatarOption(
            id = "4",
            name = "Berry",
            color = TALAColor.Purple,
            icon = "🍇"
        ),
        AvatarOption(
            id = "5",
            name = "Leafy",
            color = TALAColor.MintGreen,
            icon = "🍀"
        ),
        AvatarOption(
            id = "6",
            name = "Patch",
            color = Color(0xFFFFC0CB),
            icon = "🌸"
        ),
        AvatarOption(
            id = "7",
            name = "Choco",
            color = Color(0xFF8B5A2B),
            icon = "🐰"
        ),
        AvatarOption(
            id = "8",
            name = "Stella",
            color = TALAColor.StarYellow,
            icon = "🎀"
        )
    )

    var selectedAvatar by remember { mutableStateOf(selectedAvatarId) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Choose your avatar",
            style = MaterialTheme.typography.titleMedium,
            color = TALAColor.OnSurface,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 4x2 grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                avatarOptions.slice(0..1).forEach { avatar ->
                    AvatarOptionCard(
                        avatar = avatar,
                        isSelected = avatar.id == selectedAvatar && !isButtonDisabled,
                        onClick = { if (!isButtonDisabled) {
                            selectedAvatar = avatar.id
                            onAvatarSelected(avatar.id)
                        }},
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                avatarOptions.slice(2..3).forEach { avatar ->
                    AvatarOptionCard(
                        avatar = avatar,
                        isSelected = avatar.id == selectedAvatar && !isButtonDisabled,
                        onClick = { if (!isButtonDisabled) {
                            selectedAvatar = avatar.id
                            onAvatarSelected(avatar.id)
                        }},
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                avatarOptions.slice(4..5).forEach { avatar ->
                    AvatarOptionCard(
                        avatar = avatar,
                        isSelected = avatar.id == selectedAvatar && !isButtonDisabled,
                        onClick = { if (!isButtonDisabled) {
                            selectedAvatar = avatar.id
                            onAvatarSelected(avatar.id)
                        }},
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                avatarOptions.slice(6..7).forEach { avatar ->
                    AvatarOptionCard(
                        avatar = avatar,
                        isSelected = avatar.id == selectedAvatar && !isButtonDisabled,
                        onClick = { if (!isButtonDisabled) {
                            selectedAvatar = avatar.id
                            onAvatarSelected(avatar.id)
                        }},
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

/**
 * Avatar option card with selection ring and checkmark
 */
@Composable
private fun AvatarOptionCard(
    avatar: AvatarOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = TalaShapes.rounded,
        color = Color.White,
        shadowElevation = if (isSelected) 6.dp else 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatars loop with emoji - color circles with cute icons
            val accentColor = avatar.color
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(accentColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = avatar.icon,
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            // Selected checkmark
            if (isSelected) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = "Selected",
                    tint = TALAColor.StarYellow,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }

            Text(
                text = avatar.name,
                style = MaterialTheme.typography.bodyLarge,
                color = TALAColor.OnSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Data class for avatar options
 */
data class AvatarOption(
    val id: String,
    val name: String,
    val color: Color,
    val icon: String
)