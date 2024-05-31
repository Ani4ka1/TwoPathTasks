package com.example.twopathtask

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import java.util.*
import kotlinx.coroutines.launch

class CreateTaskActivity : AppCompatActivity() {

    private lateinit var etTaskTitle: EditText
    private lateinit var etTaskDescription: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var cbDaily: CheckBox
    private lateinit var cbWeekly: CheckBox
    private lateinit var datePicker: DatePicker
    private lateinit var btnSaveTask: Button
    private var accountId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)

        etTaskTitle = findViewById(R.id.etTaskTitle)
        etTaskDescription = findViewById(R.id.etTaskDescription)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        cbDaily = findViewById(R.id.cbDaily)
        cbWeekly = findViewById(R.id.cbWeekly)
        datePicker = findViewById(R.id.datePicker)
        btnSaveTask = findViewById(R.id.btnSaveTask)

        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
        accountId = sharedPref.getInt("account_id", -1)

        btnSaveTask.setOnClickListener {
            saveTask()
        }
        val categories = listOf("Work", "Hobby", "Study", "Sport", "Health", "Home")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
    }

    private fun saveTask() {
        val title = etTaskTitle.text.toString()
        val description = etTaskDescription.text.toString()
        val category = spinnerCategory.selectedItem?.toString() ?: ""
        val isDaily = cbDaily.isChecked
        val isWeekly = cbWeekly.isChecked

        if (title.isEmpty() || description.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val calendar = Calendar.getInstance()
        calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
        val date = calendar.time

        val task = Task(
            title = title,
            description = description,
            category = category,
            date = date,
            isDaily = isDaily,
            isWeekly = isWeekly,
            accountId = accountId
        )

        val db = AppDatabase.getDatabase(this)
        val taskDao = db.taskDao()

        lifecycleScope.launch {
            taskDao.insert(task)
            Toast.makeText(this@CreateTaskActivity, "Task saved successfully", Toast.LENGTH_SHORT).show()
            finish() // Возвращаемся на главный экран после сохранения
        }
    }
}
