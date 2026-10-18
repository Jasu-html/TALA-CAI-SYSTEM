package com.capstone.tala.core.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.capstone.tala.core.ui.theme.TALAColor
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TalaCharacterView(
    characterName: String,
    currentState: String = "wave",
    modifier: Modifier = Modifier.size(150.dp)
) {
    val accent = when (characterName.lowercase()) {
        "likha" -> TALAColor.Coral
        "bilang" -> TALAColor.SkyBlue
        "titik" -> TALAColor.Purple
        else -> TALAColor.StarYellow
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val maxRadius = size.minDimension / 2f * 0.8f

            when (currentState) {
                "wave", "cheering" -> {
                    drawStar(
                        centerX = centerX,
                        centerY = centerY,
                        radius = maxRadius,
                        color = TALAColor.StarYellow
                    )
                    // Eyes
                    drawCircle(
                        color = TALAColor.NightBlue,
                        radius = maxRadius * 0.1f,
                        center = Offset(centerX - maxRadius * 0.2f, centerY - maxRadius * 0.1f)
                    )
                    drawCircle(
                        color = TALAColor.NightBlue,
                        radius = maxRadius * 0.1f,
                        center = Offset(centerX + maxRadius * 0.2f, centerY - maxRadius * 0.1f)
                    )
                    // Smile
                    val smilePath = Path().apply {
                        moveTo(centerX - maxRadius * 0.15f, centerY + maxRadius * 0.1f)
                        cubicTo(
                            centerX - maxRadius * 0.1f, centerY + maxRadius * 0.3f,
                            centerX + maxRadius * 0.1f, centerY + maxRadius * 0.3f,
                            centerX + maxRadius * 0.15f, centerY + maxRadius * 0.1f
                        )
                    }
                    drawPath(
                        path = smilePath,
                        color = TALAColor.NightBlue,
                        style = Stroke(width = maxRadius * 0.04f)
                    )
                }
                "sleepy" -> {
                    drawStar(
                        centerX = centerX,
                        centerY = centerY,
                        radius = maxRadius,
                        color = accent.copy(alpha = 0.7f)
                    )
                    // X eyes (sleepy)
                    drawCircleWithX(
                        color = TALAColor.NightBlue,
                        radius = maxRadius * 0.1f,
                        colorCenter = Color.White,
                        center = Offset(centerX - maxRadius * 0.2f, centerY - maxRadius * 0.1f)
                    )
                    drawCircleWithX(
                        color = TALAColor.NightBlue,
                        radius = maxRadius * 0.1f,
                        colorCenter = Color.White,
                        center = Offset(centerX + maxRadius * 0.2f, centerY - maxRadius * 0.1f)
                    )
                    // Small closed smile
                    val smallSmile = Path().apply {
                        moveTo(centerX - maxRadius * 0.05f, centerY + maxRadius * 0.2f)
                        lineTo(centerX + maxRadius * 0.05f, centerY + maxRadius * 0.2f)
                    }
                    drawPath(
                        path = smallSmile,
                        color = TALAColor.NightBlue,
                        style = Stroke(width = maxRadius * 0.03f)
                    )
                }
                "thinking" -> {
                    drawStar(
                        centerX = centerX,
                        centerY = centerY - maxRadius * 0.2f,
                        radius = maxRadius,
                        color = accent
                    )
                    // Thought bubble with question mark
                    drawCircle(
                        color = TALAColor.BackgroundCream,
                        radius = maxRadius * 0.4f,
                        center = Offset(centerX + maxRadius * 0.5f, centerY - maxRadius * 0.6f)
                    )
                    // Question mark
                    val questionPath = Path().apply {
                        moveTo(centerX + maxRadius * 0.55f, centerY - maxRadius * 0.2f)
                        lineTo(centerX + maxRadius * 0.55f, centerY - maxRadius * 0.4f)
                        cubicTo(
                            centerX + maxRadius * 0.55f, centerY - maxRadius * 0.55f,
                            centerX + maxRadius * 0.4f, centerY - maxRadius * 0.55f,
                            centerX + maxRadius * 0.4f, centerY - maxRadius * 0.45f
                        )
                        lineTo(centerX + maxRadius * 0.4f, centerY - maxRadius * 0.35f)
                        cubicTo(
                            centerX + maxRadius * 0.4f, centerY - maxRadius * 0.25f,
                            centerX + maxRadius * 0.5f, centerY - maxRadius * 0.1f,
                            centerX + maxRadius * 0.5f, centerY + maxRadius * 0.1f
                        )
                        cubicTo(
                            centerX + maxRadius * 0.5f, centerY + maxRadius * 0.15f,
                            centerX + maxRadius * 0.48f, centerY + maxRadius * 0.15f,
                            centerX + maxRadius * 0.48f, centerY + maxRadius * 0.15f
                        )
                    }
                    drawPath(
                        path = questionPath,
                        color = TALAColor.NightBlue,
                        style = Stroke(width = maxRadius * 0.05f)
                    )
                }
                else -> {
                    // Default wave animation
                    drawStar(
                        centerX = centerX,
                        centerY = centerY,
                        radius = maxRadius,
                        color = accent
                    )
                    // Waving arm
                    val armPath = Path().apply {
                        moveTo(centerX, centerY + maxRadius * 0.2f)
                        arcTo(
                            Rect(
                                left = centerX - maxRadius * 0.5f,
                                top = centerY + maxRadius * 0.1f,
                                right = centerX + maxRadius * 0.4f,
                                bottom = centerY + maxRadius * 0.5f
                            ),
                            startAngleDegrees = 30f,
                            sweepAngleDegrees = 180f,
                            forceMoveTo = false
                        )
                    }
                    drawPath(
                        path = armPath,
                        color = TALAColor.StarYellow,
                        style = Stroke(width = maxRadius * 0.1f)
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawStar(
    centerX: Float,
    centerY: Float,
    radius: Float,
    color: Color
) {
    val spikeCount = 5
    val outerRadius = radius
    val innerRadius = radius * 0.4f

    val starPath = Path().apply {
        var angle = Math.PI.toFloat() / 2
        val step = (Math.PI / spikeCount).toFloat()
        moveTo(
            centerX + cos(angle) * outerRadius,
            centerY + sin(angle) * outerRadius
        )

        for (i in 0 until spikeCount) {
            val outerX = centerX + cos(angle) * outerRadius
            val outerY = centerY + sin(angle) * outerRadius
            lineTo(outerX, outerY)
            angle += step

            val innerX = centerX + cos(angle) * innerRadius
            val innerY = centerY + sin(angle) * innerRadius
            lineTo(innerX, innerY)
            angle += step
        }
        close()
    }

    drawPath(starPath, color = color)
}

private fun DrawScope.drawCircleWithX(
    color: Color,
    radius: Float,
    colorCenter: Color,
    center: Offset
) {
    drawCircle(
        color = colorCenter,
        radius = radius * 1.5f,
        center = center
    )
    drawCircle(
        color = color,
        radius = radius * 0.2f,
        center = center
    )

    // Draw X in middle
    val crossPath = Path().apply {
        moveTo(center.x - radius * 0.3f, center.y - radius * 0.3f)
        lineTo(center.x + radius * 0.3f, center.y + radius * 0.3f)
        moveTo(center.x + radius * 0.3f, center.y - radius * 0.3f)
        lineTo(center.x - radius * 0.3f, center.y + radius * 0.3f)
    }
    drawPath(crossPath, color = color, style = Stroke(width = radius * 0.1f))
}
