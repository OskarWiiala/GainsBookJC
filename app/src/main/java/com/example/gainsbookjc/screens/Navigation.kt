package com.example.gainsbookjc.screens

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.gainsbookjc.BottomNavItem
import com.example.gainsbookjc.WorkoutScreens

@Composable
fun NavigationGraph(
    navController: NavHostController,
    lifecycleScope: LifecycleCoroutineScope,
    context: Context
) {
    NavHost(navController, startDestination = BottomNavItem.LogScreen.screen_route) {
        composable(BottomNavItem.LogScreen.screen_route) {
            LogScreen(context, navController)
        }
        composable(BottomNavItem.StatsScreen.screen_route) {
            StatsScreen()
        }
        composable(BottomNavItem.TimerScreen.screen_route) {
            TimerScreen()
        }
        composable(BottomNavItem.ProfileScreen.screen_route) {
            ProfileScreen()
        }

        composable(
            route = WorkoutScreens.ViewWorkoutScreen.screen_route + "/{workoutID}", // to add multiple args, just add another /{variable}. optional arguments ?test={test}
            arguments = listOf(navArgument("workoutID") {
                type = NavType.IntType
                defaultValue = -1
            })
        ) { entry ->
            ViewWorkoutScreen(
                navController = navController,
                context = context,
                workoutID = entry.arguments!!.getInt("workoutID")
            )
        }

        composable(
            route = WorkoutScreens.EditWorkoutScreen.screen_route + "/{workoutID}", // to add multiple args, just add another /{variable}. optional arguments ?test={test}
            arguments = listOf(navArgument("workoutID") {
                type = NavType.IntType
                defaultValue = -1
            })
        ) { entry ->
            EditWorkoutScreen(
                context = context,
                navController = navController,
                workoutID = entry.arguments!!.getInt("workoutID")
            )
        }

        composable(
            route = WorkoutScreens.NewWorkoutScreen.screen_route,
        ) {
            NewWorkoutScreen(context = context, navController = navController)
        }
    }
}
