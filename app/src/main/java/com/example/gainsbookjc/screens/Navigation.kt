package com.example.gainsbookjc.screens

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gainsbookjc.BottomNavItem

@Composable
fun NavigationGraph(navController: NavHostController, lifecycleScope: LifecycleCoroutineScope, context: Context) {
    NavHost(navController, startDestination = BottomNavItem.LogScreen.screen_route) {
        composable(BottomNavItem.LogScreen.screen_route) {
            LogScreen(lifecycleScope, context)
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