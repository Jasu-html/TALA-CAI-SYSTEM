package com.capstone.tala.feature.teacher

data class TeacherSetup(
    val schoolName: String = "Tala Academy",
    val classSection: String = "Grade 1",
    val pin: String = "2468",
    val recoveryCode: String = ""
)

object TeacherSetupLogic {
    fun createRecoveryCode(): String =
        (1..8)
            .map { (('A'..'Z')).random() }
            .joinToString("")

    fun sampleClassStudents(): List<String> = listOf("Mia", "Leo", "Ayla", "Jun", "Bea")

    fun createDefaultSetup(): TeacherSetup = TeacherSetup(
        schoolName = "Tala Academy",
        classSection = "Grade 1",
        pin = "2468",
        recoveryCode = createRecoveryCode()
    )
}
