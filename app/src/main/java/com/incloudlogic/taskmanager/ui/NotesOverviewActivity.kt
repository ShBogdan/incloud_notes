package com.incloudlogic.taskmanager.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.incloudlogic.taskmanager.R
import com.incloudlogic.taskmanager.data.NoteDao
import com.incloudlogic.taskmanager.model.Note
import com.incloudlogic.taskmanager.utils.CustomAdapter
import com.incloudlogic.taskmanager.utils.EdgeToEdgeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class NotesOverviewActivity : AppCompatActivity() {

    private lateinit var addButton: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var dao: NoteDao

    val dataset = mutableListOf(
        Note(
            UUID.randomUUID(),
            "Wash the dishes",
            "Clean all plates, cups, and cutlery after dinner.",
            1
        ),
        Note(
            UUID.randomUUID(),
            "Vacuum the living room",
            "Vacuum carpets, under the sofa, and corners of the room. And so on and so on",
            3
        ),
        Note(
            UUID.randomUUID(),
            "Take out the trash",
            "Empty all bins and take trash bags outside.",
            0
        ),
        Note(
            UUID.randomUUID(),
            "Water the plants",
            "Water all indoor and balcony plants thoroughly.",
            1
        ),
        Note(
            UUID.randomUUID(),
            "Clean the bathroom",
            "Scrub sink, toilet, bathtub, and wipe down all surfaces.",
            3
        ),
        Note(
            UUID.randomUUID(),
            "Organize the closet",
            "Sort clothes, fold items, and remove unused garments.",
            3
        ),
        Note(
            UUID.randomUUID(),
            "Mop the kitchen floor",
            "Use mop and detergent to clean up any spills or stains.",
            0
        ),
        Note(
            UUID.randomUUID(),
            "Feed the cat",
            "Refill the cat's food and water bowls as needed.",
            0
        ),
        Note(
            UUID.randomUUID(),
            "Change bed sheets and test large text in title of notes",
            "Remove old sheets and replace with fresh, clean ones.",
            3
        )
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notes_overview)
        EdgeToEdgeUtils.applyEdgeToEdgePadding(R.id.main, this)

        initViews()
        setupAddButton()
        setupRecyclerView()
    }

    private fun initViews() {
        addButton = findViewById(R.id.addButton)
    }

    private fun setupAddButton() {
        addButton.setOnClickListener {
            navigateToAddNoteScreen()
        }
    }

    private fun navigateToAddNoteScreen() {

//        val intent = Intent(this, AddNoteActivity::class.java)
//        startActivity(intent)

        dataset


    }

    private fun setupRecyclerView() {

        dao = NoteDao(this)


        val customAdapter = CustomAdapter(dao.getAll().toMutableList(), this.baseContext)
//        val customAdapter = CustomAdapter(dataset2, this.baseContext)

        recyclerView = findViewById(R.id.recycler_view)

        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)


        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = customAdapter
    }

    val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(
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
            val adapter = recyclerView.adapter as CustomAdapter

            // удаляем из базы
            val noteId = (viewHolder as CustomAdapter.NoteViewHolder).noteId!!
            lifecycleScope.launch(Dispatchers.IO) { dao.delete(noteId) }

            // удаляем из списка
            adapter.removeAt(viewHolder.adapterPosition)
        }

    }

    override fun onDestroy() {
        dao.close()
        super.onDestroy()
    }

}
