package com.capstone.tala.feature.level

/**
 * Phase within a level: Lesson (learn), Demo (watch), Practice (apply), Complete (celebrate).
 * Matches the two-segment top bar pattern from the design system.
 */
enum class LevelPhase(val label: String) {
    LESSON("Lesson"),
    DEMO("Demo"),
    PRACTICE("Practice"),
    COMPLETE("Complete")
}

/**
 * State of the level flow screen.
 */
sealed interface LevelFlowState {
    data object Loading : LevelFlowState
    data object ContentNotFound : LevelFlowState

    data class Ready(
        val studentId: String,
        val moduleId: String,
        val moduleName: String,
        val levelIndex: Int,
        val levelTitle: String,
        val lessonText: String,
        val guideName: String,
        val currentPhase: LevelPhase,
        val activityType: String,
        // Practice session state
        val practiceSession: PracticeSessionState? = null,
        // Current answer feedback
        val answerFeedback: AnswerFeedback = AnswerFeedback.None,
        // Hint state
        val hintActive: Boolean = false,
        val hintsRemaining: Int = Int.MAX_VALUE, // Unlimited by default
        // Completion state
        val starsEarned: Int = 0,
        val isCompleted: Boolean = false
    ) : LevelFlowState
}
