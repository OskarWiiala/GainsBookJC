package com.example.gainsbookjc

/**
 * @author Oskar Wiiala
 * @param screen_route
 */
sealed class WorkoutScreens(val screen_route: String) {
    object NewWorkoutScreen: WorkoutScreens("new_workout_screen")
    object ViewWorkoutScreen: WorkoutScreens("view_workout_screen")
    object EditWorkoutScreen: WorkoutScreens("edit_workout_screen")

    // only works for mandatory arguments
    fun withArgs(vararg args: Int): String {
        return buildString {
            append(screen_route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}