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
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gainsbookjc.WorkoutScreens
import com.example.gainsbookjc.R
import com.example.gainsbookjc.database.AppDatabase
import com.example.gainsbookjc.database.entities.Year
import com.example.gainsbookjc.database.relations.WorkoutWithExercises
import com.example.gainsbookjc.viewmodels.LogViewModel
import com.example.gainsbookjc.viewmodels.logViewModelFactory
import java.util.Calendar

@Composable
fun LogScreen(
    context: Context,
    navController: NavController
) {
    val TAG = "LogScreen"
    val viewModel: LogViewModel = viewModel(factory = logViewModelFactory {
        LogViewModel(context)
    })
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
        Row(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
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

@Composable
fun AddNewYear(viewModel: LogViewModel) {
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

@Composable
fun NewYearDialog(viewModel: LogViewModel, setShowDialog: (Boolean) -> Unit) {
    val TAG = "NewYearDialog"
    var textFieldState by remember {
        mutableStateOf("")
    }
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
                    Button(onClick = {
                        val input = textFieldState.toIntOrNull()
                        if (input != null) {
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

@Composable
fun WorkoutList(viewModel: LogViewModel, navController: NavController) {
    viewModel.getWorkouts()
    val list by viewModel.workouts.collectAsState()
    LazyColumn(modifier = Modifier.fillMaxWidth(0.75f)) {
        itemsIndexed(
            list
        ) { index, item ->
            WorkoutCard(item = item, navController = navController, viewModel = viewModel)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WorkoutCard(item: WorkoutWithExercises, navController: NavController, viewModel: LogViewModel) {
    val TAG = "WorkoutCard"

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    if (showDeleteDialog) {
        DeleteWorkoutDialog(
            viewModel = viewModel,
            workoutID = item.workout.workoutID,
            setShowDialog = {
                showDeleteDialog = it
            })
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = 5.dp,
        border = BorderStroke(2.dp, MaterialTheme.colors.primary),
        onClick = {
            Log.d(TAG, "Clicked on card with workoutID: ${item.workout.workoutID}")
            navController.navigate(WorkoutScreens.ViewWorkoutScreen.withArgs(item.workout.workoutID))
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
                    text = "${item.workout.day}.${item.workout.month}.${item.workout.year}",
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold
                )

                // get first three exercises form item to be used as preview
                val firstThreeExercises = item.exercises.take(3)
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
                IconButton(onClick = { Log.d("edit", "clicked edit") }) {
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

@Composable
fun DeleteWorkoutDialog(
    viewModel: LogViewModel,
    workoutID: Int,
    setShowDialog: (Boolean) -> Unit
) {
    val TAG = "DeleteWorkoutDialog"

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
                    Button(onClick = {
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
    var expanded by remember {
        mutableStateOf(false)
    }
    var currentMonth by remember {
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
                Text(text = listMonths[currentMonth])
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
                        Log.d("click", "Year dropdown selected: $itemValue")
                        currentMonth = itemIndex
                        viewModel.setCurrentMonth(currentMonth + 1)
                        viewModel.getWorkouts()
                        expanded = false
                    },
                    enabled = (itemIndex != currentMonth)
                ) {
                    Text(text = itemValue)
                }
            }
        }
    }
}

@Composable
fun SelectYearDropdown(viewModel: LogViewModel) {
    val TAG = "SelectYearDropdown"

    viewModel.getYears()
    val years by viewModel.years.collectAsState()

    var expanded by remember {
        mutableStateOf(false)
    }

    var currentYear by remember {
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
                if (years.contains(currentYear)) {
                    val yearIndex = years.indexOf(currentYear)
                    Text(text = "${years[yearIndex].year}")
                } else if (years.isEmpty()) {
                    viewModel.insertYear(Calendar.getInstance().get(Calendar.YEAR))
                    if (years.contains(currentYear)) {
                        val yearIndex = years.indexOf(currentYear)
                        Text(text = "${years[yearIndex].year}")
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
                        Log.d("click", "Year dropdown selected: $itemValue")
                        currentYear = itemValue
                        viewModel.setCurrentYear(currentYear.year)
                        viewModel.getWorkouts()
                        expanded = false
                    },
                    enabled = (itemValue != currentYear)
                ) {
                    Text(text = "${itemValue.year}")
                }
            }
        }
    }
}

