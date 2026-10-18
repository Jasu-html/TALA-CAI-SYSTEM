package com.capstone.tala.feature.home

import com.capstone.tala.core.designsystem.components.LevelNodeState
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests for HomePathLogic utility functions.
 * Tests the pure logic for deriving node states from progress data.
 */
class HomePathLogicTest {

    @Test
    fun `nodeStateFor returns COMPLETED for levels at or below completedLevels`() {
        // Level 1 is completed when completedLevels = 2
        assertEquals(
            LevelNodeState.COMPLETED,
            HomePathLogic.nodeStateFor(level = 1, currentLevel = 3, completedLevels = 2)
        )

        // Level 2 is completed when completedLevels = 2
        assertEquals(
            LevelNodeState.COMPLETED,
            HomePathLogic.nodeStateFor(level = 2, currentLevel = 3, completedLevels = 2)
        )
    }

    @Test
    fun `nodeStateFor returns CURRENT for the current level`() {
        assertEquals(
            LevelNodeState.CURRENT,
            HomePathLogic.nodeStateFor(level = 3, currentLevel = 3, completedLevels = 2)
        )
    }

    @Test
    fun `nodeStateFor returns LOCKED for levels beyond current`() {
        assertEquals(
            LevelNodeState.LOCKED,
            HomePathLogic.nodeStateFor(level = 4, currentLevel = 3, completedLevels = 2)
        )

        assertEquals(
            LevelNodeState.LOCKED,
            HomePathLogic.nodeStateFor(level = 5, currentLevel = 3, completedLevels = 2)
        )
    }

    @Test
    fun `nodeStateFor handles zero progress correctly`() {
        // No levels completed, level 1 is current
        assertEquals(
            LevelNodeState.CURRENT,
            HomePathLogic.nodeStateFor(level = 1, currentLevel = 1, completedLevels = 0)
        )

        // All other levels are locked
        assertEquals(
            LevelNodeState.LOCKED,
            HomePathLogic.nodeStateFor(level = 2, currentLevel = 1, completedLevels = 0)
        )
    }

    @Test
    fun `nodeStateFor handles all levels completed`() {
        // All 20 levels completed
        assertEquals(
            LevelNodeState.COMPLETED,
            HomePathLogic.nodeStateFor(level = 20, currentLevel = 20, completedLevels = 20)
        )
    }
}
