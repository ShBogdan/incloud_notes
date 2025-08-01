package com.incloudlogic.taskmanager.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.incloudlogic.taskmanager.R
import com.incloudlogic.taskmanager.data.local.TaskDao
import com.incloudlogic.taskmanager.domain.entity.Task
import com.incloudlogic.taskmanager.ui.adapter.CustomAdapter
import com.incloudlogic.taskmanager.ui.listener.OnTaskCompletedClickListener
import com.incloudlogic.taskmanager.utils.EdgeToEdgeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class TasksOverviewActivity : AppCompatActivity(), OnTaskCompletedClickListener {

    private var isStateAsc = true
    private var isTitleAsc = true
    private var isPriorityAsc = true

    private lateinit var addButton: FloatingActionButton
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var dao: TaskDao
    private var context: Context = this

    private val localTasks = mutableListOf<Task>()
    private val icPlatformTasks = mutableListOf<Task>()

    private lateinit var localAdapter: CustomAdapter
    private lateinit var icPlatformAdapter: CustomAdapter

    private var offlineMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tasks_overview)
        EdgeToEdgeUtils.applyEdgeToEdgePadding(R.id.main, this)

        offlineMode = intent.getBooleanExtra("offlineMode", false)

        initViews()
        setupAddButton()
        setupViewPager()
        setupSortButtons()
    }

    override fun onResume() {
        super.onResume()
        updateData()
    }

    private fun initViews() {
        addButton = findViewById(R.id.addButton)
        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tabLayout)
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

    private fun navigateToEditTaskScreen(task: Task) {
        val intent = Intent(this, AddTaskActivity::class.java)
        intent.putExtra("taskId", task.id.toString())
        intent.putExtra("taskTitle", task.title)
        intent.putExtra("taskContent", task.content)
        intent.putExtra("taskPriority", task.priority)
        intent.putExtra("isLocal", task.isLocal)
        intent.putExtra("isEditMode", true)
        startActivity(intent)
    }

    private fun setupViewPager() {
        dao = TaskDao(this)
        initDb()

        // Initialize adapters
        localAdapter = CustomAdapter(localTasks, this) { task ->
            navigateToEditTaskScreen(task)
        }
        icPlatformAdapter = CustomAdapter(icPlatformTasks, this) { task ->
            navigateToEditTaskScreen(task)
        }

        updateData()

        // Set up ViewPager2 with FragmentStateAdapter
        viewPager.adapter = TasksPagerFragmentAdapter(this)

        // TabLayoutMediator to bind tabs with ViewPager2
        val tabTitles =
            if (offlineMode) listOf("Local Tasks") else listOf("Local Tasks", "ICPlatform Tasks")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        // Set initial title
        supportActionBar?.title =
            if (offlineMode || viewPager.currentItem == 0) "Local Tasks" else "ICPlatform Tasks"

        // Listen for page changes to update title
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (offlineMode) {
                    supportActionBar?.title = "Local Tasks"
                } else {
                    supportActionBar?.title =
                        if (position == 0) "Local Tasks" else "ICPlatform Tasks"
                }
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

    // Minimal FragmentStateAdapter for ViewPager2
    inner class TasksPagerFragmentAdapter(activity: FragmentActivity) :
        FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = if (offlineMode) 1 else 2

        override fun createFragment(position: Int): Fragment {
            return TasksPageFragment.newInstance(position)
        }
    }

    // Fragment displaying a RecyclerView for each page
    class TasksPageFragment : Fragment() {
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.page_recycler_view, container, false)
            val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

            val activity = requireActivity() as TasksOverviewActivity
            val position = arguments?.getInt(ARG_POSITION) ?: 0
            val adapter =
                if (activity.offlineMode || position == 0) activity.localAdapter else activity.icPlatformAdapter

            recyclerView.layoutManager = LinearLayoutManager(activity)
            recyclerView.adapter = adapter

            val itemTouchHelper = ItemTouchHelper(activity.createItemTouchCallback(adapter))
            itemTouchHelper.attachToRecyclerView(recyclerView)

            return view
        }

        companion object {
            private const val ARG_POSITION = "position"
            fun newInstance(position: Int): TasksPageFragment {
                val fragment = TasksPageFragment()
                val args = Bundle()
                args.putInt(ARG_POSITION, position)
                fragment.arguments = args
                return fragment
            }
        }
    }

    private fun createItemTouchCallback(adapter: CustomAdapter) =
        object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT
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

                val dialogView = LayoutInflater.from(context).inflate(R.layout.custom_alert_dialog, null)
                val dialog = Dialog(context)

                dialog.apply {
                    setContentView(dialogView)
                    setCancelable(true)
                    window?.setBackgroundDrawableResource(android.R.color.transparent)
                }

                // Настройка размера
                dialog.setOnShowListener {
                    dialog.window?.setLayout(
                        (resources.displayMetrics.widthPixels * 0.8).toInt(),
                        WindowManager.LayoutParams.WRAP_CONTENT
                    )
                }

                // Обработчики кнопок
                dialogView.findViewById<Button>(R.id.btn_positive).setOnClickListener {
                    lifecycleScope.launch(Dispatchers.IO) {
                        dao.delete(taskId)
                    }
                    adapter.removeAt(position)
                    dialog.dismiss()
                }

                dialogView.findViewById<Button>(R.id.btn_negative).setOnClickListener {
                    adapter.notifyItemChanged(position)
                    dialog.dismiss()
                }

                dialog.show()
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                // Save the new sort order to database when drag is complete
                adapter.saveSortOrder()
                dao.updateSortOrders(adapter.getTasks())
            }
        }

    // Remove menu-based sorting, replaced by buttons
    private fun setupSortButtons() {
        val buttonComplete = findViewById<View>(R.id.button_sort_complete)
        val buttonTitle = findViewById<View>(R.id.button_sort_title)
        val buttonPriority = findViewById<View>(R.id.button_sort_priority)

        buttonComplete.setOnClickListener {
            val data = dao.getAll()
            isStateAsc = !isStateAsc
            val sorted =
                if (isStateAsc) data.sortedBy { it.isCompleted } else data.sortedByDescending { it.isCompleted }
            updateSortedTasks(sorted)
        }

        buttonTitle.setOnClickListener {
            val data = dao.getAll()
            isTitleAsc = !isTitleAsc
            val sorted =
                if (isTitleAsc) data.sortedBy { it.title } else data.sortedByDescending { it.title }
            updateSortedTasks(sorted)
        }

        buttonPriority.setOnClickListener {
            val data = dao.getAll()
            isPriorityAsc = !isPriorityAsc
            val sorted =
                if (isPriorityAsc) data.sortedBy { it.priority } else data.sortedByDescending { it.priority }
            updateSortedTasks(sorted)
        }
    }

    private fun updateSortedTasks(sorted: List<Task>) {
        // Update sort order based on the new sorted list
        val sortedWithOrder = sorted.mapIndexed { index, task ->
            task.copy(sortOrder = index)
        }

        // Save the new sort order to database
        dao.updateSortOrders(sortedWithOrder)

        // Update adapters with sorted data
        val localSorted = sortedWithOrder.filter { it.isLocal }
        val icPlatformSorted = sortedWithOrder.filter { !it.isLocal }

        localAdapter.updateData(localSorted)
        icPlatformAdapter.updateData(icPlatformSorted)
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
                false,
                0
            ),
            Task(
                UUID.randomUUID(),
                "Vacuum the living room",
                "Vacuum carpets, under the sofa, and corners of the room.",
                2,
                false,
                false,
                1
            ),
            Task(
                UUID.randomUUID(),
                "Take out the trash",
                "Empty all bins and take trash bags outside.",
                0,
                false,
                false,
                2
            ),
            Task(
                UUID.randomUUID(),
                "Clean the bathroom",
                "Scrub sink, toilet, bathtub, and wipe down all surfaces.",
                2,
                true,
                true,
                3
            ),
            Task(
                UUID.randomUUID(),
                "Organize the closet",
                "Sort clothes, fold items, and remove unused garments.",
                2,
                true,
                false,
                4
            ),
        )

        if (dao.getAll().isEmpty()) {
            dataset.forEachIndexed { index, task ->
                dao.insert(
                    Task(
                        UUID.randomUUID(),
                        task.title,
                        task.content,
                        task.priority,
                        task.isCompleted,
                        task.isLocal,
                        index
                    )
                )
            }
        }
    }
}
