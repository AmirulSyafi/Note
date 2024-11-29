package com.amirulsyafi.note.ui.notedetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.amirulsyafi.note.R
import com.amirulsyafi.note.data.note.Note
import com.amirulsyafi.note.data.note.Priority
import com.amirulsyafi.note.databinding.FragmentNoteDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteDetailFragment : Fragment(), MenuProvider {
    private val viewModel: NoteDetailViewModel by viewModels()
    private lateinit var binding: FragmentNoteDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menHost: MenuHost = requireActivity()
        menHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Retrieve the dropdown items from the string array
        val items = resources.getStringArray(R.array.dropdown_items)
        val adapter = ArrayAdapter<Any?>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            items
        )
        binding.actPriority.setAdapter(adapter)
        binding.actPriority.setText(items[0], false)

        viewModel.note?.let {
            binding.etNoteTitle.setText(it.title)
            binding.etNoteDesc.setText(it.description)
            binding.cbDone.visibility = View.VISIBLE
            binding.cbDone.isChecked = it.status
            binding.actPriority.setText(items[it.priority.ordinal], false)
        }
    }

    private fun saveNote() {
        val title = binding.etNoteTitle.text.toString().trim()
        val description = binding.etNoteDesc.text.toString().trim()
        val date = System.currentTimeMillis()
        val priority = binding.actPriority.text.toString()
        val priorityKey = when (priority) {
            "Medium" -> Priority.MEDIUM
            "High" -> Priority.HIGH
            "Urgent" -> Priority.URGENT
            else -> Priority.LOW
        }
        val done = binding.cbDone.isChecked

        var id = 0;
        if (viewModel.note != null) {
            id = viewModel.note!!.id
        }
        val note = Note(id, title, description, date, priorityKey, done)
        viewModel.saveNote(note)
        Toast.makeText(context, "Note saved successfully", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.not_detail_menu, menu)
        menu.findItem(R.id.action_delete_note).isVisible = viewModel.note != null
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_save -> {
                saveNote()
                true
            }

            R.id.action_delete_note -> {
                viewModel.delete(viewModel.note!!)
                Toast.makeText(context, "Note deleted successfully", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
                true
            }

            else -> false
        }
    }
}