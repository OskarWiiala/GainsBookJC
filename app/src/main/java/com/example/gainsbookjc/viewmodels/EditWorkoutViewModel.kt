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

class EditWorkoutViewModel(context: Context, workoutID: Int) : ViewModel() {
    val dao = AppDatabase.getInstance(context).appDao
    val TAG = "EditExerciseViewModel"

    private val _exercises = MutableStateFlow(listOf<ExerciseWithIndex>())
    val exercises: StateFlow<List<ExerciseWithIndex>> get() = _exercises

    private val _date = MutableStateFlow<WorkoutDate>(WorkoutDate(0,0,0))
    val date: StateFlow<WorkoutDate> get() = _date

    init {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "init")
            val response = dao.getWorkoutWithExercisesByID(workoutID = workoutID)
            Log.d(TAG, "response: $response")
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
                Log.d(TAG, "date: $date")
                _date.emit(date)
                Log.d(TAG, "date after emit: ${_date.value}")

            }
        }
    }

    fun addExercises(exercises: List<ExerciseWithIndex>) {
        Log.d(TAG, "addExercise")
        viewModelScope.launch {
            Log.d(TAG, "emitting exercises")
            _exercises.emit(exercises)
        }
    }


    private suspend fun deleteWorkout(workoutID: Int) {
        Log.d(TAG, "inside deleteWorkout")
        dao.deleteWorkoutByID(workoutID = workoutID)
        deleteExercises(workoutID = workoutID)
        Log.d(TAG, "end of deleteWorkout")
    }

    private suspend fun deleteExercises(workoutID: Int) {
        Log.d(TAG, "inside deleteExercise")
        dao.deleteExercisesByWorkoutID(workoutID = workoutID)
        Log.d(TAG, "after deleteExercise")
    }

    fun addWorkout(exercises: List<ExerciseWithIndex>, workoutID: Int, day: Int, month: Int, year: Int) {
        val TAG = "addWorkout"
        Log.d(TAG, "$day $month $year")
        viewModelScope.launch(Dispatchers.IO) {
            deleteWorkout(workoutID = workoutID)
            Log.d(TAG, "after deleteWorkout")
            val workout = Workout(workoutID = workoutID, day = day, month = month, year = year)
            val response = dao.insertWorkout(workout = workout)

            val exercisesModified: MutableList<Exercise> = mutableListOf()
            exercises.forEach { exerciseWithIndex ->
                exercisesModified.add(
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
            exercisesModified.forEach { dao.insertExercise(exercise = it) }
        }
    }

    fun setDate(date: WorkoutDate) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "setting date")
            _date.emit(date)
            Log.d(TAG, "date set: ${_date.value}")
        }
    }
}

data class WorkoutDate(val day: Int, val month: Int, val year: Int)

inline fun <VM : ViewModel> editExerciseViewModelFactory(crossinline f: () -> VM) =
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = f() as T
    }