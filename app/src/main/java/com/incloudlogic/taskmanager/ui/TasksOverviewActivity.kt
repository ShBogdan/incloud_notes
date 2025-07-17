package com.incloudlogic.taskmanager.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.incloudlogic.taskmanager.R
import com.incloudlogic.taskmanager.data.TaskDao
import com.incloudlogic.taskmanager.model.Task
import com.incloudlogic.taskmanager.utils.CustomAdapter
import com.incloudlogic.taskmanager.utils.EdgeToEdgeUtils
import com.incloudlogic.taskmanager.utils.OnTaskCompletedClickListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class TasksOverviewActivity : AppCompatActivity(), OnTaskCompletedClickListener {

    private var isStateAsc = true
    private var isTitleAsc = true
    private var isPriorityAsc = true

    private lateinit var addButton: FloatingActionButton
    private lateinit var viewPager: ViewPager2
    private lateinit var dao: TaskDao
    private var context: Context = this

    private val localTasks = mutableListOf<Task>()
    private val icPlatformTasks = mutableListOf<Task>()

    private lateinit var localAdapter: CustomAdapter
    private lateinit var icPlatformAdapter: CustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tasks_overview)
        EdgeToEdgeUtils.applyEdgeToEdgePadding(R.id.main, this)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        initViews()
        setupAddButton()
        setupViewPager()
    }

    override fun onResume() {
        super.onResume()
        updateData()
    }

    private fun initViews() {
        addButton = findViewById(R.id.addButton)
        viewPager = findViewById(R.id.view_pager)
    }

    private fun setupAddButton() {
        addButton.setOnClickListener {
            navigateToAddTaskScreen()
        }
    }

    private fun navigateToAddTaskScreen() {
        val intent = Intent(this, AddTaskActivity::class.java)
        // Pass the current ViewPager position to determine if task should be local or icPlatform
        val isLocal = viewPager.currentItem == 0 // 0 = local tasks, 1 = icPlatform tasks
        intent.putExtra("isLocal", isLocal)
        startActivity(intent)
    }

    private fun setupViewPager() {
        dao = TaskDao(this)
        initDb()

        // Initialize adapters
        localAdapter = CustomAdapter(localTasks, this)
        icPlatformAdapter = CustomAdapter(icPlatformTasks, this)

        updateData()

        // Set up ViewPager2
        viewPager.adapter = TaskPagerAdapter()

        // Set initial title
        supportActionBar?.title =
            if (viewPager.currentItem == 0) "Local Tasks" else "ICPlatform Tasks"

        // Listen for page changes to update title
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                supportActionBar?.title = if (position == 0) "Local Tasks" else "ICPlatform Tasks"
            }
        })
    }

    private fun updateData() {
        val allTasks = dao.getAll()

        localTasks.clear()
        icPlatformTasks.clear()

        localTasks.addAll(allTasks.filter { it.isLocal })
        icPlatformTasks.addAll(allTasks.filter { !it.isLocal })

        if (::localAdapter.isInitialized) {
            localAdapter.notifyDataSetChanged()
        }
        if (::icPlatformAdapter.isInitialized) {
            icPlatformAdapter.notifyDataSetChanged()
        }
    }

    override fun onTaskCompletedClick(task: Task, position: Int) {
        dao.update(task)
        updateData()
    }

    // ViewPager2 Adapter
    inner class TaskPagerAdapter : RecyclerView.Adapter<TaskPagerAdapter.PageViewHolder>() {

        inner class PageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.page_recycler_view, parent, false)
            return PageViewHolder(view)
        }

        override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
            val adapter = if (position == 0) localAdapter else icPlatformAdapter

            holder.recyclerView.layoutManager = LinearLayoutManager(context)
            holder.recyclerView.adapter = adapter

            // Set up ItemTouchHelper for swipe-to-delete
            val itemTouchHelper = ItemTouchHelper(createItemTouchCallback(adapter))
            itemTouchHelper.attachToRecyclerView(holder.recyclerView)
        }

        override fun getItemCount(): Int = 2 // Two pages: local and icPlatform
    }

    private fun createItemTouchCallback(adapter: CustomAdapter) =
        object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                adapter.moveItem(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val taskId = (viewHolder as CustomAdapter.TaskViewHolder).taskId!!

                val builder = MaterialAlertDialogBuilder(context)
                    .setTitle(getString(R.string.delete_task))
                    .setMessage(getString(R.string.delete_task_confirmation))
                    .setPositiveButton(getString(R.string.yes)) { _, _ ->
                        lifecycleScope.launch(Dispatchers.IO) {
                            dao.delete(taskId)
                        }
                        adapter.removeAt(position)
                    }
                    .setNegativeButton(getString(R.string.no)) { _, _ ->
                        adapter.notifyItemChanged(position)
                    }

                builder.show()
            }
        }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_tasks_overview, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val data = dao.getAll()
        val sorted = when (item.itemId) {
            R.id.sort_complete -> {
                isStateAsc = !isStateAsc
                if (isStateAsc) data.sortedBy { it.isCompleted } else data.sortedByDescending { it.isCompleted }
            }

            R.id.sort_title -> {
                isTitleAsc = !isTitleAsc
                if (isTitleAsc) data.sortedBy { it.title } else data.sortedByDescending { it.title }
            }

            R.id.sort_priority -> {
                isPriorityAsc = !isPriorityAsc
                if (isPriorityAsc) data.sortedBy { it.priority } else data.sortedByDescending { it.priority }
            }

            else -> return super.onOptionsItemSelected(item)
        }

        // Update data with sorted results
        localTasks.clear()
        icPlatformTasks.clear()

        localTasks.addAll(sorted.filter { it.isLocal })
        icPlatformTasks.addAll(sorted.filter { !it.isLocal })

        localAdapter.notifyDataSetChanged()
        icPlatformAdapter.notifyDataSetChanged()

        return true
    }

    override fun onDestroy() {
        dao.close()
        super.onDestroy()
    }

    private fun initDb() {
        val dataset = mutableListOf(
            Task(
                UUID.randomUUID(),
                "Wash the dishes",
                "Clean all plates, cups, and cutlery after dinner.",
                1,
                false,
                false
            ),
            Task(
                UUID.randomUUID(),
                "Vacuum the living room",
                "Vacuum carpets, under the sofa, and corners of the room.",
                2,
                false,
                false
            ),
            Task(
                UUID.randomUUID(),
                "Take out the trash",
                "Empty all bins and take trash bags outside.",
                0,
                false,
                false
            ),
            Task(
                UUID.randomUUID(),
                "Clean the bathroom",
                "Scrub sink, toilet, bathtub, and wipe down all surfaces.",
                2,
                true,
                true
            ),
            Task(
                UUID.randomUUID(),
                "Organize the closet",
                "Sort clothes, fold items, and remove unused garments.",
                2,
                true,
                false
            ),
        )

        if (dao.getAll().isEmpty()) {
            dataset.forEach {
                dao.insert(
                    Task(
                        UUID.randomUUID(),
                        it.title,
                        it.content,
                        it.priority,
                        it.isCompleted,
                        it.isLocal
                    )
                )
            }
        }
    }
}
