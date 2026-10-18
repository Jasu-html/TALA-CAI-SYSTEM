package com.capstone.tala.audio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.capstone.tala.core.ui.theme.TALAColor

@Composable
fun SubtitleBar(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(TALAColor.NightBlue.copy(alpha = 0.08f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text.ifBlank { "Listening..." },
            style = MaterialTheme.typography.bodyLarge,
            color = TALAColor.NightBlue,
            fontWeight = FontWeight.SemiBold
        )
    }
}
