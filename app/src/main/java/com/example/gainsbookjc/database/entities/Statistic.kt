package com.example.gainsbookjc.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Statistic(
    @PrimaryKey(autoGenerate = true)
    val statisticID: Int,
    val variableID: Int,
    val type: String,
    val value: Double,
    val year: Int,
    val month: Int,
    val day: Int,
)

val statistics = listOf(
    Statistic(statisticID = 0, variableID = 0, type = "none", value = 80.0, year = 2023, month = 2, day = 10),
    Statistic(statisticID = 0, variableID = 0, type = "none", value = 75.0, year = 2023, month = 3, day = 10),
    Statistic(statisticID = 0, variableID = 1, type = "10rm", value = 60.0, year = 2023, month = 2, day = 10),
    Statistic(statisticID = 0, variableID = 1, type = "10rm", value = 67.5, year = 2023, month = 3, day = 10),
    Statistic(statisticID = 0, variableID = 3, type = "1rm", value = 75.0, year = 2023, month = 2, day = 10),
    Statistic(statisticID = 0, variableID = 3, type = "1rm", value = 85.0, year = 2023, month = 3, day = 10),
)
