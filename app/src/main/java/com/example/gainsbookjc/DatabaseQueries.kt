package com.example.gainsbookjc

import com.example.gainsbookjc.database.AppDao
import com.example.gainsbookjc.database.entities.Exercise
import com.example.gainsbookjc.database.entities.Lift
import com.example.gainsbookjc.database.entities.Workout
import com.example.gainsbookjc.database.entities.Year
import com.example.gainsbookjc.database.relations.WorkoutWithExercises

// Get queries
suspend fun getYearsFromDatabase(dao: AppDao): List<Year> {
    return dao.getYears()
}

suspend fun getWorkoutWithExercisesByYearMonthFromDatabase(dao: AppDao, year: Int, month: Int): List<WorkoutWithExercises> {
    return dao.getWorkoutWithExercisesByYearMonth(year = year, month = month)
}

suspend fun getWorkoutByIDFromDatabase(dao: AppDao, workoutID: Int): List<WorkoutWithExercises> {
    return dao.getWorkoutWithExercisesByID(workoutID = workoutID)
}

suspend fun getLiftsByLiftTypeYearMonthFromDatabase(dao: AppDao, lift: String, type: String, year: Int, month: Int): List<Lift> {
    return dao.getLiftsByLiftTypeYearMonth(lift = lift, type = type, year = year, month = month)
}

// Insertions
suspend fun insertWorkoutToDatabase(dao: AppDao, workout: Workout): Long {
    return dao.insertWorkout(workout)
}

suspend fun insertExerciseToDatabase(dao: AppDao, exercise: Exercise) {
    return dao.insertExercise(exercise)
}

suspend fun insertYearToDatabase(dao: AppDao, year: Int) {
    dao.insertYear(Year(year))
}

suspend fun insertLiftToDatabase(dao: AppDao, lift: Lift) {
    dao.insertLift(lift)
}

// Deletions
suspend fun deleteWorkoutByIDFromDatabase(dao: AppDao, workoutID: Int) {
    return dao.deleteWorkoutByID(workoutID)
}

suspend fun deleteExercisesByWorkoutIDFromDatabase(dao: AppDao, workoutID: Int) {
    return dao.deleteExercisesByWorkoutID(workoutID)
}


