package com.example.gainsbookjc

import android.util.Log

fun newExercise(
    exercises: MutableList<ExerciseWithIndex>,
    textFieldState: String
): MutableList<ExerciseWithIndex> {
    val TAG = "newExercise"
    Log.d(TAG, "start")
    var indexOfLast = 1

    // Creates a list of indexes based on the exercise index
    if (exercises.isNotEmpty()) {
        Log.d(TAG, "notEmpty")
        val indexList: MutableList<Int> = mutableListOf()
        exercises.forEach { exerciseWithIndex ->
            indexList.add(exerciseWithIndex.index)
        }
        indexOfLast = indexList.max() + 1
    }

    // Creates a new exercise with an index one higher than the previous
    exercises.add(
        ExerciseWithIndex(
            description = textFieldState,
            index = indexOfLast
        )
    )
    Log.d(TAG, "exercises: $exercises")
    return exercises
}

fun editExercise(
    exercises: MutableList<ExerciseWithIndex>,
    description: String,
    exerciseIndex: Int,
    textFieldState: String
): MutableList<ExerciseWithIndex> {
    // removes the selected exercise from the newly created list
    exercises.remove(
        ExerciseWithIndex(
            description = description,
            index = exerciseIndex
        )
    )

    // Creates a new exercise with an index one higher than the previous
    exercises.add(
        ExerciseWithIndex(
            description = textFieldState,
            index = exerciseIndex
        )
    )
    // Sorts the list ascending based on the index of the exercise
    exercises.sortBy { exerciseWithIndex -> exerciseWithIndex.index }

    return exercises
}

fun deleteExercise(
    exercises: MutableList<ExerciseWithIndex>,
    description: String,
    exerciseIndex: Int
): List<ExerciseWithIndex> {
    // removes the selected exercise from the newly created list
    exercises.remove(
        ExerciseWithIndex(
            description = description,
            index = exerciseIndex
        )
    )
    // Sorts the list ascending based on the index of the exercise
    exercises.sortBy { exerciseWithIndex -> exerciseWithIndex.index }
    return exercises
}

data class ExerciseWithIndex(var description: String, val index: Int)

data class WorkoutDate(val day: Int, val month: Int, val year: Int)