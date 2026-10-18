package com.capstone.tala.core.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.capstone.tala.core.ui.theme.TALAColor
import kotlin.math.cos
import kotlin.math.sin

enum class LevelNodeState {
    LOCKED,
    CURRENT,
    COMPLETED
}

@Composable
fun TwoSegmentTopBar(
    currentSegment: String,
    onSegmentChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val segments = listOf("Lesson", "Practice")

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        color = TALAColor.SurfaceCream,
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            segments.forEach { segment ->
                val selected = segment == currentSegment
                val background = if (selected) TALAColor.StarYellow else Color.Transparent
                val contentColor = if (selected) TALAColor.NightBlue else TALAColor.NightBlue.copy(alpha = 0.7f)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(background)
                        .clickable { onSegmentChange(segment) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = segment,
                        style = MaterialTheme.typography.labelLarge,
                        color = contentColor,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun StarWallet(
    stars: Int,
    label: String = "Star Wallet",
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
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
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = TALAColor.NightBlue,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = "$label: $stars",
                style = MaterialTheme.typography.labelLarge,
                color = TALAColor.NightBlue,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ProgressDots(
    total: Int,
    current: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(total) { index ->
            val active = index < current
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        if (active) TALAColor.MintGreen else TALAColor.NightBlue.copy(alpha = 0.2f),
                        CircleShape
                    )
                    .border(
                        width = 1.dp,
                        color = if (active) TALAColor.MintGreen else TALAColor.NightBlue.copy(alpha = 0.25f),
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
fun TalaLevelNode(
    level: Int,
    state: LevelNodeState,
    stars: Int = 0,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val fillColor = when (state) {
        LevelNodeState.LOCKED -> TALAColor.NightBlue.copy(alpha = 0.15f)
        LevelNodeState.CURRENT -> TALAColor.StarYellow
        LevelNodeState.COMPLETED -> TALAColor.MintGreen
    }
    val borderColor = when (state) {
        LevelNodeState.LOCKED -> TALAColor.NightBlue.copy(alpha = 0.3f)
        LevelNodeState.CURRENT -> TALAColor.StarYellow
        LevelNodeState.COMPLETED -> TALAColor.MintGreen
    }
    val textColor = when (state) {
        LevelNodeState.LOCKED -> TALAColor.NightBlue.copy(alpha = 0.5f)
        LevelNodeState.CURRENT -> TALAColor.NightBlue
        LevelNodeState.COMPLETED -> TALAColor.NightBlue
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(fillColor)
                .border(3.dp, borderColor, CircleShape)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (state == LevelNodeState.LOCKED) "🔒" else level.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(5) { index ->
                val lit = index < stars
                androidx.compose.material3.Icon(
                    imageVector = if (lit) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = null,
                    tint = if (lit) TALAColor.StarYellow else TALAColor.NightBlue.copy(alpha = 0.25f),
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
fun DesignSystemGallery(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
    ) {
        item {
            Text(
                text = "TALA Design System",
                style = MaterialTheme.typography.headlineLarge,
                color = TALAColor.NightBlue,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Text(
                text = "Palette",
                style = MaterialTheme.typography.titleLarge,
                color = TALAColor.NightBlue,
                fontWeight = FontWeight.Bold
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    listOf(
                        "Cream" to TALAColor.BackgroundCream,
                        "Surface" to TALAColor.SurfaceElevated,
                        "Night Blue" to TALAColor.NightBlue,
                        "Star Yellow" to TALAColor.StarYellow,
                        "Sky Blue" to TALAColor.SkyBlue,
                        "Mint" to TALAColor.MintGreen,
                        "Phonics" to TALAColor.PhonicsAccent,
                        "Shapes" to TALAColor.ShapesAccent
                    )
                ) { (name, color) -> PaletteChip(name, color) }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Starlight backdrop", style = MaterialTheme.typography.titleLarge, color = TALAColor.NightBlue)
                StarrySky(
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    reducedMotion = false
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TalaCharacterView("Tala", "wave", Modifier.size(148.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Every learner shines", style = MaterialTheme.typography.titleLarge, color = TALAColor.StarYellow)
                            Text("A shared surface with contrast", style = MaterialTheme.typography.bodyMedium, color = TALAColor.BackgroundCream)
                        }
                    }
                }
                TalaCard(Modifier.fillMaxWidth(), tonal = true) {
                    Text("Elevated 28dp card", Modifier.padding(24.dp), style = MaterialTheme.typography.titleMedium)
                }
            }
        }

        item {
            Text(
                text = "Buttons",
                style = MaterialTheme.typography.titleLarge,
                color = TALAColor.NightBlue,
                fontWeight = FontWeight.Bold
            )
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TalaPrimaryButton(text = "I'm ready!", onClick = {}, modifier = Modifier.fillMaxWidth())
                TalaSecondaryButton(text = "Choose another", onClick = {}, modifier = Modifier.fillMaxWidth())
                TalaTextButton(text = "Not now", onClick = {}, modifier = Modifier.fillMaxWidth())
                SpeakerButton(onClick = {}, modifier = Modifier.fillMaxWidth())
            }
        }

        item {
            Text(
                text = "Interaction Helpers",
                style = MaterialTheme.typography.titleLarge,
                color = TALAColor.NightBlue,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BigIconButton(icon = Icons.Default.Star, contentDescription = "Star action", onClick = {})
                HintBulb(onClick = {})
                GhostHand()
            }
        }

        item {
            Text(
                text = "Characters",
                style = MaterialTheme.typography.titleLarge,
                color = TALAColor.NightBlue,
                fontWeight = FontWeight.Bold
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { TalaCharacterView(characterName = "Tala", currentState = "wave") }
                item { TalaCharacterView(characterName = "Likha", currentState = "thinking") }
                item { TalaCharacterView(characterName = "Bilang", currentState = "cheering") }
                item { TalaCharacterView(characterName = "Titik", currentState = "sleepy") }
            }
        }

        item {
            Text(
                text = "Level Path",
                style = MaterialTheme.typography.titleLarge,
                color = TALAColor.NightBlue,
                fontWeight = FontWeight.Bold
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                TalaLevelNode(level = 1, state = LevelNodeState.COMPLETED, stars = 5)
                }
                item {
                TalaLevelNode(level = 2, state = LevelNodeState.CURRENT, stars = 3)
                }
                item {
                TalaLevelNode(level = 3, state = LevelNodeState.LOCKED, stars = 0)
                }
            }
        }

        item {
            Text(
                text = "Progress and Wallet",
                style = MaterialTheme.typography.titleLarge,
                color = TALAColor.NightBlue,
                fontWeight = FontWeight.Bold
            )
            Column(
                modifier = Modifier.padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StarWallet(stars = 128)
                ProgressDots(total = 5, current = 3)
                TwoSegmentTopBar(currentSegment = "Lesson", onSegmentChange = {})
            }
        }

        item {
            TalaCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Typography sample",
                        style = MaterialTheme.typography.titleLarge,
                        color = TALAColor.NightBlue,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "TALA teaches through voice and play.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TALAColor.OnSurface
                    )
                    Text(
                        text = "Big buttons, clear words, and cheerful guides help children learn without pressure.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TALAColor.OnSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun PaletteChip(
    label: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(color, CircleShape)
                .border(2.dp, TALAColor.NightBlue.copy(alpha = 0.18f), CircleShape)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = TALAColor.NightBlue
        )
    }
}

@Composable
fun SmallSparkles(
    modifier: Modifier = Modifier,
    color: Color = TALAColor.StarYellow
) {
    Canvas(modifier = modifier.size(72.dp)) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        for (i in 0 until 8) {
            val angle = (i * 45f) * (Math.PI / 180)
            val x2 = centerX + cos(angle.toDouble()).toFloat() * 18f
            val y2 = centerY + sin(angle.toDouble()).toFloat() * 18f
            drawLine(
                color = color,
                start = Offset(centerX, centerY),
                end = Offset(x2, y2),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )
        }
        drawCircle(
            color = color.copy(alpha = 0.25f),
            radius = 22f,
            center = Offset(centerX, centerY),
            style = Stroke(width = 2f)
        )
    }
}

@Composable
private fun SparklePath(
    modifier: Modifier = Modifier,
    color: Color = TALAColor.StarYellow
) {
    Canvas(modifier = modifier.size(80.dp)) {
        val path = Path().apply {
            moveTo(size.width * 0.5f, size.height * 0.1f)
            lineTo(size.width * 0.58f, size.height * 0.45f)
            lineTo(size.width * 0.9f, size.height * 0.5f)
            lineTo(size.width * 0.58f, size.height * 0.55f)
            lineTo(size.width * 0.5f, size.height * 0.9f)
            lineTo(size.width * 0.42f, size.height * 0.55f)
            lineTo(size.width * 0.1f, size.height * 0.5f)
            lineTo(size.width * 0.42f, size.height * 0.45f)
            close()
        }
        drawPath(path = path, color = color.copy(alpha = 0.8f), style = Stroke(width = 3f))
    }
}
