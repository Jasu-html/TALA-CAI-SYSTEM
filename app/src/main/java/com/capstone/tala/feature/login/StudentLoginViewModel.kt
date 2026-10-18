package com.capstone.tala.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.tala.data.db.StudentEntity
import com.capstone.tala.data.repository.StudentProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StudentLoginViewModel @Inject constructor(
    repository: StudentProgressRepository
) : ViewModel() {
    val students: StateFlow<List<StudentEntity>> = repository.getStudents().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )
}