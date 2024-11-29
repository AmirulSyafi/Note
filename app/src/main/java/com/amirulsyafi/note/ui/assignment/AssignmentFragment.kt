package com.amirulsyafi.note.ui.assignment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.amirulsyafi.note.R
import com.amirulsyafi.note.databinding.FragmentAssignmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AssignmentFragment : Fragment() {

    private val viewModel: AssignmentViewModel by viewModels()
    private lateinit var binding: FragmentAssignmentBinding
    private var menu: Menu? = null // Store a reference to the menu

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssignmentBinding.inflate(inflater, container, false)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.assignment_menu, menu)
                this@AssignmentFragment.menu = menu // Store the menu reference
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                viewModel.onMenuItemSelected(menuItem.itemId)
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = AssignmentAdapter()
        binding.recyclerAssignment.adapter = adapter
        adapter.setOnItemClickListener {
            viewModel.onItemClick(it)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.assignmentsFlow.collect { assignments ->
                        adapter.submitList(assignments)
                    }
                }
                launch {
                    viewModel.loadingFlow.collect { loading ->
                        binding.progressCircular.visibility =
                            if (loading) View.VISIBLE else View.GONE
                        // Update menu item state
                        menu?.findItem(R.id.action_refresh)?.isEnabled = !loading
                    }
                }
            }
        }
    }

}