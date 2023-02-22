package com.example.gainsbookjc.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gainsbookjc.WorkoutScreens
import com.example.gainsbookjc.R
import com.example.gainsbookjc.database.entities.Year
import com.example.gainsbookjc.database.relations.WorkoutWithExercises
import com.example.gainsbookjc.viewmodels.LogViewModel
import com.example.gainsbookjc.viewmodels.logViewModelFactory
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
    val viewModel: LogViewModel = viewModel(factory = logViewModelFactory {
        LogViewModel(context)
    })
    
    // Initializes month and year to be current month and year
    viewModel.setCurrentYear(Calendar.getInstance().get(Calendar.YEAR))
    viewModel.setCurrentMonth(Calendar.getInstance().get(Calendar.MONTH) + 1)

    // Top bar elements
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.primary),
            horizontalArrangement = Arrangement.End
        ) {
            // + new year button
            AddNewYear(viewModel = viewModel)
            Spacer(modifier = Modifier.width(10.dp))
            SelectMonthDropdown(viewModel)
            Spacer(modifier = Modifier.width(10.dp))
            SelectYearDropdown(viewModel)
            Spacer(modifier = Modifier.width(10.dp))
        }

        // List related elements
        Row(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()) {
            WorkoutList(viewModel = viewModel, navController = navController)
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
 * @param viewModel
 * This composable is the + new year button seen in the UI
 * It handles displaying the add new year dialog
 */
@Composable
fun AddNewYear(viewModel: LogViewModel) {
    
    // handles showing/closing add new year dialog
    var showDialog by remember {
        mutableStateOf(false)
    }

    if (showDialog) {
        NewYearDialog(viewModel = viewModel, setShowDialog = {
            showDialog = it
        })
    }

    Button(
        onClick = { showDialog = true },
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
        contentPadding = PaddingValues(10.dp)
    ) {
        Text(text = "+ new year")
    }
}

/**
 * @author Oskar Wiiala
 * @param viewModel
 * @param setShowDialog callback to close dialog
 * This dialog handles user input in an editable TextField
 * and calls the view model to add the new year to database
 */
@Composable
fun NewYearDialog(viewModel: LogViewModel, setShowDialog: (Boolean) -> Unit) {
    val TAG = "NewYearDialog"
    
    // handles storing user input
    var textFieldState by remember {
        mutableStateOf("")
    }
    
    // Main content
    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            border = BorderStroke(2.dp, MaterialTheme.colors.primary)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Add new year")
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = textFieldState,
                    onValueChange = { textFieldState = it },
                    label = { Text(text = "Enter a new year here") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    // OK button
                    Button(onClick = {
                        // Only accepts integers
                        val input = textFieldState.toIntOrNull()
                        if (input != null) {
                            // calls viewModel to add new year to database and update view model
                            viewModel.insertYear(input)
                            setShowDialog(false)
                        } else {
                            Log.d(TAG, "Input not integer!")
                        }
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

/**
 * @author Oskar Wiiala
 * @param viewModel
 * @param navController
 * This composable handles displaying a list of WorkoutCards
 */
@Composable
fun WorkoutList(viewModel: LogViewModel, navController: NavController) {
    // Calls view model to get workouts from database and update view model
    viewModel.getWorkouts()
    // Collects workouts as state from view model
    val workouts by viewModel.workouts.collectAsState()
    
    // The actual list
    LazyColumn(modifier = Modifier.fillMaxWidth(0.75f)) {
        itemsIndexed(
            workouts
        ) { index, item ->
            WorkoutCard(workoutWithExercises = item, navController = navController, viewModel = viewModel)
        }
    }
}

/**
 * @author Oskar Wiiala
 * @param workoutWithExercises the workout including its exercises
 * @param navController
 * @param viewModel
 * This composable is the UI for an individual workout in the list of workouts
 * User navigates to ViewWorkoutScreen by clicking on the card, hence the OptIn()
 * User can delete the workout by clicking on the trash can IconButton
 * User navigates to EditWorkoutScreen by clicking on the edit IconButton
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WorkoutCard(workoutWithExercises: WorkoutWithExercises, navController: NavController, viewModel: LogViewModel) {
    val TAG = "WorkoutCard"

    // Handles closing/showing deletion dialog
    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    if (showDeleteDialog) {
        DeleteWorkoutDialog(
            viewModel = viewModel,
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
                    navController.navigate(WorkoutScreens.EditWorkoutScreen.withArgs(workoutWithExercises.workout.workoutID))
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
 * @param viewModel
 * @param workoutID identifier of the individual workout
 * @param setShowDialog handles closing of the dialog
 * Ths composable handles the dialog for deleting an exercise
 */
@Composable
fun DeleteWorkoutDialog(
    viewModel: LogViewModel,
    workoutID: Int,
    setShowDialog: (Boolean) -> Unit
) {
    val TAG = "DeleteWorkoutDialog"

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
                        viewModel.deleteWorkoutByID(workoutID)
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

/**
 * @author Oskar Wiiala
 * @param viewModel
 * This composable is the button for displaying and selecting the month
 */
@Composable
fun SelectMonthDropdown(viewModel: LogViewModel) {
    val listMonths = listOf(
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
    )
    
    // Handles opening/closing dropdown menu
    var expanded by remember {
        mutableStateOf(false)
    }
    
    // Stores selected month as Int with the initial value of current month
    var month by remember {
        mutableStateOf(Calendar.getInstance().get(Calendar.MONTH))
    }
    // Select month dropdown
    Box() {
        Button(
            onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
        ) {
            Row() {
                Icon(
                    painter = painterResource(id = R.drawable.down_icon_24),
                    contentDescription = "Dropdown"
                )
                Text(text = listMonths[month])
            }
        }

        // drop down menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            // adding items
            listMonths.forEachIndexed { itemIndex, itemValue ->
                DropdownMenuItem(
                    onClick = {
                        month = itemIndex
                        viewModel.setCurrentMonth(month + 1)
                        viewModel.getWorkouts()
                        expanded = false
                    },
                    enabled = (itemIndex != month)
                ) {
                    Text(text = itemValue)
                }
            }
        }
    }
}

/**
 * @author Oskar Wiiala
 * @param viewModel
 * This composable is the button for displaying and selecting the year
 */
@Composable
fun SelectYearDropdown(viewModel: LogViewModel) {
    val TAG = "SelectYearDropdown"

    // Calls view model to get years from database and update view model
    viewModel.getYears()
    
    // Gets years from view model as state
    val years by viewModel.years.collectAsState()

    // handles closing/showing dropdown menu
    var expanded by remember {
        mutableStateOf(false)
    }

    // Stores selected year as Int with the initial value of current year
    var year by remember {
        mutableStateOf(Year(Calendar.getInstance().get(Calendar.YEAR)))
    }

    // Select year dropdown
    Box() {
        Button(
            onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
        ) {
            Row() {
                Icon(
                    painter = painterResource(id = R.drawable.down_icon_24),
                    contentDescription = "Dropdown"
                )
                // 
                if (years.contains(year)) {
                    /*val yearIndex = years.indexOf(year)
                    Text(text = "${years[yearIndex].year}")*/
                    Text(text = "${year.year}")
                } else if (years.isEmpty()) {
                    // Adds a new year with value of current year if no years exist
                    viewModel.insertYear(Calendar.getInstance().get(Calendar.YEAR))
                    if (years.contains(year)) {
                        /*val yearIndex = years.indexOf(year)
                        Text(text = "${years[yearIndex].year}")*/
                        Text(text = "${year.year}")
                    }
                }

            }
        }

        // drop down menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            // adding items
            years.forEachIndexed { itemIndex, itemValue ->
                DropdownMenuItem(
                    onClick = {
                        year = itemValue
                        viewModel.setCurrentYear(year.year)
                        viewModel.getWorkouts()
                        expanded = false
                    },
                    enabled = (itemValue != year)
                ) {
                    Text(text = "${itemValue.year}")
                }
            }
        }
    }
}

