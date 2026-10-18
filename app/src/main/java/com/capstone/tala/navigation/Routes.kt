package com.capstone.tala.navigation

object Routes {
    const val Splash = "splash"
    const val Login = "login"

    // Home
    const val Home = "home"

    // Parent / Teacher access
    const val ParentGate = "parent_gate"
    const val ParentPin = "parent_pin"
    const val RoleChoice = "role_choice"
    const val TeacherSetup = "teacher_setup"

    // Story intro
    const val StoryIntro = "story_intro"

    // Level Flow
    const val LevelFlow = "level_flow"

    // Helper functions for level routes
    fun levelFlow(studentId: String, moduleId: String, levelIndex: Int): String {
        return "level_flow/$studentId/$moduleId/$levelIndex"
    }

    // Modules
    const val Phonics = "phonics"
    const val PhonicsLevel = "phonics_{levelIndex}"
    const val Numbers = "numbers"
    const val NumbersLevel = "numbers_{levelIndex}"
    const val ShapesColorsArts = "shapes_colors_arts"
    const val ShapesColorsArtsLevel = "shapes_colors_arts_{levelIndex}"
    const val Values = "values"
    const val ValuesLevel = "values_{levelIndex}"
    const val Quest = "quest"
    const val QuestRound = "quest_{roundIndex}"

    // Rewards
    const val StarWallet = "star_wallet"
    const val StarShop = "star_shop"
    const val AvatarCloset = "avatar_closet"
    const val AvatarCreator = "avatar_creator"

    // Dashboards
    const val TeacherDashboard = "teacher_dashboard"
    const val ParentDashboard = "parent_dashboard"

    // Settings
    const val Settings = "settings"

    // Phase 2 debug utility
    const val AudioDebug = "audio_debug"
    const val DesignSystem = "design_system"
}