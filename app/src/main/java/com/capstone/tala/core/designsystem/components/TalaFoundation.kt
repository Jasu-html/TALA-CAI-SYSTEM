package com.capstone.tala.core.designsystem.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.capstone.tala.core.ui.theme.TALAColor
import kotlin.math.sin

object TalaSpacing {
    val xSmall = 8.dp
    val small = 16.dp
    val medium = 24.dp
    val large = 32.dp
    val xLarge = 40.dp
}

object TalaShapes {
    val card = RoundedCornerShape(28.dp)
    val input = RoundedCornerShape(20.dp)
}

val LocalReducedMotion = staticCompositionLocalOf { false }

@Composable
fun TalaCard(
    modifier: Modifier = Modifier,
    tonal: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .shadow(8.dp, TalaShapes.card, clip = false)
            .border(1.dp, TALAColor.NightBlue.copy(alpha = 0.08f), TalaShapes.card),
        shape = TalaShapes.card,
        color = if (tonal) TALAColor.SurfaceVariant else TALAColor.SurfaceElevated,
        contentColor = TALAColor.OnSurface,
        tonalElevation = 1.dp
    ) {
        Box(content = content)
    }
}

@Composable
fun StarrySky(
    modifier: Modifier = Modifier,
    reducedMotion: Boolean = LocalReducedMotion.current,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val stars = remember {
        List(42) { index ->
            val x = ((index * 73 + 19) % 997) / 997f
            val y = ((index * 151 + 37) % 991) / 991f
            val radius = 1.2f + ((index * 13) % 22) / 10f
            Triple(x, y, radius)
        }
    }
    val transition = rememberInfiniteTransition(label = "starry-sky")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(6400), RepeatMode.Restart),
        label = "star-twinkle"
    )
    val visiblePhase = if (reducedMotion) 0f else phase

    Box(modifier = modifier.background(TALAColor.DeepIndigo)) {
        Canvas(Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF202858),
                        TALAColor.NightBlue,
                        TALAColor.DeepIndigo
                    )
                )
            )
            stars.forEachIndexed { index, (x, y, radius) ->
                val twinkle = (sin(visiblePhase + index * 0.83f) + 1f) / 2f
                val alpha = if (reducedMotion) 0.55f else 0.25f + twinkle * 0.65f
                drawCircle(
                    color = TALAColor.StarYellow.copy(alpha = alpha),
                    radius = radius,
                    center = Offset(x * size.width, y * size.height)
                )
            }
        }
        content()
    }
}
