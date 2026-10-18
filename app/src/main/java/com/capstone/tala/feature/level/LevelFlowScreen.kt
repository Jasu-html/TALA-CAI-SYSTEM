package com.capstone.tala.feature.level

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.capstone.tala.core.designsystem.components.TalaCard
import com.capstone.tala.core.designsystem.components.TalaCharacterView
import com.capstone.tala.core.designsystem.components.TalaPrimaryButton
import com.capstone.tala.core.designsystem.components.TalaSecondaryButton
import com.capstone.tala.core.designsystem.components.TwoSegmentTopBar
import com.capstone.tala.core.ui.theme.TALAColor

/**
 * Level flow screen showing Lesson and Practice phases.
 *
 * Phase 6 implementation: Basic structure with lesson text display
 * and multiple choice activity. Full activity types in Phase 7+.
 *
 * @param onComplete Callback when level is finished
 * @param onBack Callback for back navigation
 * @param viewModel Level flow ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelFlowScreen(
    onComplete: () -> Unit,
    onBack: () -> Unit,
    viewModel: LevelFlowViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    BackHandler(onBack = onBack)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    when (val s = state) {
                        is LevelFlowState.Ready -> {
                            Text(
                                text = "${s.moduleName} - Level ${s.levelIndex}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        else -> {
                            Text("Level", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TALAColor.SurfaceCream,
                    titleContentColor = TALAColor.NightBlue,
                    navigationIconContentColor = TALAColor.NightBlue
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(TALAColor.BackgroundCream)
        ) {
            when (val s = state) {
                is LevelFlowState.Loading -> {
                    LoadingContent()
                }
                is LevelFlowState.ContentNotFound -> {
                    ErrorContent(onRetry = viewModel::retry, onBack = onBack)
                }
                is LevelFlowState.Ready -> {
                    LevelContent(
                        state = s,
                        onPhaseChange = viewModel::moveToPhase,
                        onComplete = onComplete
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = TALAColor.StarYellow)
    }
}

@Composable
private fun ErrorContent(
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        TalaCard(modifier = Modifier.padding(24.dp)) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Content Not Found",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TALAColor.NightBlue,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "This level could not be loaded. Please try again.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TALAColor.OnSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                TalaPrimaryButton(
                    text = "Retry",
                    onClick = onRetry,
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

@Composable
private fun LevelContent(
    state: LevelFlowState.Ready,
    onPhaseChange: (LevelPhase) -> Unit,
    onComplete: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Phase selector
        TwoSegmentTopBar(
            currentSegment = state.currentPhase.label,
            onSegmentChange = { label ->
                val phase = LevelPhase.values().find { it.label == label }
                if (phase != null) {
                    onPhaseChange(phase)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Phase content
        when (state.currentPhase) {
            LevelPhase.LESSON -> {
                LessonPhase(
                    state = state,
                    onMoveToPhase = { onPhaseChange(LevelPhase.PRACTICE) }
                )
            }
            LevelPhase.PRACTICE -> {
                PracticePhase(
                    state = state,
                    onComplete = onComplete
                )
            }
        }
    }
}

@Composable
private fun LessonPhase(
    state: LevelFlowState.Ready,
    onMoveToPhase: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TalaCard {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TalaCharacterView(
                        characterName = state.guideName,
                        currentState = "talking",
                        modifier = Modifier.padding(16.dp)
                    )

                    Text(
                        text = state.levelTitle,
                        style = MaterialTheme.typography.headlineMedium,
                        color = TALAColor.NightBlue,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = state.lessonText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TALAColor.OnSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        item {
            TalaPrimaryButton(
                text = "Start Practice",
                onClick = onMoveToPhase,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun PracticePhase(
    state: LevelFlowState.Ready,
    onComplete: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TalaCard {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = state.prompt,
                        style = MaterialTheme.typography.titleLarge,
                        color = TALAColor.NightBlue,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    TalaCharacterView(
                        characterName = state.guideName,
                        currentState = "thinking",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        // Choice buttons
        items(state.choices.size) { index ->
            val choice = state.choices[index]
            TalaSecondaryButton(
                text = choice,
                onClick = {
                    // Phase 7: Record answer and show feedback
                    // For now, just mark as complete
                    onComplete()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
