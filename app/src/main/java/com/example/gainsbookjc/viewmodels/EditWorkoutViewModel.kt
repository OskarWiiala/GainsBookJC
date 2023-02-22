package com.example.gainsbookjc.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gainsbookjc.database.AppDatabase
import com.example.gainsbookjc.database.entities.Exercise
import com.example.gainsbookjc.database.entities.Workout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * @author Oskar Wiiala
 * @param context
 * @param workoutID the identifier of an individual workout
 * ViewModel for EditWorkoutScreen. Handles the editing, adding and deletion of exercises
 * of a selected workout. Also handles changing the date of a workout
 */

class EditWorkoutViewModel(context: Context, workoutID: Int) : ViewModel() {
    val dao = AppDatabase.getInstance(context).appDao
    val TAG = "EditExerciseViewModel"

    private val _exercises = MutableStateFlow(listOf<ExerciseWithIndex>())
    val exercises: StateFlow<List<ExerciseWithIndex>> get() = _exercises

    private val _date = MutableStateFlow<WorkoutDate>(WorkoutDate(0,0,0))
    val date: StateFlow<WorkoutDate> get() = _date

    init {
        viewModelScope.launch(Dispatchers.IO) {
            // Get workout based on workoutID, which will later be loaded in the UI
            val response = dao.getWorkoutWithExercisesByID(workoutID = workoutID)
            // Converts response from WorkoutWithExercises to ExerciseWithIndex
            val convertedList: MutableList<ExerciseWithIndex> = mutableListOf()
            if (response.isNotEmpty()) {
                var index = 1
                response.first().exercises.forEach { exercise ->
                    convertedList.add(
                        ExerciseWithIndex(
                            description = exercise.description,
                            index = index
                        )
                    )
                    index++
                }
                _exercises.emit(convertedList)
                val day = response.first().workout.day
                val month = response.first().workout.month
                val year = response.first().workout.year
                val date = WorkoutDate(day = day, month = month, year = year)
                // Date is used to display the date of the workout
                _date.emit(date)
            }
        }
    }

    // Adds exercises to the viewModel
    fun addExercises(exercises: List<ExerciseWithIndex>) {
        Log.d(TAG, "addExercise")
        viewModelScope.launch {
            Log.d(TAG, "emitting exercises")
            _exercises.emit(exercises)
        }
    }


    // Deletes workout from database based on workoutID
    private suspend fun deleteWorkout(workoutID: Int) {
        dao.deleteWorkoutByID(workoutID = workoutID)
        deleteExercises(workoutID = workoutID)
    }

    // Deletes exercises from database based on workoutID
    private suspend fun deleteExercises(workoutID: Int) {
        dao.deleteExercisesByWorkoutID(workoutID = workoutID)
    }

    // Adds workout to database step by step
    // First, the deletion of the workout and its exercises from database
    // Then, inserting the new version of that workout and its exercises to database
    // The workoutID is saved and used to determine the newly created workout
    // Update could have also been used, but I decided to go with delete and then insert
    fun addWorkout(exercises: List<ExerciseWithIndex>, workoutID: Int, day: Int, month: Int, year: Int) {
        val TAG = "addWorkout"
        viewModelScope.launch(Dispatchers.IO) {
            deleteWorkout(workoutID = workoutID)
            val workout = Workout(workoutID = workoutID, day = day, month = month, year = year)
            val response = dao.insertWorkout(workout = workout)

            val exercisesConverted: MutableList<Exercise> = mutableListOf()
            exercises.forEach { exerciseWithIndex ->
                exercisesConverted.add(
                    Exercise(
                        exerciseID = 0,
                        workoutID = response.toInt(),
                        description = exerciseWithIndex.description,
                        day = day,
                        month = month,
                        year = year
                    )
                )
            }
            exercisesConverted.forEach { dao.insertExercise(exercise = it) }
        }
    }

    // Sets date to viewModel
    fun setDate(date: WorkoutDate) {
        viewModelScope.launch(Dispatchers.IO) {
            _date.emit(date)
        }
    }
}

data class WorkoutDate(val day: Int, val month: Int, val year: Int)

// ViewModel factory
inline fun <VM : ViewModel> editExerciseViewModelFactory(crossinline f: () -> VM) =
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = f() as T
    }