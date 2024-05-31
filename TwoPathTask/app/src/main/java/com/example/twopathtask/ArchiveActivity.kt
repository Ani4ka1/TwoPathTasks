package com.example.twopathtask

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ArchiveActivity : AppCompatActivity() {

    private lateinit var rvArchivedTasks: RecyclerView
    private lateinit var btnBack: ImageButton
    private lateinit var archivedTaskAdapter: ArchivedTaskAdapter
    private lateinit var archivedTaskDao: ArchivedTaskDao
    private var accountId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archive)

        rvArchivedTasks = findViewById(R.id.rvArchivedTasks)
        btnBack = findViewById(R.id.btnBack)

        archivedTaskAdapter = ArchivedTaskAdapter(this, emptyList()) { task, action ->
            when (action) {
                ArchivedTaskAdapter.Action.DELETE -> deleteArchivedTask(task)
                ArchivedTaskAdapter.Action.RESTORE -> restoreTask(task)
            }
        }

        rvArchivedTasks.layoutManager = LinearLayoutManager(this)
        rvArchivedTasks.adapter = archivedTaskAdapter

        btnBack.setOnClickListener {
            finish()
        }

        accountId = intent.getIntExtra("accountId", -1)

        val db = ArchivedTaskDatabase.getDatabase(this)
        archivedTaskDao = db.archivedTaskDao()

        displayArchivedTasks()
    }

    private fun displayArchivedTasks() {
        lifecycleScope.launch {
            archivedTaskDao.getArchivedTasks(accountId).collect { tasks ->
                archivedTaskAdapter.submitList(tasks)
            }
        }
    }

    private fun deleteArchivedTask(task: ArchivedTask) {
        lifecycleScope.launch {
            archivedTaskDao.delete(task)
            displayArchivedTasks()
        }
    }

    private fun restoreTask(task: ArchivedTask) {
        val db = AppDatabase.getDatabase(this)
        val taskDao = db.taskDao()
        val restoredTask = Task(
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
            taskDao.insert(restoredTask)
            deleteArchivedTask(task) // Удаление из архива после восстановления
        }
    }
}
