package com.capstone.tala.feature.splash

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    var animationsComplete by mutableStateOf(false)
        private set

    var randomOffset by mutableStateOf<List<Pair<Float, Float>>>(emptyList())
        private set
    var randomSize by mutableStateOf<List<Float>>(emptyList())
        private set
    var randomRotation by mutableStateOf<List<Float>>(emptyList())
        private set

    fun startAnimation() {
        viewModelScope.launch {
            val positions = (0 until 20).map {
                (0..1000).random().toFloat() to (0..1000).random().toFloat()
            }
            val sizes = (0 until 20).map { (0..100).random().toFloat() }
            val rotations = (0 until 20).map { (0..360).random().toFloat() }

            randomOffset = positions
            randomSize = sizes
            randomRotation = rotations

            delay(500)
            animationsComplete = true
        }
    }

    fun onReady() {
        animationsComplete = true
    }

    fun onShowAdultArea() {
        // Navigate to parent gate
    }
}
