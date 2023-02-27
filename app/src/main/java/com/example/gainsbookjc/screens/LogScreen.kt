package com.example.gainsbookjc.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gainsbookjc.*
import com.example.gainsbookjc.R
import com.example.gainsbookjc.database.relations.WorkoutWithExercises
import com.example.gainsbookjc.viewmodels.LogViewModel
import com.example.gainsbookjc.viewmodels.SupportViewModel
import com.example.gainsbookjc.viewmodels.logViewModelFactory
import com.example.gainsbookjc.viewmodels.supportViewModelFactory
import java.util.Calendar

/**
 * @author Oskar Wiiala
 * @param context
 * @param navController
 * This screen is the main page of the app.
 * It hosts a list of exercises based on the selected month and year
 * You can also create a new exercise by clicking the fab, which takes you to NewWorkoutScreen
 * You can also add a new year
 */
@Composable
fun LogScreen(
    context: Context,
    navController: NavController
) {
    val TAG = "LogScreen"
    val supportViewModel: SupportViewModel = viewModel(factory = supportViewModelFactory {
        SupportViewModel(context)
    })
    val logViewModel: LogViewModel = viewModel(factory = logViewModelFactory {
        LogViewModel(context)
    })

    // Initializes month and year to be current month and year
    supportViewModel.setCurrentYear(Calendar.getInstance().get(Calendar.YEAR))
    supportViewModel.setCurrentMonth(Calendar.getInstance().get(Calendar.MONTH) + 1)

    Column(modifier = Modifier.fillMaxSize()) {
        // Top bar elements
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.primary),
            horizontalArrangement = Arrangement.End
        ) {
            // + new year button
            AddNewYearButton(supportViewModel = supportViewModel)
            Spacer(modifier = Modifier.width(10.dp))
            SelectMonthDropdown(supportViewModel = supportViewModel, logViewModel = logViewModel, statsViewModel = null, screen = "LogScreen")
            Spacer(modifier = Modifier.width(10.dp))
            SelectYearDropdown(supportViewModel = supportViewModel, logViewModel = logViewModel, statsViewModel = null, screen = "LogScreen")
            Spacer(modifier = Modifier.width(10.dp))
        }

        // List related elements
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            WorkoutList(logViewModel = logViewModel, supportViewModel = supportViewModel, navController = navController)
            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .padding(bottom = 16.dp, start = 16.dp),
                onClick = {
                    navController.navigate(WorkoutScreens.NewWorkoutScreen.screen_route)
                },
                contentColor = Color.White,
            ) {
                Text(text = "+", fontSize = 30.sp)
            }
        }
    }
}

/**
 * @author Oskar Wiiala
 * @param logViewModel
 * @param navController
 * This composable handles displaying a list of WorkoutCards
 */
@Composable
fun WorkoutList(logViewModel: LogViewModel, supportViewModel: SupportViewModel, navController: NavController) {
    val currentYear by supportViewModel.currentYear.collectAsState()
    val currentMonth by supportViewModel.currentMonth.collectAsState()
    // Calls view model to get workouts from database and update view model
    logViewModel.getWorkoutsByYearMonth(year = currentYear, month = currentMonth)
    // Collects workouts as state from view model
    val workouts by logViewModel.workouts.collectAsState()

    // The actual list
    LazyColumn(modifier = Modifier.fillMaxWidth(0.75f)) {
        itemsIndexed(
            workouts
        ) { index, item ->
            WorkoutCard(
                workoutWithExercises = item,
                navController = navController,
                logViewModel = logViewModel,
                supportViewModel = supportViewModel
            )
        }
    }
}

/**
 * @author Oskar Wiiala
 * @param workoutWithExercises the workout including its exercises
 * @param navController
 * @param logViewModel
 * This composable is the UI for an individual workout in the list of workouts
 * User navigates to ViewWorkoutScreen by clicking on the card, hence the OptIn()
 * User can delete the workout by clicking on the trash can IconButton
 * User navigates to EditWorkoutScreen by clicking on the edit IconButton
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WorkoutCard(
    workoutWithExercises: WorkoutWithExercises,
    navController: NavController,
    logViewModel: LogViewModel,
    supportViewModel: SupportViewModel
) {
    val TAG = "WorkoutCard"

    // Handles closing/showing deletion dialog
    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    if (showDeleteDialog) {
        DeleteWorkoutDialog(
            logViewModel = logViewModel,
            supportViewModel = supportViewModel,
            workoutID = workoutWithExercises.workout.workoutID,
            setShowDialog = {
                showDeleteDialog = it
            })
    }

    // main content
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = 5.dp,
        border = BorderStroke(2.dp, MaterialTheme.colors.primary),
        onClick = {
            // navigates to ViewWorkoutScreen, passing the workoutID along with it
            navController.navigate(WorkoutScreens.ViewWorkoutScreen.withArgs(workoutWithExercises.workout.workoutID))
        }
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
            ) {
                Text(
                    text = "${workoutWithExercises.workout.day}.${workoutWithExercises.workout.month}.${workoutWithExercises.workout.year}",
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold
                )

                // get first three exercises from workoutWithExercises to be used as preview
                val firstThreeExercises = workoutWithExercises.exercises.take(3)
                firstThreeExercises.forEach {
                    Text(text = it.description)
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                IconButton(onClick = {
                    Log.d("delete", "clicked delete")
                    showDeleteDialog = true
                }) {
                    Icon(
                        modifier = Modifier
                            .height(32.dp)
                            .width(32.dp),
                        painter = painterResource(id = R.drawable.delete_icon_24),
                        contentDescription = "Delete workout"
                    )
                }
                IconButton(onClick = {
                    Log.d("edit", "clicked edit")
                    navController.navigate(
                        WorkoutScreens.EditWorkoutScreen.withArgs(
                            workoutWithExercises.workout.workoutID
                        )
                    )
                }) {
                    Icon(
                        modifier = Modifier
                            .height(32.dp)
                            .width(32.dp),
                        painter = painterResource(id = R.drawable.edit_icon_24),
                        contentDescription = "Edit workout"
                    )
                }
            }
        }
    }
}

/**
 * @author Oskar Wiiala
 * @param logViewModel
 * @param workoutID identifier of the individual workout
 * @param setShowDialog handles closing of the dialog
 * Ths composable handles the dialog for deleting an exercise
 */
@Composable
fun DeleteWorkoutDialog(
    logViewModel: LogViewModel,
    supportViewModel: SupportViewModel,
    workoutID: Int,
    setShowDialog: (Boolean) -> Unit
) {
    val TAG = "DeleteWorkoutDialog"

    val currentYear by supportViewModel.currentYear.collectAsState()
    val currentMonth by supportViewModel.currentMonth.collectAsState()

    // Main content
    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            border = BorderStroke(2.dp, MaterialTheme.colors.primary)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Delete workout?")
                Spacer(modifier = Modifier.height(32.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    // OK button
                    Button(onClick = {
                        // Calls view model to delete the workout from database and refresh view model
                        logViewModel.deleteWorkoutByID(workoutID = workoutID, year = currentYear, month = currentMonth)
                        setShowDialog(false)
                    }) {
                        Text(text = "Ok")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = { setShowDialog(false) }) {
                        Text(text = "Cancel")
                    }
                }
            }
        }
    }
}

