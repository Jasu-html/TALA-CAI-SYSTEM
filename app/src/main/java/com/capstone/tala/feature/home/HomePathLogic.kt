package com.capstone.tala.feature.home

import com.capstone.tala.core.designsystem.components.LevelNodeState

/**
 * Utility object for home screen level path logic.
 * Hardcoded demo data removed - now uses HomeViewModel with real database queries.
 */
object HomePathLogic {
    /**
     * Determines the visual state of a level node based on progress.
     * @param level The level index (1-based)
     * @param currentLevel The current level the student is on
     * @param completedLevels The count of completed levels
     * @return COMPLETED if already done, CURRENT if active, LOCKED if not yet available
     */
    fun nodeStateFor(level: Int, currentLevel: Int, completedLevels: Int): LevelNodeState {
        return when {
            level <= completedLevels -> LevelNodeState.COMPLETED
            level == currentLevel -> LevelNodeState.CURRENT
            else -> LevelNodeState.LOCKED
        }
    }
}
