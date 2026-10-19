package com.capstone.tala.core.designsystem.components

import android.content.ActivityNotFoundException
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Mic
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.material.icons.rounded.KeyboardBackspace
import androidx.compose.material3CircularProgressIndicator
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.capstone.tala.core.ui.theme.TALAColor
import kotlinx.coroutines.delay
import java.util.Locale
import java.util.UUID

/**
 * Voice-based name recording for young children (6-7 years old).
 *
 * Features:
 * - Large microphone button with animated recording indicator
 * - Text display of recognized name
 * - Retake button to retry recording
 * - Support for Filipino and English accents
 * - Loading and recording states with visual feedback
 */
@Composable
fun VoiceNameRecorder(
    onNameCaptured: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isRecording by remember { mutableStateOf(false) }
    val isListening by remember { mutableStateOf(false) }
    val recognizedText by remember { mutableStateOf<String?>(null) }
    var recordingMessage by remember { mutableStateOf<String?>("Saan ang iyong pangalan? \nSay your name") }

    // Animated recording pulse
    val infiniteTransition = rememberInfiniteTransition(label = "recording")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = infiniteRepeatable.BackInOut
        ),
        label = "pulse"
    )

    // Speech recognition setup
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer() }
    val speechIntent = remember {
        // Use Filipino-Specific locale by default
        val locale = Locale("fil", "PH")
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Anong pangalan mo? - What's your name?")
        }
    }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        when (speechRecognizer.resultCode) {
            androidx.appcompat.app.Activity.RESULT_OK -> {
                val matches = speechRecognizer.resultData?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (!matches.isNullOrEmpty()) {
                    // Clean and capitalize text
                    val name = matches[0].trim()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                        .replace(Regex("[\\p{Punct}\\p{Blank}\\s]+"), " ")
                        .trim()

                    if (name.length >= 2 && name.length <= 30) {
                        onNameCaptured(name)
                    } else {
                        recordingMessage = "Hirap marinig. Pakisubukan ulit! \n(Can't hear clearly. Try again!)"
                        speechRecognizer.destroy()
                    }
                } else {
                    recordingMessage = "Huwag pangitain. Pakisubukan ulit! \n(Please speak again. Try again!)"
                    speechRecognizer.destroy()
                }
            }
            android.content.Activity.RESULT_CANCELED -> {
                recordingMessage = "Nag-hindi. Pakisubukan ulit! \n(Ignore. Try again!)"
                speechRecognizer.destroy()
            }
            else -> {
                recordingMessage = "Maraming salamat. Pakisubukan ulit! \n(Thanks. Try again!)"
                speechRecognizer.destroy()
            }
        }
        isListening = false
        isRecording = false
    }

    LaunchedEffect(isListening) {
        if (isListening) {
            try {
                speechRecognizer.setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        Log.d("VoiceNameRecorder", "Ready for speech")
                    }

                    override fun onBeginningOfSpeech() {
                        Log.d("VoiceNameRecorder", "Beginning of speech")
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        // Could add visual volume indicator here
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        Log.d("VoiceNameRecorder", "End of speech")
                    }

                    override fun onError(error: Int) {
                        val errorMessages = when (error) {
                            SpeechRecognizer.ERROR_AUDIO ->
                                "Maling input. Pakisubukan ulit! \n(Bad input. Try again!)"
                            SpeechRecognizer.ERROR_CLIENT ->
                                "Hindi maganda ang koneksyon. Pakisubukan ulit! \n(Bad connection. Try again!)"
                            SpeechRecognizer.ERROR_NETWORK ->
                                "May problema sa network. Pakisubukan ulit! \n(Network problem. Try again!)"
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
                                "Matagal nang nakakanhon. Pakisubukan ulit! \n(Taking too long. Try again!)"
                            SpeechRecognizer.NO_MATCH ->
                                "Hindi ko maintindihan. Pakisubukan ulit! \n(Can't understand. Try again!)"
                            SpeechRecognizer.ERROR_SERVER ->
                                "Server error. Pakisubukan ulit! \n(Server error. Try again!)"
                            else ->
                                "May problema. Pakisubukan ulit! \n(Exception. Try again!)"
                        }
                        recordingMessage = errorMessages
                        isListening = false
                        isRecording = false
                    }

                    override fun onResults(results: Bundle?) {
                        Log.d("VoiceNameRecorder", "Got results: $results")
                    }

                    override fun onPartialResults(partialResults: Bundle?) {}

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })

                speechRecognizer.startListening(speechIntent)
            } catch (e: SecurityException) {
                recordingMessage = "Dalawang matagal. Pakisuot ito! \n(Give permission. Accept it!)"
                isRecording = false
            } catch (e: ActivityNotFoundException) {
                recordingMessage = "Kulang ng modyul. Pakisuot permissyon! \n(Missing module. Accept permission!)"
                isRecording = false
            }
        } else {
            speechRecognizer.stopListening()
        }
    }

    // Camera and microphone placeholder - modern Android gives permission automatically
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Text display area
        if (recognizedText != null) {
            RecognitionResultBox(
                text = recognizedText,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            // Recording instruction
            RecognitionInstruction(
                message = recordingMessage,
                modifier = Modifier.fillMaxWidth().height(120.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Mic button with pulse animation
        val buttonColor by animateColorAsState(
            targetValue = if (isRecording) TALAColor.StarYellow,
            label = "buttonColor"
        )
        val micRotation by animateFloatAsState(
            targetValue = if (isRecording) 360f,
            label = "micRotation"
        )

        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = buttonColor,
                    shape = CircleShape
                )
                .clickable {
                    if (isRecording) {
                        // Stop recording
                        isListening = false
                        isRecording = false
                    } else {
                        // Start recording
                        recordingMessage = "Ano ang iyong pangalan? - Say your name"
                        recognizedText = ""
                        isRecording = true
                        isListening = true
                    }
                }
                .then(
                    if (isRecording) {
                        Modifier.border(
                            width = 4.dp,
                            color = TALAColor.BackgroundCream,
                            shape = CircleShape
                        )
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isListening) {
                // Pulsing effect
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            color = TALAColor.StarYellow.copy(alpha = (0.2f + pulseAlpha * 0.4f)),
                            shape = CircleShape
                        )
                )
            }

            Icon(
                imageVector = Icons.Rounded.Mic,
                contentDescription = if (isRecording) "Stopping" else "Recording",
                tint = Color.White,
                modifier = Modifier
                    .rotate(micRotation)
                    .size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Retake button
        if (recognizedText != null) {
            androidx.compose.material3.TextButton(
                onClick = {
                    recognizedText = null
                    recordingMessage = "Ano ang iyong pangalan? - Say your name"
                },
                modifier = Modifier
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardBackspace,
                    contentDescription = "Retake",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Retake your name", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

/**
 * Text box showing the recognized name
 */
@Composable
private fun RecognitionResultBox(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Pause,
                contentDescription = "Success",
                tint = TALAColor.SkyBlue,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Ready to start learning!",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

/**
 * Instruction box shown before recording
 */
@Composable
private fun RecognitionInstruction(
    message: String?,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.85f)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Mic,
                contentDescription = "Microphone",
                tint = TALAColor.StarYellow,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = message ?: "Tap to speak your name",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}