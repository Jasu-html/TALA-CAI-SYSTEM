package com.capstone.tala.feature.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.capstone.tala.core.designsystem.components.LevelNodeState
import com.capstone.tala.core.designsystem.components.TalaLevelNode
import com.capstone.tala.core.ui.theme.TALAColor
import kotlin.math.max

/**
 * Winding level path with S-curve connectors between level nodes.
 *
 * Nodes are laid out vertically and offset horizontally in a serpentine pattern:
 * [left, center, right, center] repeating. Connector lines are drawn with bezier curves.
 *
 * @param levels Number of levels in the module
 * @param nodeStates State of each level (index 0 = level 1)
 * @param stars Stars earned per level (index 0 = level 1)
 * @param onLevelClick Callback when a node is tapped
 * @param modifier Modifier for layout
 */
@Composable
fun WindingLevelPath(
    levels: Int,
    nodeStates: List<LevelNodeState>,
    stars: List<Int>,
    onLevelClick: (levelNumber: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (levels <= 0) return

    val nodeSize = 72.dp
    val rowSpacing = 116.dp
    val totalHeight = rowSpacing * levels

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val width = maxWidth
        val anchors = listOf(0.18f, 0.5f, 0.82f, 0.5f)

        // Calculate node positions: (x offset, y offset)
        val positions = remember(levels, width, nodeSize) {
            (0 until levels).map { index ->
                val anchor = anchors[index % anchors.size]
                val x = (width - nodeSize) * anchor
                val y = rowSpacing * index
                Offset(x.value, y.value)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(totalHeight)
        ) {
            // Draw connectors between nodes
            Canvas(modifier = Modifier.fillMaxSize()) {
                if (positions.size < 2) return@Canvas

                val nodeCenterOffset = nodeSize.toPx() / 2f
                val rowSpacingPx = rowSpacing.toPx()

                for (i in 0 until positions.size - 1) {
                    val start = positions[i]
                    val end = positions[i + 1]

                    val startX = start.x + nodeCenterOffset
                    val startY = start.y + nodeCenterOffset
                    val endX = end.x + nodeCenterOffset
                    val endY = end.y + nodeCenterOffset

                    // Determine line style based on destination node state
                    val destinationState = nodeStates.getOrNull(i + 1) ?: LevelNodeState.LOCKED
                    val isLocked = destinationState == LevelNodeState.LOCKED
                    val lineColor = if (isLocked) {
                        TALAColor.NightBlue.copy(alpha = 0.2f)
                    } else {
                        TALAColor.StarYellow.copy(alpha = 0.7f)
                    }

                    // Draw bezier curve
                    val path = Path().apply {
                        moveTo(startX, startY)
                        val controlOffset = rowSpacingPx * 0.5f
                        cubicTo(
                            startX, startY + controlOffset,
                            endX, endY - controlOffset,
                            endX, endY
                        )
                    }

                    drawPath(
                        path = path,
                        color = lineColor,
                        style = Stroke(
                            width = 6f,
                            cap = StrokeCap.Round,
                            pathEffect = if (isLocked) {
                                PathEffect.dashPathEffect(floatArrayOf(16f, 14f))
                            } else {
                                null
                            }
                        )
                    )
                }
            }

            // Place level nodes
            positions.forEachIndexed { index, position ->
                val levelNumber = index + 1
                val state = nodeStates.getOrNull(index) ?: LevelNodeState.LOCKED
                val starCount = stars.getOrNull(index) ?: 0

                TalaLevelNode(
                    level = levelNumber,
                    state = state,
                    stars = starCount,
                    modifier = Modifier
                        .offset(x = position.x.dp, y = position.y.dp)
                        .size(nodeSize),
                    onClick = { onLevelClick(levelNumber) }
                )
            }
        }
    }
}
