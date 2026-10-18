package com.capstone.tala.feature.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.capstone.tala.core.designsystem.components.StarrySky
import com.capstone.tala.core.designsystem.components.TalaCard
import com.capstone.tala.core.designsystem.components.TalaCharacterView
import com.capstone.tala.core.designsystem.components.TalaShapes
import com.capstone.tala.core.designsystem.components.TalaSecondaryButton
import com.capstone.tala.core.designsystem.components.TalaPrimaryButton
import com.capstone.tala.core.ui.theme.TALAColor

@Composable
fun StudentLoginScreen(
    onLogin: (String) -> Unit = {},
    onParentGate: () -> Unit = {},
    viewModel: StudentLoginViewModel = hiltViewModel()
) {
    val students by viewModel.students.collectAsState()
    var studentName by rememberSaveable { mutableStateOf("") }

    StarrySky(modifier = Modifier.fillMaxSize()) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val tabletWidth = maxWidth >= 840.dp
            if (tabletWidth) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(0.9f), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            GrownUpButton(onParentGate)
                        }
                        LoginGreeting(landscapeTablet = true)
                    }
                    if (students.isNotEmpty()) {
                        SavedStudents(students.map { it.name }, onLogin, Modifier.weight(1f))
                    }
                    NewPlayerCard(
                        studentName = studentName,
                        onStudentNameChange = { studentName = it },
                        onLogin = onLogin,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        GrownUpButton(onParentGate)
                    }
                    LoginGreeting()
                    if (students.isNotEmpty()) {
                        Text("Choose your avatar", style = MaterialTheme.typography.titleLarge, color = TALAColor.BackgroundCream)
                        students.forEachIndexed { index, student ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(tween(260, delayMillis = index * 45)) +
                                    slideInVertically(tween(260, delayMillis = index * 45)) { it / 8 }
                            ) {
                                SavedStudentCard(student.name) { onLogin(student.name) }
                            }
                        }
                    }
                    NewPlayerCard(
                        studentName = studentName,
                        onStudentNameChange = { studentName = it },
                        onLogin = onLogin
                    )
                }
            }
        }
    }
}

@Composable
private fun GrownUpButton(onClick: () -> Unit) {
    TalaSecondaryButton(
        text = "Grown-ups",
        onClick = onClick,
        icon = Icons.Outlined.Lock,
        contentDescription = "Open parent gate"
    )
}

@Composable
private fun LoginGreeting(landscapeTablet: Boolean = false) {
    if (landscapeTablet) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            TalaCharacterView("Tala", "wave", Modifier.size(140.dp))
            GreetingText(centered = true)
        }
    } else {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TalaCharacterView("Tala", "wave", Modifier.size(120.dp))
            GreetingText(centered = true)
        }
    }
}

@Composable
private fun GreetingText(centered: Boolean = false) {
    Column(
        horizontalAlignment = if (centered) Alignment.CenterHorizontally else Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Welcome back!", style = MaterialTheme.typography.headlineLarge, color = TALAColor.StarYellow)
        Text("Choose your name to begin.", style = MaterialTheme.typography.bodyLarge, color = TALAColor.BackgroundCream)
    }
}

@Composable
private fun SavedStudents(names: List<String>, onLogin: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Choose your avatar", style = MaterialTheme.typography.titleLarge, color = TALAColor.BackgroundCream)
        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier.heightIn(max = 640.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(names.size) { index ->
                val name = names[index]
                SavedStudentCard(name) { onLogin(name) }
            }
        }
    }
}

@Composable
private fun SavedStudentCard(name: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().heightIn(min = 104.dp),
        shape = TalaShapes.card,
        color = TALAColor.SurfaceElevated,
        contentColor = TALAColor.OnSurface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(72.dp).border(2.dp, TALAColor.SkyBlue, CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.Person, contentDescription = "$name avatar", tint = TALAColor.NightBlue, modifier = Modifier.size(40.dp))
            }
            Text(name, style = MaterialTheme.typography.titleLarge, color = TALAColor.OnSurface, modifier = Modifier.weight(1f))
            Icon(Icons.Rounded.ArrowForward, contentDescription = "Continue as $name", tint = TALAColor.NightBlue)
        }
    }
}

@Composable
private fun NewPlayerCard(
    studentName: String,
    onStudentNameChange: (String) -> Unit,
    onLogin: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TalaCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("New player", style = MaterialTheme.typography.headlineMedium, color = TALAColor.OnSurface)
            OutlinedTextField(
                value = studentName,
                onValueChange = onStudentNameChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = TalaShapes.input,
                label = { Text("Your name") },
                placeholder = { Text("Type your name") }
            )
            TalaPrimaryButton(
                text = "Start learning",
                onClick = { if (studentName.isNotBlank()) onLogin(studentName.trim()) },
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Rounded.ArrowForward,
                contentDescription = "Start learning"
            )
        }
    }
}
