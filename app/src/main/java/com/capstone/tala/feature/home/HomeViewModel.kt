package com.capstone.tala.feature.home

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.tala.audio.NarrationCharacter
import com.capstone.tala.data.content.TalaContentLoader
import com.capstone.tala.data.db.LevelProgressEntity
import com.capstone.tala.data.db.StarWalletEntity
import com.capstone.tala.data.repository.StudentProgressRepository
import com.capstone.tala.domain.content.ModuleId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Data class representing a module's state for the home screen.
 * Replaces the hardcoded HomeModule with real data from content JSON and Room database.
 */
data class HomeModuleData(
    val id: String,
    val name: String,
    val guide: NarrationCharacter,
    val accent: Color,
    val levels: Int,
    val currentLevel: Int,
    val completedLevels: Int,
    val description: String,
    val levelProgress: List<LevelProgressEntity> // For per-level star display
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: StudentProgressRepository,
    private val contentLoader: TalaContentLoader,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val studentId: String = savedStateHandle["studentId"] ?: ""

    /**
     * Wallet balance from Room database.
     * Observed via Flow, updates automatically when stars are earned/spent.
     */
    val wallet: StateFlow<StarWalletEntity?> = repository.observeWallet(studentId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    /**
     * All 5 modules with content-driven level counts and database-driven progress.
     * Combines:
     * - Content JSON (title, level count, description)
     * - Room database (completed/current level, per-level stars)
     */
    val modules: StateFlow<List<HomeModuleData>> = loadAllModules()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private fun loadAllModules(): Flow<List<HomeModuleData>> = flow {
        val moduleDataList = ModuleId.all().map { moduleId ->
            loadModuleData(moduleId)
        }

        // Combine all module flows into one
        combine(moduleDataList) { array ->
            array.toList()
        }.collect { emit(it) }
    }

    private fun loadModuleData(moduleId: ModuleId): Flow<HomeModuleData> {
        return repository.getModuleProgress(studentId, moduleId.id).combine(
            flow { emit(contentLoader.loadModule(moduleId.id.lowercase())) }
        ) { progressList, content ->
            val totalLevels = content.levels.size
            val completedCount = progressList.count { it.bestStars > 0 }
            val currentLevelIndex = progressList.firstOrNull { it.bestStars == 0 }?.levelIndex ?: 1

            HomeModuleData(
                id = moduleId.id,
                name = content.title,
                guide = moduleId.guide,
                accent = moduleId.accent,
                levels = totalLevels,
                currentLevel = currentLevelIndex,
                completedLevels = completedCount,
                description = content.description,
                levelProgress = progressList
            )
        }
    }
}
