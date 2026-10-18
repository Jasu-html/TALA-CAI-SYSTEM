package com.capstone.tala.feature.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.capstone.tala.core.designsystem.components.LevelNodeState
import com.capstone.tala.core.ui.theme.TALAColor
import com.capstone.tala.feature.home.components.ComingSoonPanel
import com.capstone.tala.feature.home.components.ConstellationStrip
import com.capstone.tala.feature.home.components.HomeBottomNav
import com.capstone.tala.feature.home.components.HomeTopBar
import com.capstone.tala.feature.home.components.LevelPopup
import com.capstone.tala.feature.home.components.WindingLevelPath

@Composable
fun HomeScreen(
    studentId: String = "",
    onNavigateToLevel: (moduleId: String, levelIndex: Int) -> Unit = { _, _ -> },
    viewModel: HomeViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val modules by viewModel.modules.collectAsState()
    val wallet by viewModel.wallet.collectAsState()

    var selectedTab by rememberSaveable { mutableStateOf(HomeTab.PATH) }
    var selectedLevel by rememberSaveable { mutableStateOf<Pair<String, Int>?>(null) }

    // Back handler: return to Path tab when on other tabs
    BackHandler(enabled = selectedTab != HomeTab.PATH) {
        selectedTab = HomeTab.PATH
    }

    Scaffold(
        topBar = {
            HomeTopBar(
                studentName = studentId,
                walletStars = wallet?.balance ?: 0
            )
        },
        bottomBar = {
            HomeBottomNav(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { paddingValues ->
        when (selectedTab) {
            HomeTab.PATH -> {
                StarPathContent(
                    modules = modules,
                    paddingValues = paddingValues,
                    onLevelClick = { moduleId, levelNumber ->
                        selectedLevel = moduleId to levelNumber
                    }
                )
            }
            HomeTab.CLOSET -> {
                ComingSoonPanel(
                    tabName = "Closet",
                    message = "Customize your avatar with items you earn. Coming in a future update!"
                )
            }
            HomeTab.SHOP -> {
                ComingSoonPanel(
                    tabName = "Star Shop",
                    message = "Spend your stars on avatar items and power-ups. Coming soon!"
                )
            }
            HomeTab.SKY_MAP -> {
                ComingSoonPanel(
                    tabName = "Sky Map",
                    message = "Explore your progress across all worlds. Coming in a future update!"
                )
            }
        }
    }

    // Level popup dialog
    selectedLevel?.let { (moduleId, levelNumber) ->
        val module = modules.find { it.id == moduleId }
        if (module != null) {
            val levelProgress = module.levelProgress.find { it.levelIndex == levelNumber }
            val bestStars = levelProgress?.bestStars ?: 0
            val state = HomePathLogic.nodeStateFor(
                level = levelNumber,
                currentLevel = module.currentLevel,
                completedLevels = module.completedLevels
            )
            val isLocked = state == LevelNodeState.LOCKED

            LevelPopup(
                levelNumber = levelNumber,
                levelTitle = "Level $levelNumber",
                lessonText = module.description,
                guideName = module.guide.name,
                accent = module.accent,
                bestStars = bestStars,
                isLocked = isLocked,
                onDismiss = { selectedLevel = null },
                onPlay = {
                    selectedLevel = null
                    onNavigateToLevel(moduleId, levelNumber)
                }
            )
        }
    }
}

@Composable
private fun StarPathContent(
    modules: List<HomeModuleData>,
    paddingValues: PaddingValues,
    onLevelClick: (moduleId: String, levelNumber: Int) -> Unit
) {
    if (modules.isEmpty()) {
        // Loading or empty state
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = TALAColor.StarYellow)
        }
        return
    }

    val totalLevels = modules.sumOf { it.levels }
    val completedLevels = modules.sumOf { it.completedLevels }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TALAColor.BackgroundCream),
        contentPadding = PaddingValues(
            top = paddingValues.calculateTopPadding() + 16.dp,
            bottom = paddingValues.calculateBottomPadding() + 16.dp,
            start = 16.dp,
            end = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Constellation strip at top
        item {
            ConstellationStrip(
                totalLevels = totalLevels,
                completedLevels = completedLevels
            )
        }

        // Each module with banner + winding path
        items(modules) { module ->
            ModuleSection(
                module = module,
                onLevelClick = onLevelClick
            )
        }

        // Bottom spacer
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ModuleSection(
    module: HomeModuleData,
    onLevelClick: (moduleId: String, levelNumber: Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Module banner
        ModuleBanner(
            moduleName = module.name,
            description = module.description,
            accent = module.accent,
            completedLevels = module.completedLevels,
            totalLevels = module.levels
        )

        // Winding level path
        val nodeStates = (1..module.levels).map { levelNumber ->
            HomePathLogic.nodeStateFor(
                level = levelNumber,
                currentLevel = module.currentLevel,
                completedLevels = module.completedLevels
            )
        }

        val stars = (1..module.levels).map { levelNumber ->
            module.levelProgress.find { it.levelIndex == levelNumber }?.bestStars ?: 0
        }

        WindingLevelPath(
            levels = module.levels,
            nodeStates = nodeStates,
            stars = stars,
            onLevelClick = { levelNumber ->
                onLevelClick(module.id, levelNumber)
            }
        )
    }
}

@Composable
private fun ModuleBanner(
    moduleName: String,
    description: String,
    accent: androidx.compose.ui.graphics.Color,
    completedLevels: Int,
    totalLevels: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = accent.copy(alpha = 0.12f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = moduleName,
                    style = MaterialTheme.typography.titleLarge,
                    color = TALAColor.NightBlue,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TALAColor.OnSurfaceVariant
                )
            }

            // Progress chip
            Surface(
                color = accent,
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "$completedLevels/$totalLevels",
                    style = MaterialTheme.typography.labelLarge,
                    color = TALAColor.NightBlue,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}
