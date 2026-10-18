package com.capstone.tala.feature.teacher

import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.capstone.tala.core.designsystem.components.TalaPrimaryButton
import com.capstone.tala.core.designsystem.components.TalaSecondaryButton
import com.capstone.tala.core.designsystem.components.TalaCard
import com.capstone.tala.core.designsystem.components.TalaShapes
import com.capstone.tala.core.ui.theme.TALAColor
import com.capstone.tala.audio.LocalLessonAudio
import com.capstone.tala.audio.NarrationAvailability
import com.capstone.tala.data.settings.appSettingsRepository
import kotlinx.coroutines.flow.first

@Composable
fun TeacherSetupScreen(
    onSetupComplete: (TeacherSetup) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val audio = LocalLessonAudio.current
    val availability by audio.availability.collectAsState()
    val settings = remember(context) { context.appSettingsRepository }
    var showTtsRecovery by rememberSaveable { mutableStateOf(false) }
    val setupSaver: Saver<TeacherSetup, Any> = remember {
        listSaver(
            save = { listOf(it.schoolName, it.classSection, it.pin, it.recoveryCode) },
            restore = {
                TeacherSetup(
                    schoolName = it[0],
                    classSection = it[1],
                    pin = it[2],
                    recoveryCode = it[3]
                )
            }
        )
    }
    var setup by rememberSaveable(stateSaver = setupSaver) {
        mutableStateOf(TeacherSetupLogic.createDefaultSetup())
    }
    var confirmed by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(availability) {
        if (availability == NarrationAvailability.ENGLISH_FALLBACK ||
            availability == NarrationAvailability.UNAVAILABLE
        ) {
            if (!settings.ttsRecoveryNoticeShown.first()) {
                settings.setSubtitlesEnabled(true)
                settings.setTtsRecoveryNoticeShown(true)
                showTtsRecovery = true
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(TALAColor.BackgroundCream),
        contentAlignment = Alignment.Center
    ) {
        TalaCard(
            modifier = Modifier
                .fillMaxWidth(if (maxWidth > 840.dp) 0.72f else 0.96f)
                .heightIn(max = maxHeight - 32.dp)
                .padding(16.dp),
            tonal = true
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(28.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Teacher setup",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TALAColor.NightBlue
                )
                Text("Class details", style = MaterialTheme.typography.titleMedium, color = TALAColor.OnSurfaceVariant)

                OutlinedTextField(
                    value = setup.schoolName,
                    onValueChange = { setup = setup.copy(schoolName = it) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = TalaShapes.input,
                    label = { Text("School name") }
                )

                OutlinedTextField(
                    value = setup.classSection,
                    onValueChange = { setup = setup.copy(classSection = it) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = TalaShapes.input,
                    label = { Text("Class section") }
                )

                Text("Secure access", style = MaterialTheme.typography.titleMedium, color = TALAColor.OnSurfaceVariant)
                OutlinedTextField(
                    value = setup.pin,
                    onValueChange = { setup = setup.copy(pin = it.filter { ch -> ch.isDigit() }.take(6)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = TalaShapes.input,
                    label = { Text("4–6 digit PIN") }
                )

                Text(
                    text = "Recovery code: ${setup.recoveryCode}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TALAColor.NightBlue,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TalaSecondaryButton(
                        text = "New code",
                        onClick = { setup = setup.copy(recoveryCode = TeacherSetupLogic.createRecoveryCode()) },
                        modifier = Modifier.weight(1f)
                    )

                    TalaSecondaryButton(
                        text = "Add sample class",
                        onClick = {
                            setup = setup.copy(
                                schoolName = setup.schoolName.ifBlank { "Tala Academy" },
                                classSection = setup.classSection.ifBlank { "Grade 1" }
                            )
                            confirmed = false
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                TalaPrimaryButton(
                    text = if (confirmed) "Saved ✓" else "I saved it",
                    onClick = {
                        confirmed = true
                        onSetupComplete(setup)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                TalaSecondaryButton(
                    text = "Back",
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (showTtsRecovery) {
                AlertDialog(
                    onDismissRequest = { showTtsRecovery = false },
                    title = { Text("Speech voice needs attention") },
                    text = {
                        Text(
                            if (availability == NarrationAvailability.ENGLISH_FALLBACK) {
                                "The selected voice is unavailable. TALA will use the installed English voice. Subtitles are now on."
                            } else {
                                "Text-to-speech is unavailable. Subtitles are now on, so lesson text remains visible until a voice is installed."
                            }
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showTtsRecovery = false
                                openTtsInstallOrSettings(context)
                            }
                        ) { Text("TTS settings") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showTtsRecovery = false }) { Text("Close") }
                    }
                )
            }
        }
    }
}

private fun openTtsInstallOrSettings(context: android.content.Context) {
    val settingsIntent = Intent("com.android.settings.TTS_SETTINGS")
    val installIntent = Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)
    val target = when {
        settingsIntent.resolveActivity(context.packageManager) != null -> settingsIntent
        installIntent.resolveActivity(context.packageManager) != null -> installIntent
        else -> null
    } ?: return

    try {
        context.startActivity(target)
    } catch (_: ActivityNotFoundException) {
        if (target != installIntent && installIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(installIntent)
        }
    }
}
