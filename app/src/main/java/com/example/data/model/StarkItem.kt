package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stark_items")
data class StarkItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // "note", "reminder", "event"
    val title: String,
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val datetimeString: String = "",
    val isCompleted: Boolean = false
)
