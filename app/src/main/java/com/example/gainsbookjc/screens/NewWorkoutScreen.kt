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

@Composable
fun NewWorkoutScreen(context: Context, navController: NavController) {
    val TAG = "NewWorkoutScreen"
    val viewModel: NewExerciseViewModel = viewModel(factory = newExerciseViewModelFactory {
        NewExerciseViewModel(context)
    })

    val calendar = Calendar.getInstance()
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

    var date by remember {
        mutableStateOf("$selectedDay.$selectedMonth.$selectedYear")
    }

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
                Log.d(TAG, "clicked edit calendar")
                mDatePickerDialog.show()
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.edit_calendar_icon_24),
                    contentDescription = "edit date",
                    Modifier.size(36.dp)
                )
            }
        }

        val list by viewModel.exercises.collectAsState()

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
        ) {
            itemsIndexed(list) { index, exerciseWithIndex ->
                ExerciseCard(viewModel, exerciseWithIndex.description, exerciseWithIndex.index)
            }
        }

        AddNewExercise(viewModel)

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Button(onClick = {
                Log.d(TAG, "Attempting insert workout")
                viewModel.addWorkout(list, selectedDay, selectedMonth, selectedYear)
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

@Composable
fun AddNewExercise(viewModel: NewExerciseViewModel) {
    val TAG = "AddNewExercise"

    var showDialog by remember {
        mutableStateOf(false)
    }

    if (showDialog) {
        AddNewExerciseDialog(viewModel = viewModel, setShowDialog = {
            showDialog = it
        })
    }

    Button(onClick = {
        Log.d(TAG, "clicked add new exercise")
        showDialog = true

    }) {
        Text(text = "+ NEW EXERCISE")
    }
}

@Composable
fun AddNewExerciseDialog(viewModel: NewExerciseViewModel, setShowDialog: (Boolean) -> Unit) {
    val TAG = "AddNewExerciseDialog"

    val list by viewModel.exercises.collectAsState()
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
                Text(text = "Add new exercise")
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = textFieldState,
                    onValueChange = { textFieldState = it },
                    label = { Text(text = "Enter a new exercise here") },
                )
                Spacer(modifier = Modifier.height(32.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(onClick = {
                        val exercisesList = mutableListOf<ExerciseWithIndex>()
                        list.forEach { exerciseWithIndex ->
                            exercisesList.add(exerciseWithIndex)
                        }
                        var indexOfLast = 1
                        if (exercisesList.isNotEmpty()) {
                            Log.d(TAG, "list not empty")
                            val indexList: MutableList<Int> = mutableListOf()
                            exercisesList.forEach { exerciseWithIndex ->
                                indexList.add(exerciseWithIndex.index)
                            }
                            indexOfLast = indexList.max() + 1
                        }
                        Log.d(TAG, "indexOfLast: $indexOfLast")
                        exercisesList.add(
                            ExerciseWithIndex(
                                description = textFieldState,
                                indexOfLast
                            )
                        )
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

@Composable
fun EditExerciseDialog(
    viewModel: NewExerciseViewModel,
    description: String,
    exerciseIndex: Int,
    setShowDialog: (Boolean) -> Unit
) {
    val TAG = "EditExerciseDialog"

    var textFieldState by remember {
        mutableStateOf("")
    }

    val list by viewModel.exercises.collectAsState()

    /*list.forEach { exerciseWithIndex ->
        if (exerciseIndex == exerciseWithIndex.index) {
            Log.d(TAG, "Found exercise: ${exerciseWithIndex.description}")
            textFieldState = exerciseWithIndex.description
        }
    }*/
    textFieldState = description

    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Surface(
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
                    Button(onClick = {
                        val exercisesList = list.toMutableList()
                        exercisesList.remove(
                            ExerciseWithIndex(
                                description = description,
                                index = exerciseIndex
                            )
                        )
                        exercisesList.add(
                            ExerciseWithIndex(
                                description = textFieldState,
                                index = exerciseIndex
                            )
                        )
                        exercisesList.sortBy { exerciseWithIndex -> exerciseWithIndex.index }
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

@Composable
fun DeleteExerciseDialog(
    viewModel: NewExerciseViewModel,
    description: String,
    exerciseIndex: Int,
    setShowDialog: (Boolean) -> Unit
) {
    val TAG = "DeleteExerciseDialog"

    val list by viewModel.exercises.collectAsState()

    list.forEach { exerciseWithIndex ->
        if (exerciseIndex == exerciseWithIndex.index) {
            Log.d(TAG, "Found exercise: $exerciseWithIndex")
        }
    }

    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            border = BorderStroke(2.dp, MaterialTheme.colors.primary)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Delete exercise?")
                Spacer(modifier = Modifier.height(32.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(onClick = {
                        val exercisesList = list.toMutableList()
                        exercisesList.remove(
                            ExerciseWithIndex(
                                description = description,
                                index = exerciseIndex
                            )
                        )
                        exercisesList.sortBy { exerciseWithIndex -> exerciseWithIndex.index }
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


@Composable
fun ExerciseCard(viewModel: NewExerciseViewModel, description: String, index: Int) {
    val TAG = "ExerciseCard"

    var showEditDialog by remember {
        mutableStateOf(false)
    }

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    if (showEditDialog) {
        EditExerciseDialog(viewModel = viewModel, description = description, exerciseIndex = index, setShowDialog = {
            showEditDialog = it
        })
    }

    if (showDeleteDialog) {
        DeleteExerciseDialog(
            viewModel = viewModel,
            description = description,
            exerciseIndex = index,
            setShowDialog = { showDeleteDialog = it })
    }

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
            Row(horizontalArrangement = Arrangement.Start) {
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