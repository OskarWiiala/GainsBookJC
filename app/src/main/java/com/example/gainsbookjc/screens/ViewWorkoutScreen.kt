package com.example.gainsbookjc.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gainsbookjc.BottomNavItem
import com.example.gainsbookjc.viewmodels.ViewWorkoutViewModel
import com.example.gainsbookjc.viewmodels.viewExerciseViewModelFactory

@Composable
fun ViewWorkoutScreen(navController: NavController, context: Context, workoutID: Int) {
    val viewModel: ViewWorkoutViewModel = viewModel(factory = viewExerciseViewModelFactory {
        ViewWorkoutViewModel(context)
    })

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        viewModel.getWorkout(workoutID = workoutID)

        val workout by viewModel.workout.collectAsState()

        if (workout.isNotEmpty()) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp, start = 24.dp, end = 24.dp),
                text = "${workout.first().workout.day}.${workout.first().workout.month}.${workout.first().workout.year}",
                textAlign = TextAlign.Start,
                fontSize = 36.sp
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.75f)
            ) {
                itemsIndexed(workout.first().exercises) { _, item ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp, start = 24.dp, end = 24.dp),
                        text = item.description,
                        textAlign = TextAlign.Start,
                        fontSize = 24.sp
                    )
                }
            }
        }
        Button(
            modifier = Modifier
                .height(50.dp)
                .width(150.dp),
            onClick = { navController.navigate(BottomNavItem.LogScreen.screen_route) }) {
            Text(text = "BACK", fontSize = 24.sp)
        }
    }
}