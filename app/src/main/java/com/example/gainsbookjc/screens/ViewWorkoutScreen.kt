package com.example.gainsbookjc.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.gainsbookjc.BottomNavItem

@Composable
fun ViewWorkoutScreen(navController: NavController, workoutID: Int?) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome to view workout screen!")
        Button(onClick = { navController.navigate(BottomNavItem.LogScreen.screen_route) }) {
            Text(text = "back")
        }
    }
}