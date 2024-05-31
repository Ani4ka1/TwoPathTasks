package com.example.twopathtask

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class ArchivedTaskAdapter(
    private val context: Context,
    private var tasks: List<ArchivedTask>,
    private val listener: (ArchivedTask, Action) -> Unit
) : RecyclerView.Adapter<ArchivedTaskAdapter.TaskViewHolder>() {

    enum class Action {
        DELETE, RESTORE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_archive, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task, listener)
    }

    override fun getItemCount(): Int = tasks.size

    fun submitList(taskList: List<ArchivedTask>) {
        tasks = taskList
        notifyDataSetChanged()
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTaskTitle: TextView = itemView.findViewById(R.id.tvTaskTitle)
        private val tvTaskDescription: TextView = itemView.findViewById(R.id.tvTaskDescription)
        private val tvTaskDate: TextView = itemView.findViewById(R.id.tvTaskDate)
        private val btnRestoreTask: ImageButton = itemView.findViewById(R.id.btnCompleteTask)
        private val btnDeleteTask: ImageButton = itemView.findViewById(R.id.btnDeleteTask)

        fun bind(task: ArchivedTask, listener: (ArchivedTask, Action) -> Unit) {
            val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(task.date)

            tvTaskTitle.text = task.title
            tvTaskDescription.text = task.description
            tvTaskDate.text = formattedDate

            btnRestoreTask.setImageResource(R.drawable.ellipse_12)
            btnRestoreTask.setOnClickListener { listener(task, Action.RESTORE) }
            btnDeleteTask.setOnClickListener { listener(task, Action.DELETE) }
        }
    }
}

