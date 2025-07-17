package com.incloudlogic.taskmanager.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.incloudlogic.taskmanager.R
import com.incloudlogic.taskmanager.data.TaskDao
import com.incloudlogic.taskmanager.model.Task
import com.incloudlogic.taskmanager.utils.CustomAdapter
import com.incloudlogic.taskmanager.utils.EdgeToEdgeUtils
import com.incloudlogic.taskmanager.utils.OnTaskStateClickListener
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TasksOverviewActivity : AppCompatActivity(), OnTaskStateClickListener {

    private var isStateAsc = true
    private var isTitleAsc = true
    private var isPriorityAsc = true

    private lateinit var addButton: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var dao: TaskDao
    private  var context: Context = this

    private lateinit var customAdapter: CustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_tasks_overview)
        EdgeToEdgeUtils.applyEdgeToEdgePadding(R.id.main, this)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        initViews()
        setupAddButton()
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        updateRecyclerViewData()
    }

    private fun initViews() {
        addButton = findViewById(R.id.addButton)
    }

    private fun setupAddButton() {
        addButton.setOnClickListener { 
            navigateToAddTaskScreen()
        }
    }

    private fun navigateToAddTaskScreen() {
        val intent = Intent(this, AddTaskActivity::class.java)
        startActivity(intent)
    }

    private fun setupRecyclerView() {
        dao = TaskDao(this)
        initDb()

        customAdapter = CustomAdapter(dao.getAll().toMutableList(), context, this)

        recyclerView = findViewById(R.id.recycler_view)

        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = customAdapter
    }

    private fun updateRecyclerViewData() {
        customAdapter.updateData(dao.getAll())
    }

    override fun onTaskStateClick(task: Task, position: Int) {
        dao.update(task)
    }

    private val itemTouchCallback =
            object :
                    ItemTouchHelper.SimpleCallback(
                            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                            ItemTouchHelper.RIGHT
                    ) {

                override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                ): Boolean {
                    val adapter = recyclerView.adapter as CustomAdapter
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
                            customAdapter.removeAt(position)
                        }
                        .setNegativeButton(getString(R.string.no)) { _, _ ->
                            customAdapter.notifyItemChanged(position)
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
            R.id.sort_state -> {
                isStateAsc = !isStateAsc
                if (isStateAsc) data.sortedBy { it.state } else data.sortedByDescending { it.state }
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

        customAdapter.updateData(sorted)
        return true
    }

    override fun onDestroy() {
        dao.close()
        super.onDestroy()
    }


    private fun initDb() {
        val dataset =
                mutableListOf(
                        Task(
                                UUID.randomUUID(),
                                "Wash the dishes",
                                "Clean all plates, cups, and cutlery after dinner.",
                                1,
                            false
                        ),
                        Task(
                                UUID.randomUUID(),
                                "Vacuum the living room",
                                "Vacuum carpets, under the sofa, and corners of the room. And so on and so on",
                                3,
                            false
                        ),
                        Task(
                                UUID.randomUUID(),
                                "Take out the trash",
                                "Empty all bins and take trash bags outside.",
                                0,
                            false
                        ),
                        Task(
                                UUID.randomUUID(),
                                "Water the plants",
                                "Water all indoor and balcony plants thoroughly.",
                                1,
                            true
                        ),
                        Task(
                                UUID.randomUUID(),
                                "Clean the bathroom",
                                "Scrub sink, toilet, bathtub, and wipe down all surfaces.",
                                3,
                            false
                        ),
                        Task(
                                UUID.randomUUID(),
                                "Organize the closet",
                                "Sort clothes, fold items, and remove unused garments.",
                                3,
                            false
                        ),
                        Task(
                                UUID.randomUUID(),
                                "Mop the kitchen floor",
                                "Use mop and detergent to clean up any spills or stains.",
                                0,
                            true
                        ),
                        Task(
                                UUID.randomUUID(),
                                "Feed the cat",
                                "Refill the cat's food and water bowls as needed.",
                                0,
                            false
                        ),
                        Task(
                                UUID.randomUUID(),
                                "Change bed sheets and test large text in title of tasks",
                                "Remove old sheets and replace with fresh, clean ones.",
                                3,
                            false
                        )
                )
        if (dao.getAll().isEmpty())
                dataset.forEach { it ->
                    dao.insert(Task(UUID.randomUUID(), it.title, it.content, it.priority, it.state))
                }
    }

}
