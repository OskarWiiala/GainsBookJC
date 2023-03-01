package com.example.gainsbookjc.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gainsbookjc.*
import com.example.gainsbookjc.R
import com.example.gainsbookjc.database.entities.Statistic
import com.example.gainsbookjc.viewmodels.StatsViewModel
import com.example.gainsbookjc.viewmodels.SupportViewModel
import com.example.gainsbookjc.viewmodels.statsViewModelFactory
import com.example.gainsbookjc.viewmodels.supportViewModelFactory
import com.github.tehras.charts.line.LineChart
import com.github.tehras.charts.line.LineChartData
import com.github.tehras.charts.line.renderer.line.SolidLineDrawer
import com.github.tehras.charts.line.renderer.point.FilledCircularPointDrawer
import com.github.tehras.charts.line.renderer.xaxis.SimpleXAxisDrawer
import com.github.tehras.charts.line.renderer.yaxis.SimpleYAxisDrawer
import com.github.tehras.charts.piechart.animation.simpleChartAnimation
import java.util.*
import kotlin.math.roundToInt

@Composable
fun StatsScreen(
    context: Context,
    navController: NavController
) {
    val TAG = "StatsScreen"
    val supportViewModel: SupportViewModel = viewModel(factory = supportViewModelFactory {
        SupportViewModel(context)
    })
    val statsViewModel: StatsViewModel = viewModel(factory = statsViewModelFactory {
        StatsViewModel(context)
    })

    LaunchedEffect(Unit) {
        // Initializes month and year to be current month and year
        supportViewModel.setCurrentYear(Calendar.getInstance().get(Calendar.YEAR))
        supportViewModel.setCurrentMonth(Calendar.getInstance().get(Calendar.MONTH) + 1)
    }

    val statistics by statsViewModel.statistics.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Top bar elements
        StatsTopBar(supportViewModel = supportViewModel, statsViewModel = statsViewModel)
        // Graph
        Graph(statistics = statistics)
        // variable, type, month, year
        Selections(
            supportViewModel = supportViewModel,
            statsViewModel = statsViewModel,
            navController = navController
        )
        // Fab
    }
}

@Composable
fun StatsTopBar(supportViewModel: SupportViewModel, statsViewModel: StatsViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.primary),
        horizontalArrangement = Arrangement.End
    ) {
        // + new year button
        AddNewYearButton(supportViewModel = supportViewModel)
        Spacer(modifier = Modifier.width(10.dp))
        AddNewVariableButton(statsViewModel = statsViewModel)
        Spacer(modifier = Modifier.width(10.dp))
    }
}

@Composable
fun AddNewVariableButton(statsViewModel: StatsViewModel) {
    // handles showing/closing add new year dialog
    var showDialog by remember {
        mutableStateOf(false)
    }

    if (showDialog) {
        AddNewVariableDialog(statsViewModel = statsViewModel, setShowDialog = {
            showDialog = it
        })
    }

    Button(
        onClick = { showDialog = true },
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
        contentPadding = PaddingValues(10.dp)
    ) {
        Text(text = "+ new lift")
    }
}

@Composable
fun AddNewVariableDialog(statsViewModel: StatsViewModel, setShowDialog: (Boolean) -> Unit) {
    val TAG = "AddNewLiftDialog"

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
                Text(text = "Add new variable")
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = textFieldState,
                    onValueChange = { textFieldState = it },
                    label = { Text(text = "Enter a new variable here") },
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(32.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    // OK button
                    Button(onClick = {
                        val input = textFieldState
                        if (input.isNotEmpty()) {
                            // calls viewModel to add new year to database and update view model
                            statsViewModel.insertVariable(input)
                            setShowDialog(false)
                        } else {
                            Log.d(TAG, "Input is empty")
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
fun Graph(statistics: List<Statistic>) {
    val TAG = "Graph"

    val data = mutableListOf<LineChartData.Point>()
    statistics.forEach { statistic ->
        data.add(
            LineChartData.Point(
                value = statistic.value.toFloat(),
                label = "${statistic.day}"
            )
        )
    }

    // If there is only one entry, app will crash, so we add an empty entry
    if (data.size == 1) {
        data.add(
            LineChartData.Point(
                value = 0.0f,
                label = "0"
            )
        )
    }

    data.sortBy { it.label.toInt() }

    Log.d(TAG, "statistics: $statistics")
    Log.d(TAG, "data: $data")

    LineChart(
        linesChartData = listOf(
            LineChartData(
                points = data,
                lineDrawer = SolidLineDrawer()
            )
        ),
        // Optional properties.
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f),
        animation = simpleChartAnimation(),
        pointDrawer = FilledCircularPointDrawer(),
        horizontalOffset = 5f,
        xAxisDrawer = SimpleXAxisDrawer(),
        yAxisDrawer = SimpleYAxisDrawer(),
    )
}

@Composable
fun Selections(
    supportViewModel: SupportViewModel,
    statsViewModel: StatsViewModel,
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        SelectVariableDropdown(
            statsViewModel = statsViewModel,
            supportViewModel = supportViewModel,
            screen = "StatsScreen"
        )
        SelectTypeDropdown(
            statsViewModel = statsViewModel,
            supportViewModel = supportViewModel,
            screen = "StatsScreen"
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        SelectMonthDropdown(
            supportViewModel = supportViewModel,
            logViewModel = null,
            statsViewModel = statsViewModel,
            screen = "StatsScreen",
            color = MaterialTheme.colors.primary
        )
        SelectYearDropdown(
            supportViewModel = supportViewModel,
            logViewModel = null,
            statsViewModel = statsViewModel,
            screen = "StatsScreen",
            color = MaterialTheme.colors.primary
        )
    }
    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center) {
        FloatingActionButton(
            modifier = Modifier.padding(top = 30.dp),
            onClick = {
                navController.navigate(WorkoutScreens.newStatisticScreen.screen_route)
            },
            contentColor = Color.White,
        ) {
            Text(text = "+", fontSize = 30.sp)
        }
    }
}

