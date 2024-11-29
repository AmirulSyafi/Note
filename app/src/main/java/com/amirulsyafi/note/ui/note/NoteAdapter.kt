package com.amirulsyafi.note.ui.note

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amirulsyafi.note.R
import com.amirulsyafi.note.data.note.Note
import com.amirulsyafi.note.databinding.ItemNoteBinding

class NoteAdapter : ListAdapter<Note, NoteAdapter.NoteViewHolder>(DIFF_CALLBACK) {

    // Define the click listener interface
    fun interface OnItemClickListener {
        fun onItemClick(note: Note)
    }

    // Define the long click listener interface
    fun interface OnItemLongClickListener {
        fun onItemLongClick(id: Int, note: Note)
    }

    private var onItemClickListener: OnItemClickListener? = null
    private var onItemLongClickListener: OnItemLongClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        onItemLongClickListener = listener
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem.status == newItem.status
            }
        }
    }

    class NoteViewHolder(val itemBinding: ItemNoteBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)

        // Bind the data
        holder.itemBinding.apply {
            textTitle.text = note.title
            textDesc.text = note.description
            cardNote.isChecked = note.status
        }

        // Handle click listener
        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(note)
        }

        // Handle long click listener
        holder.itemView.setOnLongClickListener {
            // Show PopupMenu when long-clicked
            val popupMenu =
                PopupMenu(holder.itemView.context, holder.itemView) // Use itemView as the anchor
            popupMenu.menuInflater.inflate(R.menu.note_item_menu, popupMenu.menu)

            // Set menu item click listener
            popupMenu.setOnMenuItemClickListener { item ->
                onItemLongClickListener?.onItemLongClick(item.itemId, note)
                true
            }
            // Show the PopupMenu
            popupMenu.show()
            true // Return true to indicate the event is handled
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }
}