package com.partha.notesappmvvm

import android.graphics.Color
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.ViewModelProvider
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.partha.notesappmvvm.RoomDB.Note
import com.partha.notesappmvvm.ViewModel.NoteViewModel
import java.util.Date
import kotlin.properties.Delegates

class EditNoteActivity : AppCompatActivity() {
    private lateinit var titleEditText: EditText
    private lateinit var bodyEditText: EditText
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var saveButton: ImageView
    private lateinit var backButton: ImageView
    private lateinit var pinButton: ImageView
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var chooseBgColor: ImageView

    private var bgColor = "#FFF1DE"

    private var note: Note? = null
    private var isPinned by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        note = intent.getParcelableExtra("Note")

        titleEditText = findViewById(R.id.edit_text_title)
        bodyEditText = findViewById(R.id.edit_text_body)
        saveButton = findViewById(R.id.btn_save)
        backButton = findViewById(R.id.btn_back)
        pinButton = findViewById(R.id.btn_pin)
        chooseBgColor = findViewById(R.id.choose_color_button)
        toolbar = findViewById(R.id.toolbar)
        constraintLayout = findViewById(R.id.main_bg)
        //Pin button view
        isPinned = note?.isPinned ?: false
        pinButton.alpha = if (isPinned) 1.0f else 0.5f


        note?.let {
            titleEditText.setText(it.title)
            bodyEditText.setText(it.content)
        }

        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        saveButton.setOnClickListener {
            saveNote()
        }
        backButton.setOnClickListener {
            saveNote()
            finish()
        }
        pinButton.setOnClickListener {
            if (isPinned) {
                isPinned = false
                pinButton.alpha = 0.5f
            } else {
                isPinned = true
                pinButton.alpha = 1.0f
            }

        }

        chooseBgColor.setOnClickListener {
            ColorPickerDialog.Builder(this)
                .setTitle("Choose Color")
                .setColorShape(ColorShape.SQAURE)
                .setDefaultColor("#F2DBBB")
                .setColorListener { color, colorhex ->
                    bgColor = lightColorMaker(colorhex)
                    titleEditText.setBackgroundColor(Color.parseColor(bgColor))
                    bodyEditText.setBackgroundColor(Color.parseColor(bgColor))
                    toolbar.setBackgroundColor(Color.parseColor(bgColor))
                    constraintLayout.setBackgroundColor(Color.parseColor(bgColor))
                }.show()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                saveNote()
                finish()
            }
        })
    }

    private fun lightColorMaker(hexColor: String): String {
        // val hexColor = "#336699" // The hex color value you want to make lighter
        val color = Color.parseColor(hexColor) // Convert hex color to an integer

        val lightness = 0.8f // The desired lightness factor (adjust as needed)
        val lighterColor =
            ColorUtils.blendARGB(color, Color.WHITE, lightness) // Make the color lighter

        val lighterHexColor = String.format("#%06X", 0xFFFFFF and lighterColor)
        return lighterHexColor
    }

    private fun saveNote() {
        val title = titleEditText.text.toString().trim()
        val body = bodyEditText.text.toString().trim()
        if (title.isBlank() || body.isEmpty()) {
            Toast.makeText(this, "Please fill the fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (note != null) {
            val updatedNote = note!!.copy(
                title = title,
                content = body,
                bgColor = bgColor,
                isPinned = isPinned,
                noteDate = Date()
            )
            noteViewModel.update(updatedNote)
            Toast.makeText(this, "Note Updated", Toast.LENGTH_SHORT).show()
        } else {
            val newNote = Note(
                title = title,
                content = body,
                bgColor = bgColor,
                isPinned = isPinned,
                noteDate = Date()
            )
            noteViewModel.insert(newNote)
            Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show()
        }
    }
}