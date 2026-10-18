package com.capstone.tala.feature.level

import kotlinx.serialization.Serializable

/**
 * State for a single practice question within a level.
 * Tracks attempts and first-try correctness for progress recording.
 */
@Serializable
data class PracticeQuestion(
    val prompt: String,
    val choices: List<String>,
    val correctAnswer: String,
    val itemKey: String, // e.g., "letter_A", "number_3"
    var attempts: Int = 0,
    var firstTryCorrect: Boolean = false,
    var hintsUsed: Int = 0,
    var completed: Boolean = false
)

/**
 * Practice phase state tracking all questions and current progress.
 */
@Serializable
data class PracticeSessionState(
    val questions: List<PracticeQuestion>,
    val currentQuestionIndex: Int = 0,
    val startTimeMs: Long = System.currentTimeMillis()
) {
    val currentQuestion: PracticeQuestion?
        get() = questions.getOrNull(currentQuestionIndex)

    val isComplete: Boolean
        get() = currentQuestionIndex >= questions.size

    val totalQuestions: Int
        get() = questions.size

    val completedQuestions: Int
        get() = questions.count { it.completed }

    /**
     * Calculate first-try accuracy percentage for star calculation.
     */
    val accuracyPercent: Int
        get() {
            val total = questions.size
            if (total == 0) return 0
            val correct = questions.count { it.firstTryCorrect }
            return (correct * 100) / total
        }
}

/**
 * Feedback state shown after answering a question.
 */
sealed interface AnswerFeedback {
    data object None : AnswerFeedback
    data object Correct : AnswerFeedback
    data class Incorrect(val attemptsRemaining: Int) : AnswerFeedback
    data object Revealed : AnswerFeedback // After 3 misses
}
