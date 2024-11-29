package com.amirulsyafi.note.ui.assignment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amirulsyafi.note.R
import com.amirulsyafi.note.data.assignment.Assignment
import com.amirulsyafi.note.repository.AssignmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssignmentViewModel @Inject constructor(
    private val assignmentRepository: AssignmentRepository
) : ViewModel() {

    private val _loadingFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loadingFlow: StateFlow<Boolean> = _loadingFlow.asStateFlow()

    val assignmentsFlow: Flow<List<Assignment>> = assignmentRepository.getAssignmentsFlow()

    fun onItemClick(assignment: Assignment) {
    }

    fun onMenuItemSelected(itemId: Int) {
        when (itemId) {
            R.id.action_refresh -> {
                getAssignmentsApi()
            }
        }
    }

    private fun getAssignmentsApi() {
        viewModelScope.launch {
            try {
                _loadingFlow.value = true
                assignmentRepository.getAssignmentApi(100000)
            } catch (e: Exception) {
                Log.d(TAG, "getApi: ${e.message}")
            } finally {
                _loadingFlow.value = false
            }
        }
    }

    companion object {
        private const val TAG = "AssignmentViewModel"
    }

}