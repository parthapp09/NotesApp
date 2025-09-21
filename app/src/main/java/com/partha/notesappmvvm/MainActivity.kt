package com.partha.notesappmvvm

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.partha.notesappmvvm.Adapter.NoteAdapter
import com.partha.notesappmvvm.RoomDB.Note
import com.partha.notesappmvvm.ViewModel.NoteViewModel
import java.util.Date

class MainActivity : AppCompatActivity(), NoteAdapter.OnNoteMenuClickListener {
    lateinit var noteViewModel: NoteViewModel
    lateinit var noteAdapter: NoteAdapter
    lateinit var createNote: FloatingActionButton
    private var allNotes = listOf<Note>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initSearchView()

        noteAdapter = NoteAdapter(this)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = noteAdapter
        createNote = findViewById(R.id.floatingActionButton)
        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        createNote.setOnClickListener {
            startActivity(Intent(this, EditNoteActivity::class.java))
        }
        noteViewModel.allNotes.observe(this, Observer { notes ->
            notes?.let {
                allNotes = it
                noteAdapter.submitList(allNotes)
            }

        })
    }

    override fun onEditClicked(note: Note) {
        startActivity(Intent(this, EditNoteActivity::class.java).putExtra("Note", note))
    }

    override fun onDeleteClicked(note: Note) {
        noteViewModel.delete(note)
    }

    override fun onPinClicked(note: Note) {
        pinNote(note)
    }

    private fun initSearchView() {
        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.isIconifiedByDefault = false
        searchView.queryHint = "Search Notes"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterNotes(newText)
                return true
            }

        })
    }

    private fun filterNotes(query: String?) {
        if (query.isNullOrEmpty()) {
            noteAdapter.submitList(allNotes)
        } else {
            val filterList = allNotes.filter { note ->
                note.title.contains(query, true) || note.content.contains(query, true)
            }
            noteAdapter.submitList(filterList)
        }
    }

    private fun pinNote(note: Note) {
        val updatedNote = note.copy(
            title = note.title,
            content = note.content,
            bgColor = note.bgColor,
            isPinned = !note.isPinned,
            noteDate = note.noteDate
        )
        noteViewModel.update(updatedNote)
    }
}