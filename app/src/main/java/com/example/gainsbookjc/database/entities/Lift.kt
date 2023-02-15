package com.example.gainsbookjc.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Lift(
    @PrimaryKey(autoGenerate = false)
    val lift: String
)
