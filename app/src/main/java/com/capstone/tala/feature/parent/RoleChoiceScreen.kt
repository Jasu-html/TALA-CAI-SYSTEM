package com.capstone.tala.feature.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.capstone.tala.core.designsystem.components.TalaPrimaryButton
import com.capstone.tala.core.designsystem.components.TalaSecondaryButton
import com.capstone.tala.core.designsystem.components.TalaCard
import com.capstone.tala.core.ui.theme.TALAColor

@Composable
fun RoleChoiceScreen(
    onRoleSelected: (String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(TALAColor.BackgroundCream),
        contentAlignment = Alignment.Center
    ) {
        TalaCard(
            modifier = Modifier
                .fillMaxWidth(if (maxWidth > 840.dp) 0.56f else 0.94f)
                .padding(16.dp),
            tonal = true
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Who are you?",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TALAColor.NightBlue,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Choose the grown-up area you want to open.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TALAColor.NightBlue.copy(alpha = 0.8f)
                )

                TalaPrimaryButton(
                    text = "Teacher",
                    onClick = { onRoleSelected("Teacher") },
                    modifier = Modifier.fillMaxWidth()
                )

                TalaSecondaryButton(
                    text = "Parent",
                    onClick = { onRoleSelected("Parent") },
                    modifier = Modifier.fillMaxWidth()
                )

                TalaSecondaryButton(
                    text = "Back",
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
