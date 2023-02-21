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

class NewWorkoutViewModel(context: Context) : ViewModel() {
    val dao = AppDatabase.getInstance(context).appDao
    val TAG = "NewExerciseViewModel"

    private val _exercises = MutableStateFlow(listOf<ExerciseWithIndex>())
    val exercises: StateFlow<List<ExerciseWithIndex>> get() = _exercises

    fun addExercises(exercises: List<ExerciseWithIndex>) {
        Log.d(TAG, "addExercise")
        viewModelScope.launch {
            Log.d(TAG, "emitting exercises")
            _exercises.emit(exercises)
        }
    }

    fun addWorkout(exercises: List<ExerciseWithIndex>, day: Int, month: Int, year: Int) {
        val TAG = "addWorkout"
        Log.d(TAG, "$day $month $year")
        viewModelScope.launch(Dispatchers.IO) {
            val workout = Workout(workoutID = 0, day = day, month = month, year = year)
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
}

data class ExerciseWithIndex(var description: String, val index: Int)

inline fun <VM : ViewModel> newExerciseViewModelFactory(crossinline f: () -> VM) =
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = f() as T
    }