package com.example.twopathtask

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import android.view.View

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var rvCardTasks: RecyclerView
    private lateinit var btnViewAllTasks: View
    private lateinit var selectedCategory: String
    private lateinit var overlay: View
    private lateinit var cardTaskContainer: View
    private lateinit var imgCreateTask: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var imgMenu: ImageButton
    private lateinit var navigationView: NavigationView
    private var accountId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvCardTasks = findViewById(R.id.rvCardTasks)
        btnViewAllTasks = findViewById(R.id.btnViewAllTasks)
        overlay = findViewById(R.id.overlay)
        cardTaskContainer = findViewById(R.id.cardTaskContainer)
        imgCreateTask = findViewById(R.id.imgCreateTask)
        drawerLayout = findViewById(R.id.drawer_layout)
        imgMenu = findViewById(R.id.imgMenu)
        navigationView = findViewById(R.id.navigation_view)

        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
        accountId = sharedPref.getInt("account_id", -1)

        setupCategoryButtons()

        btnViewAllTasks.setOnClickListener {
            val intent = Intent(this, CategoryTasksActivity::class.java)
            intent.putExtra("category", selectedCategory)
            intent.putExtra("accountId", accountId)
            startActivity(intent)
        }

        imgCreateTask.setOnClickListener {
            val intent = Intent(this, CreateTaskActivity::class.java)
            startActivity(intent)
        }

        imgMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        overlay.setOnClickListener {
            hideCardTasks()
        }

        navigationView.setNavigationItemSelectedListener(this)

        // Определяем, какое меню показывать
        updateNavigationView()
    }

    private fun setupCategoryButtons() {
        val categories = listOf("Work", "Hobby", "Study", "Sport", "Health", "Home")
        val buttonIds = listOf(
            R.id.btnWork,
            R.id.btnHobby,
            R.id.btnStudy,
            R.id.btnSport,
            R.id.btnHealth,
            R.id.btnHome
        )

        for (i in categories.indices) {
            val category = categories[i]
            val buttonId = buttonIds[i]
            val button = findViewById<ImageButton>(buttonId)
            button.setOnClickListener {
                selectedCategory = category
                displayCategoryTasks(category)
            }
        }
    }

    private fun displayCategoryTasks(category: String) {
        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
        accountId = sharedPref.getInt("account_id", -1)

        if (accountId == -1) {
            Toast.makeText(this, "Please login to view tasks", Toast.LENGTH_SHORT).show()
            return
        }

        val db = AppDatabase.getDatabase(this)
        val taskDao = db.taskDao()

        lifecycleScope.launch {
            taskDao.getTwoNearestTasks(category, accountId).collect { tasks ->
                if (tasks.size < 2) {
                    val intent = Intent(this@MainActivity, CategoryTasksActivity::class.java)
                    intent.putExtra("category", category)
                    intent.putExtra("accountId", accountId)
                    startActivity(intent)
                } else {
                    rvCardTasks.layoutManager = LinearLayoutManager(this@MainActivity)
                    rvCardTasks.adapter = TaskCardAdapter(this@MainActivity, tasks, { task, action ->
                        when (action) {
                            TaskCardAdapter.Action.DELETE -> deleteTask(task)
                            TaskCardAdapter.Action.MARK_COMPLETE -> markTaskComplete(task)
                            TaskCardAdapter.Action.EDIT -> editTask(task)
                        }
                    })
                    btnViewAllTasks.visibility = View.VISIBLE
                    cardTaskContainer.visibility = View.VISIBLE
                    overlay.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun hideCardTasks() {
        cardTaskContainer.visibility = View.GONE
        btnViewAllTasks.visibility = View.GONE
        overlay.visibility = View.GONE
    }

    private fun deleteTask(task: Task) {
        val db = AppDatabase.getDatabase(this)
        val taskDao = db.taskDao()

        lifecycleScope.launch {
            taskDao.delete(task)
            displayCategoryTasks(selectedCategory)
        }
    }

    private fun markTaskComplete(task: Task) {
        val db = AppDatabase.getDatabase(this)
        val taskDao = db.taskDao()
        archiveTask(task)

        lifecycleScope.launch {
            taskDao.update(task.copy(isCompleted = true))
            displayCategoryTasks(selectedCategory)
        }
    }

    private fun editTask(task: Task) {
        val intent = Intent(this, CreateTaskActivity::class.java)
        intent.putExtra("task", task)
        startActivity(intent)
    }

    private fun updateNavigationView() {
        val isAuthenticated = isAuthenticated()

        if (isAuthenticated) {
            navigationView.menu.clear()
            navigationView.inflateMenu(R.menu.nav_menu_user)
        } else {
            navigationView.menu.clear()
            navigationView.inflateMenu(R.menu.nav_menu_guest)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_login -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_register -> {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_settings -> {
                val intent = Intent(this, AccountSettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_archive -> {
                val intent = Intent(this, ArchiveActivity::class.java)
                intent.putExtra("accountId", accountId)
                startActivity(intent)
            }
            R.id.nav_support -> {
                val supportIntent = Intent(Intent.ACTION_VIEW)
                supportIntent.data = Uri.parse("https://t.me/TwoPathTasksSUPPORT_bot")
                startActivity(supportIntent)
            }
            // Добавьте обработчики для других пунктов меню, если необходимо
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun isAuthenticated(): Boolean {
        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return sharedPref.contains("account_id")
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