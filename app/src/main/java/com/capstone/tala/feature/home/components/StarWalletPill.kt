package com.capstone.tala.feature.home.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.capstone.tala.core.ui.theme.TALAColor

/**
 * Animated star wallet pill for HomeScreen top bar.
 * Shows star count with scale animation when value changes.
 *
 * @param stars Current star count from database
 * @param modifier Modifier for layout
 */
@Composable
fun StarWalletPill(
    stars: Int,
    modifier: Modifier = Modifier
) {
    var previousStars by remember { mutableIntStateOf(stars) }
    val scale = remember { Animatable(1f) }

    // Animate scale when stars change (not on first composition)
    LaunchedEffect(stars) {
        if (previousStars != stars && previousStars != 0) {
            scale.animateTo(1.12f, animationSpec = tween(150))
            scale.animateTo(1f, animationSpec = tween(150))
        }
        previousStars = stars
    }

    Surface(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .semantics {
                contentDescription = "$stars stars"
            },
        color = TALAColor.StarYellow.copy(alpha = 0.18f),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(TALAColor.StarYellow, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = TALAColor.NightBlue,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = stars.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = TALAColor.NightBlue,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
