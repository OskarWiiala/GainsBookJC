package com.example.gainsbookjc.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Year(
    @PrimaryKey(autoGenerate = false)
    val year: Int
)
