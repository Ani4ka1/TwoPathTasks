package com.example.twopathtask

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class TaskCardAdapter(
    private val context: Context,
    private val tasks: List<Task>,
    private val listener: (Task, Action) -> Unit
) : RecyclerView.Adapter<TaskCardAdapter.TaskViewHolder>() {

    enum class Action {
        DELETE, MARK_COMPLETE, EDIT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_task_card, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task, listener)
    }

    override fun getItemCount(): Int = tasks.size

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTaskTitle: TextView = itemView.findViewById(R.id.tvTaskTitle)
        private val tvTaskDescription: TextView = itemView.findViewById(R.id.tvTaskDescription)
        private val tvTaskDate: TextView = itemView.findViewById(R.id.tvTaskDate)
        private val btnCompleteTask: ImageButton = itemView.findViewById(R.id.btnCompleteTask)
        private val btnEditTask: ImageButton = itemView.findViewById(R.id.btnEditTask)
        private val btnDeleteTask: ImageButton = itemView.findViewById(R.id.btnDeleteTask)

        fun bind(task: Task, listener: (Task, Action) -> Unit) {
            val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(task.date)

            tvTaskTitle.text = task.title
            tvTaskDescription.text = task.description
            tvTaskDate.text = formattedDate

            btnCompleteTask.setOnClickListener { listener(task, Action.MARK_COMPLETE) }
            btnEditTask.setOnClickListener { listener(task, Action.EDIT) }
            btnDeleteTask.setOnClickListener { listener(task, Action.DELETE) }
        }
    }
}
