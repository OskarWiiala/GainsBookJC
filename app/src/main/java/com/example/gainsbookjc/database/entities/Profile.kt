package com.example.gainsbookjc.database.entities

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Profile(
    @PrimaryKey(autoGenerate = false)
    val userID: Int,
    val username: String,
    val description: String
)
