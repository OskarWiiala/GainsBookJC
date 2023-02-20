package com.example.gainsbookjc.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Lift(
    @PrimaryKey(autoGenerate = false)
    val liftID: Int,
    val lift: String,
    val type: String,
    val value: Double,
    val year: Int,
    val month: Int,
    val day: Int,
)

/*@Entity
data class Statistic(
    @PrimaryKey(autoGenerate = true)
    val statisticID: Int,
    val year: Int,
    val month: Int,
    val day: Int,
)

data class StatisticWithVariables(
    @Embedded val statistic: Statistic,
    @Relation(
        parentColumn = "entryID",
        entityColumn = "entryID"
    )
    val variables: List<Variable>
)

val statistics = listOf(
    Statistic(statisticID = 0, 2023, 2, 27),
    Statistic(statisticID = 0, 2023, 2, 28),
)*/

val lifts = listOf(
    Lift(liftID = 0, lift = "bodyweight", type = "none", value = 80.0, year = 2023, month = 2, day = 10),
    Lift(liftID = 0, lift = "bodyweight", type = "none", value = 75.0, year = 2023, month = 3, day = 10),
    Lift(liftID = 0, lift = "bench press", type = "10rm", value = 60.0, year = 2023, month = 2, day = 10),
    Lift(liftID = 0, lift = "bench press", type = "10rm", value = 67.5, year = 2023, month = 3, day = 10),
    Lift(liftID = 0, lift = "seal row", type = "1rm", value = 75.0, year = 2023, month = 2, day = 10),
    Lift(liftID = 0, lift = "seal", type = "1rm", value = 85.0, year = 2023, month = 3, day = 10),
)
