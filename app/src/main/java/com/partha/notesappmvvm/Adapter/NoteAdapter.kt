package com.partha.notesappmvvm.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.partha.notesappmvvm.R
import com.partha.notesappmvvm.RoomDB.Note
import java.text.SimpleDateFormat

class NoteAdapter(private val onMenuClickListener: OnNoteMenuClickListener) :
    ListAdapter<Note, NoteAdapter.NoteViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem == newItem
            }

        }
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val title: TextView = itemView.findViewById(R.id.text_view_title)
        private val content: TextView = itemView.findViewById(R.id.text_view_body)
        private val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        private val card: CardView = itemView.findViewById(R.id.card_view_item)
        private val pin: ImageView = itemView.findViewById(R.id.btn_pin)

        fun bind(note: Note) {
            val formatter = SimpleDateFormat("MMM dd, yyyy")
            title.text = note.title
            content.text = note.content
            tvDate.text = formatter.format(note.noteDate)
            card.setCardBackgroundColor(Color.parseColor(note.bgColor))
            if (note.isPinned) {
                pin.visibility = VISIBLE
            }
            itemView.setOnClickListener {
                onMenuClickListener.onEditClicked(note)
            }
            itemView.setOnLongClickListener {
                showPopUpMenu(itemView, note)
                true
            }
        }
    }

    interface OnNoteLongPressClickListener {
        fun onNoteLongClicked(note: Note)
    }

    interface OnNoteMenuClickListener {
        fun onEditClicked(note: Note)
        fun onDeleteClicked(note: Note)
        fun onPinClicked(note: Note)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        holder.bind(note)
    }

    fun showPopUpMenu(view: View, note: Note) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.menuInflater.inflate(R.menu.popupmenu, popupMenu.menu)
        if (note.isPinned) {
            popupMenu.menu.findItem(R.id.pin).title = "UnPin"
        }
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.delete -> {
                    onMenuClickListener.onDeleteClicked(note)
                    true
                }

                R.id.pin -> {
                    onMenuClickListener.onPinClicked(note)
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }
}