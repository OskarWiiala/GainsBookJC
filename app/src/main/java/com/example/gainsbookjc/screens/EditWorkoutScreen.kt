package com.example.gainsbookjc.screens

import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.R
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
import com.example.gainsbookjc.BottomNavItem
import com.example.gainsbookjc.viewmodels.*
import java.util.*

@Composable
fun EditWorkoutScreen(context: Context, navController: NavController, workoutID: Int) {
    val TAG = "EditWorkoutScreen"

    val viewModel: EditWorkoutViewModel = viewModel(factory = editExerciseViewModelFactory {
        EditWorkoutViewModel(context = context, workoutID = workoutID)
    })

    var day by remember {
        mutableStateOf(0)
    }
    var month by remember {
        mutableStateOf(0)
    }
    var year by remember {
        mutableStateOf(0)
    }

    val dateVM by viewModel.date.collectAsState()
    val exercisesVM by viewModel.exercises.collectAsState()

    Log.d(TAG, "dateVM: $dateVM")
    Log.d(TAG, "dateVM day: ${dateVM.day}")

    val mContext = LocalContext.current

    // Declaring DatePickerDialog and setting
    // initial values as current values (present year, month and day)
    fun initDialog() {
        day = dateVM.day
        month = dateVM.month - 1
        year = dateVM.year
        val mDatePickerDialog = DatePickerDialog(
            mContext,
            { _: DatePicker, mSelectedYear: Int, mSelectedMonth: Int, mSelectedDay: Int ->
                val date =
                    WorkoutDate(day = mSelectedDay, month = mSelectedMonth + 1, year = mSelectedYear)
                viewModel.setDate(date)
            }, year, month, day
        )
        mDatePickerDialog.show()
    }

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
                text = "${dateVM.day}.${dateVM.month}.${dateVM.year}",
                fontSize = 20.sp,
                color = Color.Gray
            )
            IconButton(onClick = {
                initDialog()
            }) {
                Icon(
                    painter = painterResource(id = com.example.gainsbookjc.R.drawable.edit_calendar_icon_24),
                    contentDescription = "edit date",
                    Modifier.size(36.dp)
                )
            }
        }
        Button(onClick = { navController.navigate(BottomNavItem.LogScreen.screen_route) }) {
            Text(text = "back")
        }
    }
}