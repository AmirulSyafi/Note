package com.amirulsyafi.note.ui.assignment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amirulsyafi.note.data.assignment.Assignment
import com.amirulsyafi.note.databinding.ItemAssignmentBinding

class AssignmentAdapter : ListAdapter<Assignment, AssignmentAdapter.NoteViewHolder>(DIFF_CALLBACK) {

    // Define the click listener interface
    fun interface OnItemClickListener {
        fun onItemClick(assignment: Assignment)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Assignment>() {
            override fun areItemsTheSame(oldItem: Assignment, newItem: Assignment): Boolean {
                return oldItem.slotNo == newItem.slotNo
            }

            override fun areContentsTheSame(oldItem: Assignment, newItem: Assignment): Boolean {
                return oldItem == newItem
            }
        }
    }

    class NoteViewHolder(val itemBinding: ItemAssignmentBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding =
            ItemAssignmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val assignment = getItem(position)

        // Bind the data
        holder.itemBinding.apply {
            assignment.slotNo.toString().also { textSlotNo.text = it }
            textType.text = assignment.assignmentName
            textMo.text = assignment.mo
            textOp.text = assignment.op
            textSmv.text = assignment.smv
            textRatio.text = assignment.opRatio
            textPrevious.text = assignment.previousOp
            textGroup.text = assignment.isGroupOp.toString()
        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(assignment)
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }
}