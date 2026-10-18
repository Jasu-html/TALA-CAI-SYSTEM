package com.capstone.tala.feature.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.capstone.tala.core.designsystem.components.TalaCard
import com.capstone.tala.core.designsystem.components.TalaCharacterView
import com.capstone.tala.core.ui.theme.TALAColor

/**
 * Honest "Coming Soon" panel for unimplemented tabs (Closet, Shop, Sky Map).
 * Phase 13+ feature - no fake data displayed.
 *
 * @param tabName Name of the tab (e.g., "Closet", "Shop")
 * @param message Description of what will be available
 * @param modifier Modifier for layout
 */
@Composable
fun ComingSoonPanel(
    tabName: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        TalaCard(
            modifier = Modifier.padding(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TalaCharacterView(
                    characterName = "Tala",
                    currentState = "wave",
                    modifier = Modifier.padding(16.dp)
                )

                Text(
                    text = "$tabName Coming Soon",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TALAColor.NightBlue,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TALAColor.OnSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
