package com.capstone.tala.core.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.capstone.tala.core.ui.theme.TALAColor

@Composable
fun TalaPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    contentDescription: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val reducedMotion = LocalReducedMotion.current
    val scale by animateFloatAsState(
        targetValue = if (pressed && !reducedMotion) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.58f, stiffness = 520f),
        label = "primary-button-press"
    )
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .heightIn(min = 72.dp)
            .shadow(5.dp, CircleShape, clip = false)
            .graphicsLayer { scaleX = scale; scaleY = scale },
        interactionSource = interactionSource,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = TALAColor.StarYellow,
            contentColor = TALAColor.NightBlue
        )
    ) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            if (icon != null) {
                Icon(icon, contentDescription = contentDescription)
                Spacer(Modifier.width(10.dp))
            }
            Text(text = text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun TalaSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    contentDescription: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val reducedMotion = LocalReducedMotion.current
    val scale by animateFloatAsState(
        targetValue = if (pressed && !reducedMotion) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.62f, stiffness = 520f),
        label = "secondary-button-press"
    )
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = 72.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale },
        interactionSource = interactionSource,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
        shape = CircleShape,
        border = BorderStroke(2.dp, TALAColor.NightBlue.copy(alpha = 0.3f)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = TALAColor.SurfaceCream,
            contentColor = TALAColor.NightBlue
        )
    ) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            if (icon != null) {
                Icon(icon, contentDescription = contentDescription)
                Spacer(Modifier.width(10.dp))
            }
            Text(text = text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun TalaTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    contentDescription: String? = null
) {
    androidx.compose.material3.TextButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 72.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        colors = ButtonDefaults.textButtonColors(contentColor = TALAColor.NightBlue)
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = contentDescription)
            Spacer(Modifier.width(8.dp))
        }
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun BigIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = TALAColor.StarYellow,
    tint: Color = TALAColor.NightBlue
) {
    Button(
        onClick = onClick,
        modifier = modifier.size(72.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = tint
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(34.dp)
        )
    }
}

@Composable
fun HintBulb(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    active: Boolean = true
) {
    val containerColor = if (active) TALAColor.SurfaceVariant else TALAColor.SurfaceCream
    val iconColor = TALAColor.NightBlue

    FilledTonalButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 72.dp),
        shape = CircleShape,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = containerColor,
            contentColor = iconColor
        )
    ) {
        Icon(
            imageVector = Icons.Outlined.Lightbulb,
            contentDescription = "Hint",
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
fun SpeakerButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 72.dp),
        shape = CircleShape,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = TALAColor.SurfaceVariant,
            contentColor = TALAColor.NightBlue
        )
    ) {
        Icon(
            imageVector = Icons.Default.VolumeUp,
            contentDescription = "Play narration"
        )
    }
}
