package com.example.gainsbookjc.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.gainsbookjc.*
import com.example.gainsbookjc.database.AppDatabase
import com.example.gainsbookjc.database.entities.Year
import com.example.gainsbookjc.database.relations.WorkoutWithExercises
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LogViewModel(context: Context) : ViewModel() {
    val dao = AppDatabase.getInstance(context).appDao
    val TAG = "LogViewModel"

    private val _workouts = MutableStateFlow(listOf<WorkoutWithExercises>())
    val workouts: StateFlow<List<WorkoutWithExercises>> get() = _workouts

    fun getWorkouts() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "Getting workouts in MVVM")
            val initialList = getWorkoutWithExercisesByYearMonthFromDatabase(dao = dao, year = currentYear, month = currentMonth)
            _workouts.emit(initialList)
        }
    }

    fun deleteWorkoutByID(workoutID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "Attemping to delete workout with ID: $workoutID")
            deleteWorkoutByIDFromDatabase(dao = dao, workoutID = workoutID)
            deleteExercisesByWorkoutIDFromDatabase(dao = dao, workoutID = workoutID)
            getWorkouts()
        }
    }

    private var currentYear = 0
    fun setCurrentYear(year: Int) {currentYear = year}
    private var currentMonth = 0
    fun setCurrentMonth(month: Int) {currentMonth = month}

    private val _years = MutableStateFlow(listOf<Year>())
    val years: StateFlow<List<Year>> get() = _years

    fun getYears() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "Getting years in MVVM")
            val initialList = getYearsFromDatabase(dao)
            _years.emit(initialList)
        }
    }

    fun insertYearMVVM(year: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "Inserting year: $year")
            insertYearToDatabase(dao = dao, year = year)
        }
    }
}

inline fun <VM : ViewModel> logViewModelFactory(crossinline f: () -> VM) =
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = f() as T
    }