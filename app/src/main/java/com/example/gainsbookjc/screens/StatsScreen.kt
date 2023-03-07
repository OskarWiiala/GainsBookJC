package com.example.gainsbookjc.screens

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gainsbookjc.*
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

/**
 * @author Oskar Wiiala
 * @param context
 * @param navController
 * Composable for displaying graph data based on variable, type, month and year
 * Adding a new year and variable to database is also handled here
 * Navigating to NewStatisticScreen is done by clicking on the fab
 */
@Composable
fun StatsScreen(
    context: Context,
    navController: NavController
) {
    val supportViewModel: SupportViewModel = viewModel(factory = supportViewModelFactory {
        SupportViewModel(context)
    })
    val statsViewModel: StatsViewModel = viewModel(factory = statsViewModelFactory {
        StatsViewModel(context)
    })

    // Do this only once
    LaunchedEffect(Unit) {
        // Initializes month and year to be current month and year
        supportViewModel.setCurrentYear(Calendar.getInstance().get(Calendar.YEAR))
        supportViewModel.setCurrentMonth(Calendar.getInstance().get(Calendar.MONTH) + 1)
    }

    val statistics by statsViewModel.statistics.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Top bar elements
        StatsTopBar(supportViewModel = supportViewModel, statsViewModel = statsViewModel)
        Spacer(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(2.dp)
                .background(MaterialTheme.colors.secondary)
                .align(Alignment.CenterHorizontally)
        )
        // Graph
        Graph(statistics = statistics)
        // variable, type, month, year
        Selections(
            supportViewModel = supportViewModel,
            statsViewModel = statsViewModel,
            navController = navController
        )
    }
}

/**
 * @author Oskar Wiiala
 * @param supportViewModel
 * @param statsViewModel
 * Top bar for the view
 * UI for adding a new year and variable to database
 */
@Composable
fun StatsTopBar(supportViewModel: SupportViewModel, statsViewModel: StatsViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        horizontalArrangement = Arrangement.End
    ) {
        AddNewYearButton(supportViewModel = supportViewModel)
        Spacer(modifier = Modifier.width(10.dp))
        AddNewVariableButton(statsViewModel = statsViewModel)
        Spacer(modifier = Modifier.width(10.dp))
    }
}

/**
 * @author Oskar Wiiala
 * @param statsViewModel
 * Button for adding a new variable to database
 */
@Composable
fun AddNewVariableButton(
    statsViewModel: StatsViewModel,
    color: Color = MaterialTheme.colors.primary
) {
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
        colors = ButtonDefaults.buttonColors(backgroundColor = color),
        contentPadding = PaddingValues(10.dp)
    ) {
        Text(text = "+ new lift")
    }
}

/**
 * @author Oskar Wiiala
 * @param statsViewModel
 * @param setShowDialog callback to close dialog
 * Dialog for adding a new variable to database
 * Handles call to add a new variable to database in the view model as well
 */
@Composable
fun AddNewVariableDialog(statsViewModel: StatsViewModel, setShowDialog: (Boolean) -> Unit) {
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
                            // calls viewModel to add new variable to database and update view model
                            statsViewModel.insertVariable(input)
                            setShowDialog(false)
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
 * @param statistics list of statistics
 * This graph handles displaying weight progression data for a lift
 * Is based on the variable, type, month and year
 */
@Composable
fun Graph(statistics: List<Statistic>) {
    val data = mutableListOf<LineChartData.Point>()
    // Converts data of type Statistic to type LineChartData.Point
    statistics.forEach { statistic ->
        data.add(
            LineChartData.Point(
                value = statistic.value.toFloat(),
                label = "${statistic.day}"
            )
        )
    }

    // If there is only one entry the app will crash, so we add an empty entry
    if (data.size == 1) {
        data.add(
            LineChartData.Point(
                value = 0.0f,
                label = "0"
            )
        )
    }

    // Since the label of an entry in the x-axis is the day of when the statistic was added,
    // it makes sense to sort it in an ascending order
    data.sortBy { it.label.toInt() }

    // Main content
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize(0.5f)
            .padding(16.dp)
    ) {
        LineChart(
            linesChartData = listOf(
                LineChartData(
                    points = data,
                    lineDrawer = SolidLineDrawer(color = MaterialTheme.colors.secondary)
                )
            ),
            // Optional properties.
            modifier = Modifier
                .fillMaxSize(),
            animation = simpleChartAnimation(),
            pointDrawer = FilledCircularPointDrawer(diameter = 0.dp),
            horizontalOffset = 5f,
            xAxisDrawer = SimpleXAxisDrawer(axisLineColor = MaterialTheme.colors.secondary),
            yAxisDrawer = SimpleYAxisDrawer(axisLineColor = MaterialTheme.colors.secondary),
        )
    }
}

/**
 * @author Oskar Wiiala
 * @param supportViewModel
 * @param statsViewModel
 * @param navController used with a fab to navigate to NewStatisticScreen
 * UI for displaying variable, type, month and year dropdowns as well as the fab
 */
@Composable
fun Selections(
    supportViewModel: SupportViewModel,
    statsViewModel: StatsViewModel,
    navController: NavController
) {
    // Variable and type dropdowns
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        SelectVariableDropdown(
            statsViewModel = statsViewModel,
            supportViewModel = supportViewModel,
            screen = "StatsScreen",
            modifier = Modifier.fillMaxWidth(0.5f)
        )
        SelectTypeDropdown(
            statsViewModel = statsViewModel,
            supportViewModel = supportViewModel,
            screen = "StatsScreen",
            modifier = Modifier.fillMaxWidth(0.5f)
        )
    }

    // Month and year dropdowns
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
            color = MaterialTheme.colors.primary,
            modifier = Modifier.fillMaxWidth(0.5f)
        )
        SelectYearDropdown(
            supportViewModel = supportViewModel,
            logViewModel = null,
            statsViewModel = statsViewModel,
            screen = "StatsScreen",
            color = MaterialTheme.colors.primary,
            modifier = Modifier.fillMaxWidth(0.5f)
        )
    }

    // Fab which navigates to NewStatisticScreen
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

