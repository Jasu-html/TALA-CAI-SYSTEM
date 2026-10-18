package com.capstone.tala.feature.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.capstone.tala.core.ui.theme.TALAColor

/**
 * Night sky constellation progress strip showing overall progress.
 *
 * Displays a horizontal constellation with one point per module group,
 * showing lit (completed) vs unlit (incomplete) star pieces.
 *
 * @param totalLevels Total number of levels across all modules (70)
 * @param completedLevels Number of completed levels
 * @param modifier Modifier for layout
 */
@Composable
fun ConstellationStrip(
    totalLevels: Int,
    completedLevels: Int,
    modifier: Modifier = Modifier
) {
    val nightSkyGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF202858), // Night sky top
            Color(0xFF2B3A67), // Night sky mid
            Color(0xFF171D45)  // Night sky bottom
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(
                brush = nightSkyGradient,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(20.dp)
            .semantics {
                contentDescription = "$completedLevels of $totalLevels Star Pieces lit"
            }
    ) {
        // Title
        Text(
            text = "Your Night Sky",
            style = MaterialTheme.typography.titleMedium,
            color = TALAColor.StarYellow,
            modifier = Modifier.align(Alignment.TopStart)
        )

        // Draw constellation
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.BottomCenter)
        ) {
            val width = size.width
            val height = size.height
            val spacing = width / (totalLevels + 1)

            // Draw connecting lines between adjacent stars
            for (i in 0 until totalLevels - 1) {
                val x1 = spacing * (i + 1)
                val x2 = spacing * (i + 2)
                val y = height / 2f

                drawLine(
                    color = TALAColor.BackgroundCream.copy(alpha = 0.15f),
                    start = Offset(x1, y),
                    end = Offset(x2, y),
                    strokeWidth = 2f,
                    cap = StrokeCap.Round
                )
            }

            // Draw star points
            for (i in 0 until totalLevels) {
                val x = spacing * (i + 1)
                val y = height / 2f
                val isLit = i < completedLevels

                // Outer glow for lit stars
                if (isLit) {
                    drawCircle(
                        color = TALAColor.StarYellow.copy(alpha = 0.3f),
                        radius = 10f,
                        center = Offset(x, y)
                    )
                }

                // Star point
                drawCircle(
                    color = if (isLit) {
                        TALAColor.StarYellow
                    } else {
                        TALAColor.BackgroundCream.copy(alpha = 0.25f)
                    },
                    radius = if (isLit) 6f else 4f,
                    center = Offset(x, y)
                )

                // Inner highlight for lit stars
                if (isLit) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.8f),
                        radius = 2f,
                        center = Offset(x, y)
                    )
                }
            }
        }
    }
}
