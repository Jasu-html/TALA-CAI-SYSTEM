package com.capstone.tala.feature.level

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.tala.data.content.TalaContentLoader
import com.capstone.tala.data.repository.ActivityRecord
import com.capstone.tala.data.repository.ProgressRecorder
import com.capstone.tala.data.repository.ProgressScoring
import com.capstone.tala.data.repository.StudentProgressRepository
import com.capstone.tala.domain.content.ModuleId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the level flow screen (Lesson -> Demo -> Practice -> Complete).
 *
 * Phase 6 complete implementation:
 * - Multi-question practice with validation
 * - No-fail rules (try again, max 3 attempts)
 * - Hint system with idle timer
 * - Progress recording with ProgressRecorder
 * - Level Complete celebration
 */
@HiltViewModel
class LevelFlowViewModel @Inject constructor(
    private val contentLoader: TalaContentLoader,
    private val repository: StudentProgressRepository,
    private val progressRecorder: ProgressRecorder,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val moduleId: String = savedStateHandle["moduleId"] ?: ""
    private val levelIndex: Int = savedStateHandle.get<String>("levelIndex")?.toIntOrNull() ?: 1
    private val studentId: String = savedStateHandle["studentId"] ?: ""

    private val _state = MutableStateFlow<LevelFlowState>(LevelFlowState.Loading)
    val state: StateFlow<LevelFlowState> = _state.asStateFlow()

    private var hintTimerJob: Job? = null
    private val hintIdleSeconds = 8 // TODO: Load from settings

    init {
        loadLevelContent()
    }

    private fun loadLevelContent() {
        viewModelScope.launch {
            try {
                val module = contentLoader.loadModule(moduleId)
                val level = module.levels.find { it.index == levelIndex }

                if (level == null) {
                    _state.value = LevelFlowState.ContentNotFound
                    return@launch
                }

                // Build practice questions (for now, just one from the level)
                // Phase 7+ will expand to multiple questions per level
                val questions = listOf(
                    PracticeQuestion(
                        prompt = level.prompt,
                        choices = level.choices,
                        correctAnswer = level.correctAnswer,
                        itemKey = "${moduleId.lowercase()}_level_$levelIndex"
                    )
                )

                val practiceSession = PracticeSessionState(questions = questions)

                _state.value = LevelFlowState.Ready(
                    studentId = studentId,
                    moduleId = moduleId,
                    moduleName = module.title,
                    levelIndex = levelIndex,
                    levelTitle = level.title,
                    lessonText = level.lessonText,
                    guideName = level.guide,
                    currentPhase = LevelPhase.LESSON,
                    activityType = "multiple_choice",
                    practiceSession = practiceSession
                )
            } catch (e: Exception) {
                _state.value = LevelFlowState.ContentNotFound
            }
        }
    }

    /**
     * Move to a specific phase.
     */
    fun moveToPhase(phase: LevelPhase) {
        val current = _state.value
        if (current is LevelFlowState.Ready) {
            _state.value = current.copy(currentPhase = phase)

            // Start hint timer when entering Practice
            if (phase == LevelPhase.PRACTICE) {
                startHintTimer()
            } else {
                cancelHintTimer()
            }
        }
    }

    /**
     * Student selected an answer choice.
     */
    fun onAnswerSelected(choice: String) {
        val current = _state.value as? LevelFlowState.Ready ?: return
        val session = current.practiceSession ?: return
        val question = session.currentQuestion ?: return

        cancelHintTimer() // Stop hint timer on interaction

        val isCorrect = choice == question.correctAnswer
        question.attempts++

        if (isCorrect) {
            // Correct answer!
            if (question.attempts == 1) {
                question.firstTryCorrect = true
            }
            question.completed = true

            _state.value = current.copy(
                answerFeedback = AnswerFeedback.Correct,
                hintActive = false
            )

            // Move to next question after a short delay
            viewModelScope.launch {
                delay(1500)
                moveToNextQuestion()
            }
        } else {
            // Wrong answer
            if (question.attempts >= 3) {
                // Max attempts reached - reveal answer and move on
                question.completed = true
                _state.value = current.copy(
                    answerFeedback = AnswerFeedback.Revealed,
                    hintActive = false
                )

                viewModelScope.launch {
                    delay(2000)
                    moveToNextQuestion()
                }
            } else {
                // Show "Try again!" feedback
                val remaining = 3 - question.attempts
                _state.value = current.copy(
                    answerFeedback = AnswerFeedback.Incorrect(remaining)
                )

                // Clear feedback and restart hint timer
                viewModelScope.launch {
                    delay(1500)
                    val updated = _state.value as? LevelFlowState.Ready ?: return@launch
                    _state.value = updated.copy(answerFeedback = AnswerFeedback.None)
                    startHintTimer()
                }
            }
        }
    }

    /**
     * Student tapped the hint bulb.
     */
    fun onHintRequested() {
        val current = _state.value as? LevelFlowState.Ready ?: return
        val session = current.practiceSession ?: return
        val question = session.currentQuestion ?: return

        question.hintsUsed++
        _state.value = current.copy(
            hintActive = true,
            hintsRemaining = current.hintsRemaining - 1
        )

        cancelHintTimer()

        // Clear hint after showing
        viewModelScope.launch {
            delay(3000)
            val updated = _state.value as? LevelFlowState.Ready ?: return@launch
            _state.value = updated.copy(hintActive = false)
            startHintTimer()
        }
    }

    /**
     * Move to the next question or complete the level.
     */
    private fun moveToNextQuestion() {
        val current = _state.value as? LevelFlowState.Ready ?: return
        val session = current.practiceSession ?: return

        val nextIndex = session.currentQuestionIndex + 1
        if (nextIndex >= session.questions.size) {
            // All questions complete - record progress and show Level Complete
            completeLevel()
        } else {
            // Move to next question
            val updatedSession = session.copy(currentQuestionIndex = nextIndex)
            _state.value = current.copy(
                practiceSession = updatedSession,
                answerFeedback = AnswerFeedback.None,
                hintActive = false
            )
            startHintTimer()
        }
    }

    /**
     * Complete the level: calculate stars, record progress, show celebration.
     */
    private fun completeLevel() {
        val current = _state.value as? LevelFlowState.Ready ?: return
        val session = current.practiceSession ?: return

        val accuracy = session.accuracyPercent
        val stars = ProgressScoring.starsForAccuracy(accuracy)

        viewModelScope.launch {
            try {
                // Record each question result
                session.questions.forEach { question ->
                    val record = ActivityRecord(
                        studentId = studentId,
                        moduleId = moduleId,
                        levelIndex = levelIndex,
                        activityType = current.activityType,
                        itemKey = question.itemKey,
                        moduleLevelCount = ModuleId.fromId(moduleId)?.expectedLevels ?: 20,
                        firstTryCorrect = question.firstTryCorrect,
                        attempts = question.attempts,
                        hintsUsed = question.hintsUsed,
                        durationMs = System.currentTimeMillis() - session.startTimeMs,
                        difficulty = "medium",
                        accuracyPercent = accuracy
                    )
                    progressRecorder.recordActivity(record)
                }

                // Show Level Complete screen
                _state.value = current.copy(
                    currentPhase = LevelPhase.COMPLETE,
                    starsEarned = stars,
                    isCompleted = true
                )
            } catch (e: Exception) {
                // Handle error - for now just move to complete anyway
                _state.value = current.copy(
                    currentPhase = LevelPhase.COMPLETE,
                    starsEarned = stars,
                    isCompleted = true
                )
            }
        }

        cancelHintTimer()
    }

    /**
     * Start the idle hint timer (triggers after 8 seconds of no interaction).
     */
    private fun startHintTimer() {
        cancelHintTimer()
        hintTimerJob = viewModelScope.launch {
            delay(hintIdleSeconds * 1000L)
            onHintRequested() // Auto-trigger hint
        }
    }

    /**
     * Cancel the hint timer.
     */
    private fun cancelHintTimer() {
        hintTimerJob?.cancel()
        hintTimerJob = null
    }

    /**
     * Retry loading content after an error.
     */
    fun retry() {
        _state.value = LevelFlowState.Loading
        loadLevelContent()
    }

    override fun onCleared() {
        super.onCleared()
        cancelHintTimer()
    }
}
