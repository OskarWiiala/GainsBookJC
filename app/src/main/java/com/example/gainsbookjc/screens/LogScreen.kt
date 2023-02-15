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
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gainsbookjc.R
import com.example.gainsbookjc.database.AppDatabase
import com.example.gainsbookjc.database.entities.Year
import com.example.gainsbookjc.database.relations.WorkoutWithExercises
import com.example.gainsbookjc.insertToDatabase
import com.example.gainsbookjc.viewmodels.LogViewModel
import com.example.gainsbookjc.viewmodels.logViewModelFactory
import java.util.Calendar

@Composable
fun LogScreen(lifecycleScope: LifecycleCoroutineScope, context: Context) {
    val TAG = "LogScreen"
    val dao = AppDatabase.getInstance(context).appDao
    val viewModel: LogViewModel = viewModel(factory = logViewModelFactory {
        LogViewModel(context)
    })
    Log.d(TAG, "start runBlocking")

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.primary),
            horizontalArrangement = Arrangement.End
        ) {
            // + new year button
            Button(
                onClick = { Log.d("click", "Clicked + new year") },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                contentPadding = PaddingValues(10.dp)
            ) {
                Text(text = "+ new year")
            }
            Spacer(modifier = Modifier.width(10.dp))

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
            SelectMonthDropdown(listMonths = listMonths)
            Spacer(modifier = Modifier.width(10.dp))
            SelectYearDropdown(viewModel)
            Spacer(modifier = Modifier.width(10.dp))
        }

        Row() {
            WorkoutList(viewModel = viewModel)
            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .padding(bottom = 16.dp, start = 16.dp),
                onClick = { Log.d("fab", "fab clicked: ${viewModel.workouts.value}") },
                contentColor = Color.White,
            ) {
                Text(text = "+", fontSize = 30.sp)
            }
        }
    }
}

@Composable
fun WorkoutList(viewModel: LogViewModel) {
    viewModel.getWorkoutsMVVM()
    val list by viewModel.workouts.collectAsState()
    LazyColumn(modifier = Modifier.fillMaxWidth(0.75f)) {
        itemsIndexed(
            list
        ) { index, item ->
            WorkoutCard(item)
        }
    }
}

@Composable
fun WorkoutCard(item: WorkoutWithExercises) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = 5.dp,
        border = BorderStroke(2.dp, MaterialTheme.colors.primary),
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
                IconButton(onClick = { Log.d("delete", "clicked delete") }) {
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
fun SelectMonthDropdown(listMonths: List<String>) {
    var expanded by remember {
        mutableStateOf(false)
    }
    var currentMonth by remember {
        mutableStateOf(1)
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

    viewModel.getYearsMVVM()
    val list by viewModel.years.collectAsState()

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
                if (list.contains(currentYear)) {
                    val yearIndex = list.indexOf(currentYear)
                    Text(text = "${list[yearIndex].year}")
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
            list.forEachIndexed { itemIndex, itemValue ->
                DropdownMenuItem(
                    onClick = {
                        Log.d("click", "Year dropdown selected: $itemValue")
                        currentYear = itemValue
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
