package com.example.gainsbookjc.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.gainsbookjc.database.entities.Exercise
import com.example.gainsbookjc.database.entities.Workout

data class WorkoutWithExercises(
    @Embedded val workout: Workout,
    @Relation(
        parentColumn = "workoutID",
        entityColumn = "workoutID"
    )
    val exercises: List<Exercise>
)
