package com.amirulsyafi.note.ui.note

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.amirulsyafi.note.R
import com.amirulsyafi.note.data.note.Priority
import com.amirulsyafi.note.databinding.FragmentNoteBinding
import com.amirulsyafi.note.ui.note.NoteViewModel.NoteEvent.NavigateBack
import com.amirulsyafi.note.ui.note.NoteViewModel.NoteEvent.NavigateToDetails
import com.amirulsyafi.note.ui.note.NoteViewModel.NoteEvent.OpenDatePicker
import com.amirulsyafi.note.ui.note.NoteViewModel.NoteEvent.ShowToast
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class NoteFragment : Fragment(), MenuProvider, OnClickListener {
    private val viewModel: NoteViewModel by viewModels()
    private lateinit var binding: FragmentNoteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteBinding.inflate(inflater, container, false)
        binding.chipDateRange.setOnClickListener(this)
        binding.chipPriority.setOnClickListener(this)
        binding.chipStatus.setOnClickListener(this)
        binding.chipServerTime.setOnClickListener(this)
        binding.chipAssignment.setOnClickListener(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val noteAdapter = NoteAdapter()
        noteAdapter.setOnItemClickListener { note ->
            viewModel.onItemClick(note);
        }
        noteAdapter.setOnItemLongClickListener { id, note ->
            viewModel.onItemLongClick(id, note);
        }
        binding.recyclerNote.apply {
            adapter = noteAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.loadingFlow.collect { loading ->
                        binding.progressCircular.visibility =
                            if (loading) View.VISIBLE else View.GONE
                        binding.chipServerTime.isEnabled = !loading
                    }
                }
                launch {
                    viewModel.priorityFlow.collect { priority ->
                        binding.chipPriority.text = when (priority) {
                            Priority.LOW -> "Low"
                            Priority.MEDIUM -> "Medium"
                            Priority.HIGH -> "High"
                            Priority.URGENT -> "Urgent"
                            else -> "All"
                        }
                    }
                }
                launch {
                    viewModel.statusFlow.collect { status ->
                        binding.chipStatus.text = when (status) {
                            true -> "Done"
                            false -> "Pending"
                            else -> "All"
                        }
                    }
                }
                launch {
                    viewModel.searchResultsFlow.collect { notes ->
                        noteAdapter.submitList(notes)
                    }
                }
                launch {
                    viewModel.dateRangeFlow.collect { dateRange ->
                        val firstDate = Date(dateRange.first)
                        val secondDate = Date(dateRange.second)

                        val sameMonthFormat = SimpleDateFormat("d", Locale.getDefault())
                        val differentMonthFormat =
                            SimpleDateFormat("d MMM yyyy", Locale.getDefault())
                        val monthAndYearFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())

                        val dateRangeText =
                            if (sameMonthFormat.format(firstDate) == sameMonthFormat.format(
                                    secondDate
                                )
                            ) {
                                // If the dates are within the same day
                                differentMonthFormat.format(firstDate)
                            } else if (monthAndYearFormat.format(firstDate) == monthAndYearFormat.format(
                                    secondDate
                                )
                            ) {
                                // If the dates are within the same month and year
                                "${
                                    SimpleDateFormat(
                                        "d",
                                        Locale.getDefault()
                                    ).format(firstDate)
                                } - ${
                                    differentMonthFormat.format(secondDate)
                                }"
                            } else {
                                // If the dates are in different months or years
                                "${differentMonthFormat.format(firstDate)} - ${
                                    differentMonthFormat.format(
                                        secondDate
                                    )
                                }"
                            }

                        binding.chipDateRange.text = dateRangeText
                    }
                }
                launch {
                    viewModel.eventChannel.collect { event ->
                        when (event) {
                            is NavigateBack -> {
                                findNavController().popBackStack()
                            }

                            is ShowToast -> {
                                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                            }

                            is NavigateToDetails -> {
                                findNavController().navigate(
                                    R.id.action_noteFragment_to_noteDetailFragment,
                                    Bundle().apply {
                                        putParcelable("note", event.note)
                                    })
                            }

                            is OpenDatePicker -> {
                                val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                                    .setTitleText("Select dates").setSelection(
                                        event.dateRange
                                    ).build()

                                dateRangePicker.show(parentFragmentManager, "date_range_picker")
                                dateRangePicker.addOnPositiveButtonClickListener { s ->
                                    viewModel.onDateSelect(s)
                                }
                            }

                            is NoteViewModel.NoteEvent.ShowFilterMenu -> {
                                val v =
                                    if (event.priority) binding.chipPriority else binding.chipStatus
                                val menuId =
                                    if (event.priority) R.menu.priority_menu else R.menu.filter_menu
                                val popupMenu = PopupMenu(requireContext(), v)
                                popupMenu.menuInflater.inflate(menuId, popupMenu.menu)
                                popupMenu.setOnMenuItemClickListener { item ->
                                    viewModel.onMenuItemSelected(event.priority, item.itemId)
                                    true
                                }
                                popupMenu.show()
                            }

                            NoteViewModel.NoteEvent.NavigateToAssignment ->
                                findNavController().navigate(NoteFragmentDirections.actionNoteFragmentToAssignmentFragment())

                            NoteViewModel.NoteEvent.AddNote -> {
                                val bundle = Bundle()
                                bundle.putBoolean("isUpdate", false)
                                findNavController().navigate(
                                    R.id.action_noteFragment_to_noteDetailFragment,
                                    bundle
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.note_menu, menu)
        val menuSearch = menu.findItem(R.id.menu_search).actionView as SearchView

        // Use viewLifecycleOwner.lifecycleScope if in Fragment
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.queryFlow.collect { query ->
                menuSearch.setQuery(query, false)
                Log.d(TAG, "onCreateMenu: queryFlow $query")
            }
        }
        menuSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.onQueryTextChange(newText)
                return true
            }
        })

    }

    override fun onMenuItemSelected(i: MenuItem): Boolean {
        viewModel.onClick(i.itemId)
        return false
    }

    override fun onClick(v: View) {
        viewModel.onClick(v.id)
    }

    companion object {
        private const val TAG = "NoteFragment"
    }
}