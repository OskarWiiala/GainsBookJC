package com.example.gainsbookjc.screens

import android.app.DatePickerDialog
import android.util.Log
import android.widget.DatePicker
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gainsbookjc.BottomNavItem
import com.example.gainsbookjc.R
import java.util.*

@Composable
fun NewWorkoutScreen(navController: NavController) {
    val TAG = "NewWorkoutScreen"

    val calendar = Calendar.getInstance()
    var selectedDay = calendar.get(Calendar.DAY_OF_MONTH)
    var selectedMonth = calendar.get(Calendar.MONTH) + 1
    var selectedMonth2 = calendar.get(Calendar.MONTH)
    var selectedYear = calendar.get(Calendar.YEAR)

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

        var list by remember {
            mutableStateOf(listOf("test1", "test2", "test3", "test4", "test5", "test6", "test7", "test8", "test9", "test10"))
        }

        LazyColumn(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.7f)) {
            itemsIndexed(list) { index, description ->
                ExerciseCard(description)
            }
        }
        
        Button(onClick = { Log.d(TAG, "clicked add new exercise") }) {
            Text(text = "+ NEW EXERCISE")
        }

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Button(onClick = {
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
fun ExerciseCard(description: String) {
    val TAG = "ExerciseCard"
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 5.dp,
        border = BorderStroke(2.dp, MaterialTheme.colors.primary),
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Row(horizontalArrangement = Arrangement.Start) {
                Text(text = description)
            }
            Row(horizontalArrangement = Arrangement.End) {
                IconButton(onClick = { Log.d(TAG, "clicked edit") }) {
                    Icon(
                        painter = painterResource(id = R.drawable.edit_icon_24),
                        contentDescription = "Edit exercise"
                    )
                }
                IconButton(onClick = { Log.d(TAG, "clicked delete") }) {
                    Icon(
                        painter = painterResource(id = R.drawable.delete_icon_24),
                        contentDescription = "delete exercise"
                    )
                }
            }
        }
    }
}