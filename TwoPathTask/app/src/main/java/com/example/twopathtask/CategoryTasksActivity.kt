package com.example.twopathtask

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.lifecycle.lifecycleScope

class CategoryTasksActivity : AppCompatActivity() {

    private lateinit var rvTasks: RecyclerView
    private lateinit var btnBack: ImageButton
    private lateinit var tvCategoryName: TextView
    private lateinit var category: String
    private var accountId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_tasks)

        rvTasks = findViewById(R.id.rvTasks)
        btnBack = findViewById(R.id.btnBack)
        tvCategoryName = findViewById(R.id.tvCategoryName)

        category = intent.getStringExtra("category") ?: ""
        accountId = intent.getIntExtra("accountId", -1)

        tvCategoryName.text = category

        btnBack.setOnClickListener {
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        if (accountId != -1) {
            loadTasks(category, accountId)
        } else {
            Toast.makeText(this, "Account ID not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadTasks(category: String, accountId: Int) {
        val db = AppDatabase.getDatabase(this)
        val taskDao = db.taskDao()

        lifecycleScope.launch {
            taskDao.getTasksByCategory(category, accountId).collect { tasks ->
                rvTasks.layoutManager = LinearLayoutManager(this@CategoryTasksActivity)
                rvTasks.adapter = TaskListAdapter(this@CategoryTasksActivity, tasks, { task, action ->
                    when (action) {
                        TaskListAdapter.Action.DELETE -> deleteTask(task)
                        TaskListAdapter.Action.MARK_COMPLETE -> markTaskComplete(task)
                        TaskListAdapter.Action.EDIT -> editTask(task)
                    }
                })
            }
        }
    }

    private fun deleteTask(task: Task) {
        val db = AppDatabase.getDatabase(this)
        val taskDao = db.taskDao()

        lifecycleScope.launch {
            taskDao.delete(task)
        }
    }

    private fun markTaskComplete(task: Task) {
        val db = AppDatabase.getDatabase(this)
        val taskDao = db.taskDao()
        archiveTask(task)

        lifecycleScope.launch {
            taskDao.update(task.copy(isCompleted = true))
        }
    }

    private fun editTask(task: Task) {
        val intent = Intent(this, CreateTaskActivity::class.java)
        intent.putExtra("task", task)
        startActivity(intent)
    }
    private fun archiveTask(task: Task) {
        val archiveDb = ArchivedTaskDatabase.getDatabase(this)
        val archivedTaskDao = archiveDb.archivedTaskDao()
        val archivedTask = ArchivedTask(
            id = task.id,
            title = task.title,
            description = task.description,
            category = task.category,
            date = task.date,
            isDaily = task.isDaily,
            isWeekly = task.isWeekly,
            accountId = task.accountId
        )

        lifecycleScope.launch {
            archivedTaskDao.insert(archivedTask)
            deleteTask(task) // Удаление из текущей базы данных после перемещения в архив
        }
    }
}
