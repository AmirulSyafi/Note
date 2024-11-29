package com.amirulsyafi.note.ui.notedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amirulsyafi.note.data.note.Note
import com.amirulsyafi.note.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class NoteDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val noteRepository: NoteRepository
) : ViewModel() {

    val note = NoteDetailFragmentArgs.fromSavedStateHandle(savedStateHandle).note

    fun delete(note: Note) = viewModelScope.launch {
        noteRepository.deleteNote(note)
    }

    fun saveNote(note: Note) {
        viewModelScope.launch {
            if (note.id > 0) {
                noteRepository.updateNote(note)
            } else {
                noteRepository.insertNote(note)
            }
        }
    }
}