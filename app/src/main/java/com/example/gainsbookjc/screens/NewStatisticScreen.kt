package com.example.gainsbookjc.screens

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gainsbookjc.*
import com.example.gainsbookjc.R
import com.example.gainsbookjc.viewmodels.StatsViewModel
import com.example.gainsbookjc.viewmodels.SupportViewModel
import com.example.gainsbookjc.viewmodels.supportViewModelFactory
import java.util.*

@Composable
fun NewStatisticScreen(
    context: Context,
    navController: NavController,
) {
    val TAG = "NewStatisticScreen"
    val supportViewModel: SupportViewModel = viewModel(factory = supportViewModelFactory {
        SupportViewModel(context)
    })
    val statsViewModel: StatsViewModel = viewModel(factory = supportViewModelFactory {
        StatsViewModel(context)
    })

    // collects the state of the view model's date
    val dateVM by supportViewModel.date.collectAsState()

    val type by statsViewModel.type.collectAsState()
    val variable by statsViewModel.variable.collectAsState()
    val newValue by statsViewModel.newValue.collectAsState()

    val calendar = Calendar.getInstance()
    // Do this only once
    LaunchedEffect(Unit) {
        supportViewModel.setDate(
            WorkoutDate(
                day = calendar.get(Calendar.DAY_OF_MONTH),
                month = calendar.get(Calendar.MONTH) + 1,
                year = calendar.get(Calendar.YEAR)
            )
        )
    }

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
        Text(text = "New workout", fontSize = 24.sp, fontWeight = FontWeight.Bold)
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
                    painter = painterResource(id = R.drawable.edit_calendar_icon_24),
                    contentDescription = "edit date",
                    Modifier.size(36.dp)
                )
            }
        }

        // Variable and type selection
        Row(modifier = Modifier.fillMaxWidth()) {
            SelectVariableDropdown(
                statsViewModel = statsViewModel,
                supportViewModel = supportViewModel,
                screen = "NewStatisticScreen"
            )
            Spacer(modifier = Modifier.width(16.dp))
            SelectTypeDropdown(
                statsViewModel = statsViewModel,
                supportViewModel = supportViewModel,
                screen = "NewStatisticScreen"
            )
        }

        ValueTextField(statsViewModel)

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
                statsViewModel.insertStatistic(
                    variableName = variable.variableName,
                    type = type,
                    value = newValue,
                    day = dateVM.day,
                    month = dateVM.month,
                    year = dateVM.year,
                )
                // navigates back to LogScreen
                navController.navigate(BottomNavItem.StatsScreen.screen_route)
            }) {
                Text(text = "OK")
            }
            Button(onClick = { navController.navigate(BottomNavItem.StatsScreen.screen_route) }) {
                Text(text = "CANCEL")
            }
        }
    }
}

@Composable
fun ValueTextField(statsViewModel: StatsViewModel) {

    val newValue by statsViewModel.newValue.collectAsState()
    Row(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = newValue.toString(),
            onValueChange = { statsViewModel.setNewValue(it.toDouble()) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Text(text = "Kg", modifier = Modifier.padding(start = 8.dp), fontSize = 30.sp)
    }

}