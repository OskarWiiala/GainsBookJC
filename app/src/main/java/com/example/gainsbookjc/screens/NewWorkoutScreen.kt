package com.example.gainsbookjc.screens

import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gainsbookjc.BottomNavItem
import com.example.gainsbookjc.R
import com.example.gainsbookjc.viewmodels.*
import java.util.*

/**
 * @Author Oskar Wiiala
 * @param context
 * @param navController
 * UI for creating a new workout. Handles UI of the editing, adding and deletion of
 * the workout's exercises. Also handles UI of the changing of the workout's date
 */
@Composable
fun NewWorkoutScreen(context: Context, navController: NavController) {
    val TAG = "NewWorkoutScreen"
    val viewModel: NewWorkoutViewModel = viewModel(factory = newExerciseViewModelFactory {
        NewWorkoutViewModel(context)
    })

    val calendar = Calendar.getInstance()
    // Stores the values of day, month and year of the workout
    // Initialized with current date
    var selectedDay by remember {
        mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH))
    }
    var selectedMonth by remember {
        mutableStateOf(calendar.get(Calendar.MONTH) + 1)
    }
    val selectedMonth2 = calendar.get(Calendar.MONTH)
    var selectedYear by remember {
        mutableStateOf(calendar.get(Calendar.YEAR))
    }

    // Used for displaying the date in a Text element
    var date by remember {
        mutableStateOf("$selectedDay.$selectedMonth.$selectedYear")
    }

    // collects the state of the view model's exercises
    val exercises by viewModel.exercises.collectAsState()

    // context used for DatePicker
    val mContext = LocalContext.current

    // Declaring DatePickerDialog and setting
    // initial values as current values (present year, month and day)
    val mDatePickerDialog = DatePickerDialog(
        mContext,
        { _: DatePicker, mSelectedYear: Int, mSelectedMonth2: Int, mSelectedDay: Int ->
            date = "$mSelectedDay.${mSelectedMonth2 + 1}.$mSelectedYear"
            selectedDay = mSelectedDay
            selectedMonth = mSelectedMonth2 + 1
            selectedYear = mSelectedYear
            Log.d("datepicker", "$selectedDay $selectedMonth $selectedYear")
        }, selectedYear, selectedMonth2, selectedDay
    )

    // UI for displaying and selecting date
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "New workout", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.padding(top = 16.dp)) {
            Text(
                modifier = Modifier.padding(end = 16.dp),
                text = date,
                fontSize = 20.sp,
                color = Color.Gray
            )
            IconButton(onClick = {
                mDatePickerDialog.show()
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.edit_calendar_icon_24),
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
                ExerciseCard(viewModel, exerciseWithIndex.description, exerciseWithIndex.index)
            }
        }

        // Button for adding a new exercise
        AddNewExercise(viewModel)

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
                viewModel.addWorkout(
                    exercises = exercises,
                    day = selectedDay,
                    month = selectedMonth,
                    year = selectedYear
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

/**
 * @author Oskar Wiiala
 * @param viewModel
 * @param description the description of an individual exercise
 * @param exerciseIndex the index of an individual exercise
 * This card is the UI for an individual exercise in the list of exercises
 */
@Composable
fun ExerciseCard(viewModel: NewWorkoutViewModel, description: String, exerciseIndex: Int) {
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
        EditExerciseDialog(
            viewModel = viewModel,
            description = description,
            exerciseIndex = exerciseIndex,
            setShowDialog = {
                showEditDialog = it
            })
    }

    if (showDeleteDialog) {
        DeleteExerciseDialog(
            viewModel = viewModel,
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
 * @param viewModel
 * UI for button  which adds a new exercise to the list of exercises
 */
@Composable
fun AddNewExercise(viewModel: NewWorkoutViewModel) {
    val TAG = "AddNewExercise"

    // used for showing/hiding dialog for adding an exercise
    var showDialog by remember {
        mutableStateOf(false)
    }

    if (showDialog) {
        AddNewExerciseDialog(viewModel = viewModel, setShowDialog = {
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
 * @param viewModel
 * @param setShowDialog callback to close the dialog
 * Dialog for adding a new exercise
 * Uses an editable TextField as user input for the description of the exercise
 */
@Composable
fun AddNewExerciseDialog(viewModel: NewWorkoutViewModel, setShowDialog: (Boolean) -> Unit) {
    val TAG = "AddNewExerciseDialog"

    // Collects exercises as state from view model
    val exercises by viewModel.exercises.collectAsState()

    // Stores the input of the editable TextField
    var textFieldState by remember {
        mutableStateOf("")
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
                Text(text = "Add new exercise")
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = textFieldState,
                    onValueChange = { textFieldState = it },
                    label = { Text(text = "Enter a new exercise here") },
                )
                Spacer(modifier = Modifier.height(32.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    // Handles OK click
                    Button(onClick = {
                        // Copies the contents of exercises from the view model to a new list
                        val exercisesList = mutableListOf<ExerciseWithIndex>()
                        exercises.forEach { exerciseWithIndex ->
                            exercisesList.add(exerciseWithIndex)
                        }

                        var indexOfLast = 1

                        // Creates a list of indexes based on the exercise index
                        if (exercisesList.isNotEmpty()) {
                            Log.d(TAG, "list not empty")
                            val indexList: MutableList<Int> = mutableListOf()
                            exercisesList.forEach { exerciseWithIndex ->
                                indexList.add(exerciseWithIndex.index)
                            }
                            indexOfLast = indexList.max() + 1
                        }

                        // Creates a new exercise with an index one higher than the previous
                        exercisesList.add(
                            ExerciseWithIndex(
                                description = textFieldState,
                                indexOfLast
                            )
                        )

                        // Finally recreates the exercises in the view model by adding a new list
                        // containing all of the exercises
                        viewModel.addExercises(exercisesList)
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
 * @param description the description of an individual exercise
 * @param exerciseIndex the index of an individual exercise
 * @param setShowDialog callback to close the dialog
 * This dialog is used to edit the description of an individual exercise
 */
@Composable
fun EditExerciseDialog(
    viewModel: NewWorkoutViewModel,
    description: String,
    exerciseIndex: Int,
    setShowDialog: (Boolean) -> Unit
) {
    val TAG = "EditExerciseDialog"

    // Collects exercises as state from view model
    val list by viewModel.exercises.collectAsState()

    // Stores the input of the editable TextField
    // Initializes the state with the old description
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
                Text(text = "Edit exercise")
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = textFieldState,
                    onValueChange = { textFieldState = it },
                    label = { Text(text = "Edit your exercise here") },
                )
                Spacer(modifier = Modifier.height(32.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    // OK click
                    Button(onClick = {
                        // Copies the contents of the view model's list of exercises to a new list
                        val exercisesList = list.toMutableList()

                        // removes the selected exercise from the newly created list
                        exercisesList.remove(
                            ExerciseWithIndex(
                                description = description,
                                index = exerciseIndex
                            )
                        )
                        // Adds the new edited exercise to the newly created list
                        exercisesList.add(
                            ExerciseWithIndex(
                                description = textFieldState,
                                index = exerciseIndex
                            )
                        )
                        // Sorts the list ascending based on the index of the exercise
                        exercisesList.sortBy { exerciseWithIndex -> exerciseWithIndex.index }
                        // Finally updates the view model with the new list of exercises
                        viewModel.addExercises(exercisesList)
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
 * @param description the description of an individual exercise
 * @param exerciseIndex the index of an individual exercise
 * @param setShowDialog callback to close the dialog
 * This dialog deletes an individual exercise from the list of exercises
 */
@Composable
fun DeleteExerciseDialog(
    viewModel: NewWorkoutViewModel,
    description: String,
    exerciseIndex: Int,
    setShowDialog: (Boolean) -> Unit
) {
    val TAG = "DeleteExerciseDialog"

    // Collects exercises as state from view model
    val exercises by viewModel.exercises.collectAsState()

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
                        // Copies the contents of the view model's list of exercises to a new list
                        val exercisesList = exercises.toMutableList()

                        // removes the selected exercise from the newly created list
                        exercisesList.remove(
                            ExerciseWithIndex(
                                description = description,
                                index = exerciseIndex
                            )
                        )
                        // Sorts the list ascending based on the index of the exercise
                        exercisesList.sortBy { exerciseWithIndex -> exerciseWithIndex.index }
                        // Finally updates the view model with the new list of exercises
                        viewModel.addExercises(exercisesList)
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