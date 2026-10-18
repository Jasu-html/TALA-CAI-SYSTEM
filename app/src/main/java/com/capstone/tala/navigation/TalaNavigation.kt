package com.capstone.tala.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.capstone.tala.audio.LocalLessonAudio
import com.capstone.tala.core.designsystem.components.DesignSystemGallery
import com.capstone.tala.core.ui.theme.TALAColor
import com.capstone.tala.feature.audio.AudioDebugScreen
import com.capstone.tala.feature.home.HomeScreen
import com.capstone.tala.feature.level.LevelFlowScreen
import com.capstone.tala.feature.login.StudentLoginScreen
import com.capstone.tala.feature.parent.ParentGateScreen
import com.capstone.tala.feature.parent.ParentPinScreen
import com.capstone.tala.feature.parent.RoleChoiceScreen
import com.capstone.tala.feature.splash.SplashScreen
import com.capstone.tala.feature.story.StoryIntroScreen
import com.capstone.tala.feature.teacher.TeacherSetupScreen

@Composable
fun TalaNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.Splash
) {
    val audio = LocalLessonAudio.current
    val currentEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(currentEntry?.destination?.route) {
        audio.stop()
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            fadeIn(tween(260)) + slideInHorizontally(tween(260)) { it / 28 }
        },
        exitTransition = {
            fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { -it / 32 }
        },
        popEnterTransition = {
            fadeIn(tween(240)) + slideInHorizontally(tween(240)) { -it / 32 }
        },
        popExitTransition = {
            fadeOut(tween(180)) + slideOutHorizontally(tween(180)) { it / 28 }
        }
    ) {
        composable(Routes.AudioDebug) {
            AudioDebugScreen()
        }

        composable(Routes.DesignSystem) {
            androidx.compose.material3.Surface(
                modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                color = TALAColor.BackgroundCream
            ) {
                DesignSystemGallery()
            }
        }

        composable(Routes.Splash) {
            SplashScreen(
                onParentGate = { navController.navigate(Routes.ParentGate) },
                onNavigateToHome = {
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.Splash) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Login) {
            StudentLoginScreen(
                onLogin = { studentName ->
                    navController.navigate("${Routes.Home}/$studentName") {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                },
                onParentGate = {
                    navController.navigate(Routes.ParentGate) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.StoryIntro) {
            StoryIntroScreen(
                onContinue = {
                    // StoryIntro doesn't know studentId, go back to login or extract from backstack
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.StoryIntro) { inclusive = true }
                    }
                },
                onSkip = {
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.StoryIntro) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.ParentGate) {
            ParentGateScreen(
                onUnlock = {
                    navController.navigate(Routes.ParentPin) {
                        popUpTo(Routes.ParentGate) { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.ParentPin) {
            ParentPinScreen(
                onPinAccepted = {
                    navController.navigate(Routes.RoleChoice) {
                        popUpTo(Routes.ParentPin) { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.RoleChoice) {
            RoleChoiceScreen(
                onRoleSelected = { role ->
                    if (role.equals("Teacher", ignoreCase = true)) {
                        navController.navigate(Routes.TeacherSetup) {
                            popUpTo(Routes.RoleChoice) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Routes.Home) {
                            popUpTo(Routes.RoleChoice) { inclusive = true }
                        }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.TeacherSetup) {
            TeacherSetupScreen(
                onSetupComplete = {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.TeacherSetup) { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "${Routes.Home}/{studentId}",
            arguments = listOf(
                androidx.navigation.navArgument("studentId") {
                    type = androidx.navigation.NavType.StringType
                }
            )
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: ""
            HomeScreen(
                studentId = studentId,
                onNavigateToLevel = { moduleId, levelIndex ->
                    navController.navigate(Routes.levelFlow(studentId, moduleId, levelIndex))
                }
            )
        }

        composable(
            route = "level_flow/{studentId}/{moduleId}/{levelIndex}",
            arguments = listOf(
                androidx.navigation.navArgument("studentId") {
                    type = androidx.navigation.NavType.StringType
                },
                androidx.navigation.navArgument("moduleId") {
                    type = androidx.navigation.NavType.StringType
                },
                androidx.navigation.navArgument("levelIndex") {
                    type = androidx.navigation.NavType.IntType
                }
            )
        ) {
            LevelFlowScreen(
                onComplete = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
