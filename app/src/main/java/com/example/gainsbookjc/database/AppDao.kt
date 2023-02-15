package com.example.gainsbookjc.database

import androidx.room.*
import com.example.gainsbookjc.database.entities.Exercise
import com.example.gainsbookjc.database.entities.Lift
import com.example.gainsbookjc.database.entities.Workout
import com.example.gainsbookjc.database.entities.Year
import com.example.gainsbookjc.database.relations.WorkoutWithExercises

@Dao
interface AppDao {
    // suspend because they are executed on a background thread
    // in order to not block the main thread
    // onConflict is when you try to insert a workout which already exists
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: Workout)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: Exercise)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertYear(year: Year)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLift(lift: Lift)

    // @Transaction is required to prevent multithreading problems
    @Transaction
    @Query("SELECT * FROM workout WHERE workoutID = :workoutID")
    suspend fun getWorkoutWithExercisesByID(workoutID: Int): List<WorkoutWithExercises>

    @Transaction
    @Query("SELECT * FROM workout WHERE year = :year AND month = :month")
    suspend fun getWorkoutWithExercisesByYearMonth(year: Int, month: Int): List<WorkoutWithExercises>

    @Transaction
    @Query("SELECT * FROM year")
    suspend fun getYears(): List<Year>
}