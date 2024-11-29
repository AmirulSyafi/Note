package com.amirulsyafi.note.ui.note

import android.util.Log
import androidx.core.util.Pair
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amirulsyafi.note.R
import com.amirulsyafi.note.data.note.Note
import com.amirulsyafi.note.data.note.Priority
import com.amirulsyafi.note.data.setting.SettingKey
import com.amirulsyafi.note.repository.NoteRepository
import com.amirulsyafi.note.repository.SettingRepository
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val settingRepository: SettingRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _eventChannel = Channel<NoteEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    private val _queryFlow: MutableStateFlow<String> = MutableStateFlow("")
    val queryFlow: StateFlow<String> = _queryFlow.asStateFlow()

    private val _dateRangeFlow: MutableStateFlow<Pair<Long, Long>> = MutableStateFlow(
        Pair(
            MaterialDatePicker.thisMonthInUtcMilliseconds(),
            MaterialDatePicker.todayInUtcMilliseconds()
        )
    )
    val dateRangeFlow: StateFlow<Pair<Long, Long>> = _dateRangeFlow.asStateFlow()

    private val _priorityFlow: MutableStateFlow<Priority?> = MutableStateFlow(null)
    val priorityFlow: StateFlow<Priority?> = _priorityFlow.asStateFlow()

    private val _statusFlow: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val statusFlow: StateFlow<Boolean?> = _statusFlow.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResultsFlow: Flow<List<Note>> =
        combine(_queryFlow, _dateRangeFlow, _priorityFlow, _statusFlow) { a, b, c, d ->
            Query(a, b, c, d)
        }.flatMapLatest { q ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS", Locale.ENGLISH)
            dateFormat.timeZone = TimeZone.getDefault() // Ensure it uses the local timezone

            val calendar = Calendar.getInstance(TimeZone.getDefault())

            // Set start date in UTC and adjust for local timezone
            calendar.timeInMillis = q.dateRange.first
            calendar[Calendar.HOUR_OF_DAY] = 0
            calendar[Calendar.MINUTE] = 0
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0
            val startDate = calendar.time

            // Set end date in UTC and adjust for local timezone
            calendar.timeInMillis = q.dateRange.second
            calendar[Calendar.HOUR_OF_DAY] = 23
            calendar[Calendar.MINUTE] = 59
            calendar[Calendar.SECOND] = 59
            calendar[Calendar.MILLISECOND] = 999
            val endDate = calendar.time

            Log.d(
                Companion.TAG,
                ": startDate ${dateFormat.format(startDate)} - endDate ${dateFormat.format(endDate)}"
            )

            noteRepository.searchNotesFlow(
                "%${q.query}%", startDate.time, endDate.time, q.priority, q.status
            )
        }

    private val _loadingFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loadingFlow: StateFlow<Boolean> = _loadingFlow.asStateFlow()

    init {
        viewModelScope.launch {
            val priority = settingRepository.getPrioritySetting(SettingKey.Priority)
            _priorityFlow.value = priority
            val status = settingRepository.getBoolSetting(SettingKey.Status)
            _statusFlow.value = status
        }
    }

    fun onClick(id: Int) {
        viewModelScope.launch {
            when (id) {
                R.id.action_add -> _eventChannel.send(NoteEvent.AddNote)
                R.id.chip_date_range -> _eventChannel.send(NoteEvent.OpenDatePicker(dateRangeFlow.value))
                R.id.chip_priority -> _eventChannel.send(NoteEvent.ShowFilterMenu(true))
                R.id.chip_status -> _eventChannel.send(NoteEvent.ShowFilterMenu(false))
                R.id.chip_server_time -> getServerDatetime()
                R.id.chip_assignment -> _eventChannel.send(NoteEvent.NavigateToAssignment)
            }
        }
    }

    private fun getServerDatetime() {
        viewModelScope.launch {
            try {
                _loadingFlow.value = true
                val response = settingRepository.getServerDatetimeApi()
                Log.d(TAG, "getApi: $response")
                _eventChannel.send(NoteEvent.ShowToast(convertToReadableDatetime(response)))
            } catch (e: Exception) {
                _eventChannel.send(NoteEvent.ShowToast(e.message.toString()))
            } finally {
                _loadingFlow.value = false
            }
        }
    }

    fun convertToReadableDatetime(timestamp: String): String {
        val inputFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val date = inputFormat.parse(timestamp)
        return outputFormat.format(date)
    }

    fun onItemClick(note: Note) {
        viewModelScope.launch {
            note.status = !note.status
            noteRepository.updateNote(note)
        }
    }

    fun onItemLongClick(id: Int, note: Note) {
        viewModelScope.launch {
            when (id) {
                R.id.action_edit_note -> {
                    _eventChannel.send(NoteEvent.NavigateToDetails(note))
                }
            }
        }
    }

    fun onMenuItemSelected(priority: Boolean, itemId: Int) {
        viewModelScope.launch {
            // Set priority or status based on the 'priority' flag
            val value = getSelectedValue(priority, itemId)

            // Update the appropriate flow and repository
            if (priority) {
                _priorityFlow.value = value as Priority?
                settingRepository.updatePrioritySetting(SettingKey.Priority, value)
            } else {
                _statusFlow.value = value as Boolean?
                settingRepository.updateBoolSetting(SettingKey.Status, value)
            }
        }
    }

    private fun getSelectedValue(priority: Boolean, itemId: Int): Any? {
        return if (priority) {
            when (itemId) {
                R.id.all -> null
                R.id.low -> Priority.LOW
                R.id.medium -> Priority.MEDIUM
                R.id.high -> Priority.HIGH
                R.id.urgent -> Priority.URGENT
                else -> null
            }
        } else {
            when (itemId) {
                R.id.all -> null
                R.id.pending -> false
                R.id.done -> true
                else -> null
            }
        }
    }

    fun onDateSelect(dateRange: Pair<Long, Long>) {
        _dateRangeFlow.value = dateRange
    }

    fun onQueryTextChange(newText: String?) {
        _queryFlow.value = newText ?: ""
    }

    data class Query(
        val query: String,
        val dateRange: Pair<Long, Long>,
        val priority: Priority?,
        val status: Boolean?
    )

    sealed interface NoteEvent {
        data object NavigateBack : NoteEvent
        data class ShowToast(val message: String) : NoteEvent
        data object AddNote : NoteEvent
        data class NavigateToDetails(val note: Note) : NoteEvent
        data object NavigateToAssignment : NoteEvent
        data class OpenDatePicker(val dateRange: Pair<Long, Long>) : NoteEvent
        data class ShowFilterMenu(val priority: Boolean) : NoteEvent
    }

    companion object {
        private const val TAG = "NoteViewModel"
    }
}