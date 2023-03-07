package com.example.gainsbookjc.screens

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gainsbookjc.*
import com.example.gainsbookjc.viewmodels.*

/**
 * @Author Oskar Wiiala
 * @param context
 * @param navController
 * @param workoutID the identifier of the individual workout
 * UI for editing an individual workout. Handles UI of the editing, adding and deletion of
 * the workout's exercises. Also handles UI of the changing of the workout's date
 */

@Composable
fun EditWorkoutScreen(context: Context, navController: NavController, workoutID: Int) {
    val supportViewModel: SupportViewModel = viewModel(factory = supportViewModelFactory {
        SupportViewModel(context = context)
    })

    // Do this only once
    LaunchedEffect(Unit) {
        supportViewModel.getWorkoutByID(workoutID = workoutID)
    }

    // collects the state of the view model's date and exercises
    val dateVM by supportViewModel.date.collectAsState()
    val exercises by supportViewModel.exercises.collectAsState()

    // context used for DatePicker
    val mContext = LocalContext.current

    // Declaring DatePickerDialog and setting
    // initial values as current values (present year, month and day)
    fun initDatePickerDialog() {
        val mDatePickerDialog = DatePickerDialog(
            mContext,
            { _: DatePicker, mSelectedYear: Int, mSelectedMonth: Int, mSelectedDay: Int ->
                val date =
                    WorkoutDate(
                        day = mSelectedDay,
                        month = mSelectedMonth + 1,
                        year = mSelectedYear
                    )
                supportViewModel.setDate(date)
            }, dateVM.year, dateVM.month - 1, dateVM.day
        )
        mDatePickerDialog.show()
    }

    // UI for displaying and selecting date
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "Edit workout", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.padding(top = 16.dp)) {
            Text(
                modifier = Modifier.padding(end = 16.dp),
                text = "${dateVM.day}.${dateVM.month}.${dateVM.year}",
                fontSize = 20.sp,
                color = Color.Gray
            )
            IconButton(onClick = {
                initDatePickerDialog()
            }) {
                Icon(
                    painter = painterResource(id = com.example.gainsbookjc.R.drawable.edit_calendar_icon_24),
                    contentDescription = "edit date",
                    Modifier.size(36.dp)
                )
            }
        }

        // List of ExerciseCards
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
        ) {
            itemsIndexed(exercises) { index, exerciseWithIndex ->
                ExerciseCard(
                    supportViewModel = supportViewModel,
                    description = exerciseWithIndex.description,
                    exerciseIndex = exerciseWithIndex.index
                )
            }
        }

        // Button for adding a new exercise
        AddNewExerciseButton(supportViewModel)

        // UI for OK and CANCEL buttons
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxSize()
        ) {
            // OK button
            Button(onClick = {
                // Calls view model to add new workout to database
                supportViewModel.addWorkout(
                    exercises = exercises,
                    workoutID = workoutID,
                    day = dateVM.day,
                    month = dateVM.month,
                    year = dateVM.year,
                    type = "delete"
                )
                // navigates back to LogScreen
                navController.navigate(BottomNavItem.LogScreen.screen_route)
            }) {
                Text(text = "OK")
            }
            Button(onClick = { navController.navigate(BottomNavItem.LogScreen.screen_route) }) {
                Text(text = "CANCEL")
            }
        }
    }
}