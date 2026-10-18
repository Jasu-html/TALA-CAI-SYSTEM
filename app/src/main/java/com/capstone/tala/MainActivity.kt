package com.capstone.tala

import android.os.Bundle
import android.content.pm.ApplicationInfo
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.capstone.tala.audio.LessonAudioViewModel
import com.capstone.tala.audio.LocalLessonAudio
import com.capstone.tala.core.ui.theme.TalaTheme
import com.capstone.tala.core.designsystem.components.LocalReducedMotion
import com.capstone.tala.navigation.Routes
import com.capstone.tala.navigation.TalaNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val lessonAudio: LessonAudioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val reducedMotion = runCatching {
            Settings.Global.getFloat(contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
        }.getOrDefault(false)
        setContent {
            CompositionLocalProvider(
                LocalLessonAudio provides lessonAudio,
                LocalReducedMotion provides reducedMotion
            ) {
                TalaApplicationTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val isDebugBuild = applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
                        val startDestination = when {
                            isDebugBuild && intent.getBooleanExtra(EXTRA_DESIGN_SYSTEM, false) -> Routes.DesignSystem
                            isDebugBuild && intent.getBooleanExtra(EXTRA_AUDIO_DEBUG, false) -> Routes.AudioDebug
                            else -> Routes.Splash
                        }
                        TalaNavigation(startDestination = startDestination)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lessonAudio.resumeFromBackground()
    }

    override fun onPause() {
        if (!isChangingConfigurations) lessonAudio.pauseForBackground()
        super.onPause()
    }

    companion object {
        const val EXTRA_AUDIO_DEBUG = "com.capstone.tala.extra.AUDIO_DEBUG"
        const val EXTRA_DESIGN_SYSTEM = "com.capstone.tala.extra.DESIGN_SYSTEM"
    }
}

@Composable
fun TalaApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    TalaTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
