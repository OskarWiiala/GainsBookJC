package com.example.gainsbookjc

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.gainsbookjc.database.entities.Year
import com.example.gainsbookjc.viewmodels.LogViewModel
import com.example.gainsbookjc.viewmodels.SupportViewModel
import java.util.*

/**
 * @author Oskar Wiiala
 * @param supportViewModel
 * This composable is the + new year button seen in the UI
 * It handles displaying the add new year dialog
 */
@Composable
fun AddNewYearButton(supportViewModel: SupportViewModel) {

    // handles showing/closing add new year dialog
    var showDialog by remember {
        mutableStateOf(false)
    }

    if (showDialog) {
        AddNewYearDialog(supportViewModel = supportViewModel, setShowDialog = {
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
 * @param supportViewModel
 * @param setShowDialog callback to close dialog
 * This dialog handles user input in an editable TextField
 * and calls the view model to add the new year to database
 */
@Composable
fun AddNewYearDialog(supportViewModel: SupportViewModel, setShowDialog: (Boolean) -> Unit) {
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
                            supportViewModel.insertYear(input)
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
 * @param supportViewModel
 * This composable is the button for displaying and selecting the month
 */
@Composable
fun SelectMonthDropdown(
    supportViewModel: SupportViewModel,
    logViewModel: LogViewModel?,
    screen: String
) {
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

    // Get currentYear and currentMonth from supportViewModel as state
    val currentYear by supportViewModel.currentYear.collectAsState()
    val currentMonth by supportViewModel.currentMonth.collectAsState()

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
                        supportViewModel.setCurrentMonth(month + 1)
                        if (screen == "LogScreen") {
                            logViewModel?.getWorkoutsByYearMonth(
                                year = currentYear,
                                month = currentMonth
                            )
                        } else if (screen == "StatsScreen") {

                        }

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
 * @param supportViewModel
 * This composable is the button for displaying and selecting the year
 */
@Composable
fun SelectYearDropdown(
    supportViewModel: SupportViewModel,
    logViewModel: LogViewModel?,
    screen: String
) {
    val TAG = "SelectYearDropdown"

    // Calls view model to get years from database and update view model
    supportViewModel.getYears()

    // Gets years from view model as state
    val years by supportViewModel.years.collectAsState()

    // Get currentYear and currentMonth from supportViewModel as state
    val currentYear by supportViewModel.currentYear.collectAsState()
    val currentMonth by supportViewModel.currentMonth.collectAsState()

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
                    supportViewModel.insertYear(Calendar.getInstance().get(Calendar.YEAR))
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
                        supportViewModel.setCurrentYear(year.year)
                        if (screen == "LogScreen") {
                            logViewModel?.getWorkoutsByYearMonth(
                                year = currentYear,
                                month = currentMonth
                            )
                        } else if (screen == "StatsScreen") {

                        }

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

/**
 * @author Oskar Wiiala
 * @param supportViewModel
 * @param description the description of an individual exercise
 * @param exerciseIndex the index of an individual exercise
 * This card is the UI for an individual exercise in the list of exercises
 */
@Composable
fun ExerciseCard(supportViewModel: SupportViewModel, description: String, exerciseIndex: Int) {
    val TAG = "ExerciseCard"

    // used for showing/hiding dialog for editing an exercise
    var showEditDialog by remember {
        mutableStateOf(false)
    }

    // used for showing/hiding dialog for deleting an exercise
    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    if (showEditDialog) {
        HandleExerciseDialog(
            supportViewModel = supportViewModel,
            description = description,
            exerciseIndex = exerciseIndex,
            type = "edit",
            setShowDialog = {
                showEditDialog = it
            })
    }

    if (showDeleteDialog) {
        DeleteExerciseDialog(
            supportViewModel = supportViewModel,
            description = description,
            exerciseIndex = exerciseIndex,
            setShowDialog = { showDeleteDialog = it })
    }

    // Main contents
    // Includes the description of the workout and edit/delete IconButtons
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 5.dp,
        border = BorderStroke(2.dp, MaterialTheme.colors.primary),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth(0.7f)) {
                Text(text = description)
            }
            Row(horizontalArrangement = Arrangement.End) {
                IconButton(onClick = {
                    Log.d(TAG, "clicked edit")
                    showEditDialog = true
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.edit_icon_24),
                        contentDescription = "Edit exercise"
                    )
                }
                IconButton(onClick = {
                    Log.d(TAG, "clicked delete")
                    showDeleteDialog = true
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.delete_icon_24),
                        contentDescription = "delete exercise"
                    )
                }
            }
        }
    }
}

/**
 * @author Oskar Wiiala
 * @param supportViewModel
 * UI for button  which adds a new exercise to the list of exercises
 */
@Composable
fun AddNewExerciseButton(supportViewModel: SupportViewModel) {
    val TAG = "AddNewExercise"

    // used for showing/hiding dialog for adding an exercise
    var showDialog by remember {
        mutableStateOf(false)
    }

    if (showDialog) {
        HandleExerciseDialog(
            supportViewModel = supportViewModel,
            description = "",
            exerciseIndex = 0,
            type = "new",
            setShowDialog = {
                showDialog = it
            })
    }

    // Clicking the button displays the dialog for adding a new exercise
    Button(onClick = {
        Log.d(TAG, "clicked add new exercise")
        showDialog = true
    }) {
        Text(text = "+ NEW EXERCISE")
    }
}

/**
 * @author OskarWiiala
 * @param supportViewModel
 * @param setShowDialog callback to close the dialog
 * Dialog for adding a new exercise
 * Uses an editable TextField as user input for the description of the exercise
 */
@Composable
fun HandleExerciseDialog(
    supportViewModel: SupportViewModel,
    description: String,
    exerciseIndex: Int,
    type: String,
    setShowDialog: (Boolean) -> Unit
) {
    val TAG = "AddNewExerciseDialog"

    // Collects exercises as state from view model
    val exercises by supportViewModel.exercises.collectAsState()

    // Stores the input of the editable TextField
    var textFieldState by remember {
        mutableStateOf(description)
    }

    // Main content
    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.6f),
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            border = BorderStroke(2.dp, MaterialTheme.colors.primary)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (type == "new") Text(text = "Add new exercise")
                else if (type == "edit") Text(text = "Edit exercise")
                Spacer(modifier = Modifier.height(16.dp))
                if (type == "new") {
                    TextField(
                        value = textFieldState,
                        onValueChange = { textFieldState = it },
                        label = { Text(text = "Enter a new exercise here") },
                    )
                } else if (type == "edit") {
                    TextField(
                        value = textFieldState,
                        onValueChange = { textFieldState = it },
                        label = { Text(text = "Edit your exercise here") },
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    // Handles OK click
                    Button(onClick = {
                        // Copies the contents of exercises from the view model to a new list
                        var exercisesList =
                            mutableListOf(ExerciseWithIndex(description = "fail", index = 0))

                        if (type == "new") {
                            Log.d(TAG, "new")
                            exercisesList = newExercise(
                                exercises = exercises.toMutableList(),
                                textFieldState = textFieldState
                            )
                        } else if (type == "edit") {
                            exercisesList = editExercise(
                                exercises = exercises.toMutableList(),
                                description = description,
                                exerciseIndex = exerciseIndex,
                                textFieldState = textFieldState
                            )
                        }

                        // Finally recreates the exercises in the view model by adding a new list
                        // containing all of the exercises
                        supportViewModel.addExercises(exercisesList)
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
 * @param supportViewModel
 * @param description the description of an individual exercise
 * @param exerciseIndex the index of an individual exercise
 * @param setShowDialog callback to close the dialog
 * This dialog deletes an individual exercise from the list of exercises
 */
@Composable
fun DeleteExerciseDialog(
    supportViewModel: SupportViewModel,
    description: String,
    exerciseIndex: Int,
    setShowDialog: (Boolean) -> Unit
) {
    val TAG = "DeleteExerciseDialog"

    // Collects exercises as state from view model
    val exercises by supportViewModel.exercises.collectAsState()

    // Main content
    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            border = BorderStroke(2.dp, MaterialTheme.colors.primary)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Delete exercise?")
                Spacer(modifier = Modifier.height(32.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    // OK button
                    Button(onClick = {
                        val exercisesList = deleteExercise(
                            exercises = exercises.toMutableList(),
                            description = description,
                            exerciseIndex = exerciseIndex
                        )

                        // Finally updates the view model with the new list of exercises
                        supportViewModel.addExercises(exercisesList)
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