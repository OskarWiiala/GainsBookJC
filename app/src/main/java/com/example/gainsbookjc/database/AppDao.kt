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

    // Insertions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: Workout): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: Exercise)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertYear(year: Year)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLift(lift: Lift)

    // GET Queries
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

    @Transaction
    @Query("SELECT * FROM lift WHERE lift = :lift AND type = :type AND year = :year AND month = :month")
    suspend fun getLiftsByLiftTypeYearMonth(lift: String, type: String, year: Int, month: Int): List<Lift>

    // Deletions
    @Transaction
    @Query("DELETE FROM workout WHERE workoutID = :workoutID")
    suspend fun deleteWorkoutByID(workoutID: Int)

    @Transaction
    @Query("DELETE FROM exercise WHERE workoutID = :workoutID")
    suspend fun deleteExercisesByWorkoutID(workoutID: Int)
}