package com.capstone.tala.domain.content

import androidx.compose.ui.graphics.Color
import com.capstone.tala.audio.NarrationCharacter
import com.capstone.tala.core.ui.theme.TALAColor

/**
 * Canonical module definitions for TALA.
 * Source: TALA_PROGRAMMING_GUIDE.md lines 31-84
 */
enum class ModuleId(
    val id: String,
    val displayName: String,
    val guide: NarrationCharacter,
    val accent: Color,
    val expectedLevels: Int
) {
    PHONICS(
        id = "PHONICS",
        displayName = "Phonics and Reading",
        guide = NarrationCharacter.TITIK,
        accent = Color(0xFF9C27B0), // Purple for Titik
        expectedLevels = 20
    ),
    NUMBERS(
        id = "NUMBERS",
        displayName = "Numbers",
        guide = NarrationCharacter.BILANG,
        accent = Color(0xFF2196F3), // Sky blue for Bilang
        expectedLevels = 15
    ),
    SHAPES_COLORS_ARTS(
        id = "SHAPES_COLORS_ARTS",
        displayName = "Shapes, Colors, Arts",
        guide = NarrationCharacter.LIKHA,
        accent = Color(0xFFFF7043), // Coral for Likha
        expectedLevels = 15
    ),
    VALUES(
        id = "VALUES",
        displayName = "Good Values",
        guide = NarrationCharacter.TALA,
        accent = TALAColor.StarYellow,
        expectedLevels = 10
    ),
    QUEST(
        id = "QUEST",
        displayName = "Quest Quiz",
        guide = NarrationCharacter.TALA,
        accent = TALAColor.StarYellow,
        expectedLevels = 10
    );

    companion object {
        fun all(): List<ModuleId> = values().toList()

        fun fromId(id: String): ModuleId? = values().find {
            it.id.equals(id, ignoreCase = true)
        }
    }
}
