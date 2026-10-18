package com.capstone.tala.core.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.capstone.tala.core.ui.theme.TALAColor

@Composable
fun AdaptiveStage(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val maxWidth = maxWidth
        val safeWidth = if (maxWidth < 600.dp) maxWidth else maxWidth * 0.82f

        Box(
            modifier = Modifier
                .widthIn(max = safeWidth)
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            content()
        }
    }
}

@Composable
fun StagePanel(
    title: String,
    subtitle: String,
    accent: Color,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = TALAColor.SurfaceCream,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = TALAColor.NightBlue,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TALAColor.NightBlue.copy(alpha = 0.75f)
                    )
                }
                Box(
                    modifier = Modifier
                        .background(accent.copy(alpha = 0.22f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Live",
                        style = MaterialTheme.typography.labelLarge,
                        color = TALAColor.NightBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            content()
        }
    }
}

@Composable
fun WorldBanner(
    title: String,
    subtitle: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = accent.copy(alpha = 0.16f),
        shape = RoundedCornerShape(22.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = TALAColor.NightBlue,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TALAColor.NightBlue.copy(alpha = 0.75f)
                )
            }

            Box(
                modifier = Modifier
                    .background(accent, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Play",
                    style = MaterialTheme.typography.labelLarge,
                    color = TALAColor.NightBlue,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AdaptiveCardRow(
    modifier: Modifier = Modifier,
    spacing: Dp = 12.dp,
    content: @Composable () -> Unit
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val columns = if (maxWidth < 500.dp) 1 else if (maxWidth < 840.dp) 2 else 3

        when (columns) {
            1 -> Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(spacing)
            ) {
                content()
            }
            2 -> Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                Box(modifier = Modifier.weight(1f)) { content() }
                Box(modifier = Modifier.weight(1f)) { content() }
            }
            else -> Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                Box(modifier = Modifier.weight(1f)) { content() }
                Box(modifier = Modifier.weight(1f)) { content() }
                Box(modifier = Modifier.weight(1f)) { content() }
            }
        }
    }
}

@Composable
fun GhostHand(
    modifier: Modifier = Modifier,
    color: Color = TALAColor.SkyBlue.copy(alpha = 0.7f)
) {
    Canvas(modifier = modifier.size(88.dp)) {
        val hand = Path().apply {
            moveTo(size.width * 0.16f, size.height * 0.70f)
            lineTo(size.width * 0.22f, size.height * 0.34f)
            lineTo(size.width * 0.31f, size.height * 0.20f)
            lineTo(size.width * 0.42f, size.height * 0.18f)
            lineTo(size.width * 0.54f, size.height * 0.28f)
            lineTo(size.width * 0.59f, size.height * 0.22f)
            lineTo(size.width * 0.72f, size.height * 0.34f)
            lineTo(size.width * 0.78f, size.height * 0.62f)
            lineTo(size.width * 0.55f, size.height * 0.72f)
            lineTo(size.width * 0.34f, size.height * 0.72f)
            close()
        }

        drawPath(path = hand, color = color.copy(alpha = 0.8f), style = Stroke(width = 6f))

        repeat(5) { index ->
            val x = size.width * (0.22f + index * 0.13f)
            val startY = size.height * (0.34f + index * 0.05f)
            val endY = size.height * (0.18f + index * 0.04f)
            drawLine(
                color = color.copy(alpha = 0.8f),
                start = Offset(x, startY),
                end = Offset(x + 6f, endY),
                strokeWidth = 5f
            )
        }
    }
}
