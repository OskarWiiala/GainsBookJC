package com.example.gainsbookjc.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Profile(
    @PrimaryKey(autoGenerate = true)
    val userID: Int,
    val username: String,
    val picture: String,
    val description: String
)
