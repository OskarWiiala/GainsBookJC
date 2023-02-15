package com.example.gainsbookjc

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.gainsbookjc.database.AppDao
import com.example.gainsbookjc.database.AppDatabase
import com.example.gainsbookjc.database.entities.Exercise
import com.example.gainsbookjc.database.entities.Workout
import com.example.gainsbookjc.database.entities.Year
import com.example.gainsbookjc.database.relations.WorkoutWithExercises
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun insertToDatabase(lifecycleScope: LifecycleCoroutineScope, context: Context) {
    val workouts = listOf(
        Workout(workoutID = 0, year = 2023, month = 2, day = 10),
        Workout(workoutID = 0, year = 2023, month = 2, day = 12),
        Workout(workoutID = 0, year = 2023, month = 2, day = 15)
    )
    val exercises = listOf(
        Exercise(
            exerciseID = 0,
            workoutID = 1,
            description = "Bench press: 5x5 80 kg 3mr",
            year = 2023,
            month = 2,
            day = 10
        ),
        Exercise(
            exerciseID = 0,
            workoutID = 1,
            description = "Pull up: 10, 8, 6 3mr",
            year = 2023,
            month = 2,
            day = 10
        ),
        Exercise(
            exerciseID = 0,
            workoutID = 1,
            description = "Squat: 3x5 100 kg 3mr",
            year = 2023,
            month = 2,
            day = 10
        ),
        Exercise(
            exerciseID = 0,
            workoutID = 2,
            description = "Bench press: 5x5 80 kg 3mr",
            year = 2023,
            month = 2,
            day = 12
        ),
        Exercise(
            exerciseID = 0,
            workoutID = 2,
            description = "Pull up: 10, 8, 6 3mr",
            year = 2023,
            month = 2,
            day = 12
        ),
        Exercise(
            exerciseID = 0,
            workoutID = 2,
            description = "Squat: 3x5 100 kg 3mr",
            year = 2023,
            month = 2,
            day = 12
        ),
        Exercise(
            exerciseID = 0,
            workoutID = 3,
            description = "Bench press: 5x5 80 kg 3mr",
            year = 2023,
            month = 2,
            day = 15
        ),
        Exercise(
            exerciseID = 0,
            workoutID = 3,
            description = "Pull up: 10, 8, 6 3mr",
            year = 2023,
            month = 2,
            day = 15
        ),
        Exercise(
            exerciseID = 0,
            workoutID = 3,
            description = "Squat: 3x5 100 kg 3mr",
            year = 2023,
            month = 2,
            day = 15
        ),
    )

    val years = listOf<Year>(
        Year(2020),
        Year(2021),
        Year(2022),
        Year(2023)
    )
    val dao = AppDatabase.getInstance(context).appDao
    val TAG = "insert"
    lifecycleScope.launch(Dispatchers.IO) {
        Log.d(TAG, "lifecyclescope launched")
        workouts.forEach { dao.insertWorkout(it) }
        exercises.forEach { dao.insertExercise(it) }
        years.forEach { dao.insertYear(it) }
    }
}

suspend fun getYears(dao: AppDao): List<Year> {
    return dao.getYears()
}

suspend fun getWorkoutWithExercisesByYearMonth(dao: AppDao, year: Int, month: Int): List<WorkoutWithExercises> {
    return dao.getWorkoutWithExercisesByYearMonth(year = year, month = month)
}

suspend fun getWorkoutByID(dao: AppDao, id: Int): List<WorkoutWithExercises> {
    return dao.getWorkoutWithExercisesByID(workoutID = id)
}

suspend fun insertYear(dao: AppDao, year: Int) {
    dao.insertYear(Year(year))
}