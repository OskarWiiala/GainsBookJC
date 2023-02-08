package com.example.gainsbookjc.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gainsbookjc.BottomNavItem

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController, startDestination = BottomNavItem.LogScreen.screen_route) {
        composable(BottomNavItem.LogScreen.screen_route) {
            LogScreen()
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
    }
}