package com.capstone.tala.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.capstone.tala.core.ui.theme.TALAColor

private data class CharacterSpec(
    val name: String,
    val accent: Color,
    val states: List<String>
)

@Composable
fun CharacterStateGallery(
    modifier: Modifier = Modifier
) {
    val specs = listOf(
        CharacterSpec("Tala", TALAColor.StarYellow, listOf("wave", "talking", "thinking", "cheering", "sleepy")),
        CharacterSpec("Likha", TALAColor.Coral, listOf("wave", "talking", "thinking", "cheering", "sleepy")),
        CharacterSpec("Bilang", TALAColor.SkyBlue, listOf("wave", "talking", "thinking", "cheering", "sleepy")),
        CharacterSpec("Titik", TALAColor.Purple, listOf("wave", "talking", "thinking", "cheering", "sleepy"))
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        specs.forEach { spec ->
            Surface(
                color = TALAColor.SurfaceCream,
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = spec.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = TALAColor.NightBlue,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        spec.states.forEach { state ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .background(spec.accent.copy(alpha = 0.16f), CircleShape)
                                        .border(2.dp, spec.accent.copy(alpha = 0.4f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = state.take(1).uppercase(),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = TALAColor.NightBlue,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = state,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = TALAColor.NightBlue.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
