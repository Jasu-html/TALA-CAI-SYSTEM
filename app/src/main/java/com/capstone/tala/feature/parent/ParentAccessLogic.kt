package com.capstone.tala.feature.parent

data class MathChallenge(
    val prompt: String,
    val correctAnswer: Int
)

object ParentAccessLogic {
    const val DEFAULT_PIN = "2468"

    fun createMathChallenge(): MathChallenge {
        val left = (2..9).random()
        val right = (1..9).random()
        return MathChallenge(
            prompt = "$left + $right = ?",
            correctAnswer = left + right
        )
    }

    fun isMathAnswerCorrect(challenge: MathChallenge, answer: Int): Boolean =
        challenge.correctAnswer == answer

    fun isPinValid(input: String, expectedPin: String = DEFAULT_PIN): Boolean =
        input == expectedPin

    fun roleToRoute(role: String): String =
        when (role.trim().lowercase()) {
            "teacher" -> "teacher"
            "parent" -> "parent"
            else -> "home"
        }
}
