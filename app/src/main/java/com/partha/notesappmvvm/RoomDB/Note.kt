package com.partha.notesappmvvm.RoomDB

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
@Entity(tableName = "Notes")
data class Note(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Int = 0,
    val title: String,
    val content: String,
    val bgColor: String,
    val isPinned: Boolean,
    val noteDate: Date
) : Parcelable
