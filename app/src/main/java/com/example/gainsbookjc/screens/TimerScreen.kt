package com.example.gainsbookjc.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.gainsbookjc.CustomTimeType
import com.example.gainsbookjc.R
import com.example.gainsbookjc.viewmodels.TimerViewModel
import kotlinx.coroutines.delay
import java.lang.Math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * This screen acts as the timer function of the app and it displays two buttons, either start count up or count down.
 * If count down is clicked, a dialog pops up where the user can select times from 1 minute to 15 minutes.
 * Clicking OK displays the following elements: time left arch and timer, buttons to to stop, pause and restart.
 * If user clicks count up button, the same elements pop up as count down, but without a time left arc.
 * Instead of counting down, the timer counts up.
 * Time is displayed as minutes and seconds.
 * @author Oskar Wiiala
 */
@Composable
fun TimerScreen() {
    val timerViewModel = TimerViewModel()
    val totalTime by timerViewModel.totalTime.collectAsState()
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CountUpTimer(timerViewModel = timerViewModel)
            CountDownTimer(
                timerViewModel = timerViewModel,
                totalTime = totalTime * 1000L,
                modifier = Modifier.size(200.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
            ) {
                StartCountDownButton(
                    timerViewModel = timerViewModel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 5.dp)
                )
                StartCountUpButton(
                    timerViewModel = timerViewModel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                )
            }
        }
    }
}

/**
 * Button used in TimerScreen to open up SelectTimeDialog composable.
 * @author Oskar Wiiala
 * @param timerViewModel
 * @param modifier
 */
@Composable
fun StartCountDownButton(timerViewModel: TimerViewModel, modifier: Modifier = Modifier) {
    val isVisible by timerViewModel.isButtonCountDownVisible.collectAsState()
    if (isVisible) {
        // handles showing/closing add new year dialog
        var showDialog by remember {
            mutableStateOf(false)
        }

        if (showDialog) {
            SelectTimeDialog(timerViewModel = timerViewModel, setShowDialog = {
                showDialog = it
            })
        }

        Button(
            modifier = modifier,
            onClick = { showDialog = true },
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
            contentPadding = PaddingValues(10.dp)
        ) {
            Text(text = "START COUNTDOWN", fontSize = 20.sp)
        }
    }
}

/**
 * Dialog, which is used in TimerScreen. It is activated by clicking StartCountDownButton composable.
 * The user can select a time between 1 minute and 15 minutes from a dropdown.
 * Clicking OK displays a count down timer with stop, pause and restart functionalities.
 * @author Oskar Wiiala
 * @param timerViewModel
 * @param setShowDialog callback to close dialog
 */
@Composable
fun SelectTimeDialog(timerViewModel: TimerViewModel, setShowDialog: (Boolean) -> Unit) {
// Main content
    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            border = BorderStroke(2.dp, MaterialTheme.colors.primary)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Select time")
                Spacer(modifier = Modifier.height(16.dp))
                SelectTimeDropDown(
                    timerViewModel = timerViewModel,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(32.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    // OK button
                    Button(onClick = {
                        val timeType = timerViewModel.customTimeType.value
                        // setup timer
                        timerViewModel.setTotalTime(timeType.value)
                        Log.d("ok button", "timeType: ${timeType.value}")
                        setShowDialog(false)
                        timerViewModel.setVisibility(element = "CountDown", value = true)
                        timerViewModel.setVisibility(element = "ButtonCountDown", value = false)
                        timerViewModel.setVisibility(element = "ButtonCountUp", value = false)
                    }) {
                        Text(text = "OK")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = { setShowDialog(false) }) {
                        Text(text = "CANCEL")
                    }
                }
            }
        }
    }
}

/**
 * Dropdown used in TimerScreen inside SelectTimeDialog composable.
 * Allows the user to select a time between 1 minute and 15 minutes.
 * @author Oskar Wiiala
 * @param timerViewModel
 * @param modifier
 */
@Composable
fun SelectTimeDropDown(timerViewModel: TimerViewModel, modifier: Modifier = Modifier) {
    val customTimeTypes = listOf(
        CustomTimeType(type = "1 min", value = 60L),
        CustomTimeType(type = "2 min", value = 120L),
        CustomTimeType(type = "3 min", value = 180L),
        CustomTimeType(type = "4 min", value = 240L),
        CustomTimeType(type = "5 min", value = 300L),
        CustomTimeType(type = "10 min", value = 600L),
        CustomTimeType(type = "15 min", value = 900L),
    )

    // Get timeType from timerViewModel as state
    val customTimeType by timerViewModel.customTimeType.collectAsState()

    // Handles opening/closing dropdown menu
    var expanded by remember {
        mutableStateOf(false)
    }

    // Select time type dropdown
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Button(
            onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
        ) {
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.down_icon_24),
                    contentDescription = "Dropdown"
                )
                Text(text = customTimeType.type)
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
            customTimeTypes.forEachIndexed { _, itemValue ->
                DropdownMenuItem(
                    onClick = {
                        timerViewModel.setCustomTimeType(itemValue)
                        expanded = false

                    },
                    enabled = (itemValue != timerViewModel.customTimeType.value)
                ) {
                    Text(text = itemValue.type)
                }
            }
        }
    }
}

/**
 * Button used in TimerScreen to display a count-up timer.
 * @author Oskar Wiiala
 * @param timerViewModel
 * @param modifier
 */
@Composable
fun StartCountUpButton(timerViewModel: TimerViewModel, modifier: Modifier = Modifier) {
    val isVisible by timerViewModel.isButtonCountUpVisible.collectAsState()
    if (isVisible) {
        Button(
            modifier = modifier,
            onClick = {
                timerViewModel.setTotalTime(0L)
                timerViewModel.setVisibility(element = "ButtonCountDown", value = false)
                timerViewModel.setVisibility(element = "ButtonCountUp", value = false)
                timerViewModel.setVisibility(element = "CountUp", value = true)
            },
            contentPadding = PaddingValues(10.dp)
        ) {
            Text(text = "START STOPWATCH", fontSize = 20.sp)
        }
    }
}

/**
 * Displays a count-up timer.
 * Has buttons to stop, pause and restart timer.
 * @author Oskar Wiiala
 * @param timerViewModel
 */
@Composable
fun CountUpTimer(timerViewModel: TimerViewModel) {
    val isVisible by timerViewModel.isCountUpVisible.collectAsState()

    if (isVisible) {
        var currentTime by remember {
            mutableStateOf(0L)
        }

        var isTimerRunning by remember {
            mutableStateOf(true)
        }

        // Whenever key changes, rerun this code
        LaunchedEffect(key1 = currentTime, key2 = isTimerRunning) {
            if (isTimerRunning) {
                delay(100L)
                // Played around with this, 113 seems to be accurate
                currentTime += 113L
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${((currentTime / 1000L) / 60)} m ${(currentTime / 1000L) % 60} s",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.425f)
                    .padding(top = 20.dp)
            ) {
                // Toggles the timer
                Button(
                    onClick = {
                        timerViewModel.setVisibility(element = "CountUp", value = false)
                        timerViewModel.setVisibility(element = "ButtonCountDown", value = true)
                        timerViewModel.setVisibility(element = "ButtonCountUp", value = true)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 5.dp)
                ) {
                    Text(text = "STOP", fontSize = 20.sp)
                }
                Button(
                    onClick = { isTimerRunning = !isTimerRunning },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 5.dp)
                ) {
                    Text(text = if (isTimerRunning) "PAUSE" else "RESUME", fontSize = 20.sp)
                }
                Button(
                    onClick = {
                        currentTime = 0L
                        isTimerRunning = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "RESTART", fontSize = 20.sp)
                }
            }
        }
    }
}

/**
 * Displays a countdown timer with an arc to visualize countdown.
 * @author Oskar Wiiala
 * @param modifier
 * @param timerViewModel
 * @param totalTime how much time was originally selected for the countdown
 * @param pointColor The color of the point in the timer arc
 * @param inactiveBarColor color of the inactive bar
 * @param activeBarColor color of the active bar
 * @param initialValue value of how much time has passed as a percentage. Should always be 1f, unless you want to start the timer arc from less than 100 % filled.
 * @param strokeWidth width of the line of the arc
 */
@Composable
fun CountDownTimer(
    modifier: Modifier = Modifier,
    timerViewModel: TimerViewModel,
    totalTime: Long,
    pointColor: Color = MaterialTheme.colors.secondary,
    inactiveBarColor: Color = Color.DarkGray,
    activeBarColor: Color = MaterialTheme.colors.primary,
    initialValue: Float = 1f,
    strokeWidth: Dp = 5.dp
) {
    val isVisible by timerViewModel.isCountDownVisible.collectAsState()
    if (isVisible) {
        var size by remember {
            // IntSize.Zero allows to scale the size
            mutableStateOf(IntSize.Zero)
        }

        // how much time is left compared to max as percentage
        var valuePercent by remember {
            mutableStateOf(initialValue)
        }

        var currentTime by remember {
            mutableStateOf(totalTime)
        }

        var isTimerRunning by remember {
            mutableStateOf(true)
        }

        // Whenever key changes, rerun this code
        LaunchedEffect(key1 = totalTime) {
            currentTime = totalTime
        }

        // Whenever key changes, rerun this code
        LaunchedEffect(key1 = currentTime, key2 = isTimerRunning) {
            if (currentTime > 0 && isTimerRunning) {
                delay(100L)
                // Played around with this, 113 seems to be accurate
                currentTime -= 113L
                valuePercent = currentTime / totalTime.toFloat()
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier.onSizeChanged { size = it }) {
                Canvas(modifier = modifier) {
                    // draws the inactive arc under the active arc
                    drawArc(
                        color = inactiveBarColor,
                        startAngle = -215f,
                        sweepAngle = 250f,
                        // Prevents the ends of the arc to be connected to center
                        useCenter = false,
                        size = Size(size.width.toFloat(), size.height.toFloat()),
                        style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
                    )

                    // draws the active arc over the inactive arc
                    drawArc(
                        color = activeBarColor,
                        startAngle = -215f,
                        sweepAngle = 250f * valuePercent,
                        // Prevents the ends of the arc to be connected to center
                        useCenter = false,
                        size = Size(size.width.toFloat(), size.height.toFloat()),
                        style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
                    )

                    // uses the radius and x and y axis to determine where the point is
                    val center = Offset(size.width / 2f, size.height / 2f)
                    // beta is the angle and 145 seems to fit nicely
                    val beta = (250f * valuePercent + 145f) * (PI / 180f).toFloat()
                    val radius = size.width / 2f
                    val sideA = cos(beta) * radius
                    val sideB = sin(beta) * radius

                    drawPoints(
                        listOf(
                            Offset(
                                // center.x is the center of the arc
                                // sideA is the difference between the x of the point and center.x
                                x = center.x + sideA,
                                // The difference between x of sideA and y of sideB  I guess
                                y = center.y + sideB
                            )
                        ),
                        pointMode = PointMode.Points,
                        color = pointColor,
                        strokeWidth = (strokeWidth * 3f).toPx(),
                        cap = StrokeCap.Round
                    )
                }

                Text(
                    // currentTime is in ms, so we want to convert it to seconds
                    text = "${((currentTime / 1000L) / 60)} m ${(currentTime / 1000L) % 60} s",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Column(modifier = Modifier.fillMaxWidth(0.425f)) {
                // Toggles the timer
                Button(
                    onClick = {
                        timerViewModel.setVisibility(element = "CountDown", value = false)
                        timerViewModel.setVisibility(element = "ButtonCountDown", value = true)
                        timerViewModel.setVisibility(element = "ButtonCountUp", value = true)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 5.dp)
                ) {
                    Text(text = "STOP", fontSize = 20.sp)
                }
                Button(
                    onClick = { isTimerRunning = !isTimerRunning },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 5.dp)
                ) {
                    Text(text = if (isTimerRunning) "PAUSE" else "RESUME", fontSize = 20.sp)
                }
                Button(
                    onClick = {
                        currentTime = totalTime
                        isTimerRunning = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "RESTART", fontSize = 20.sp)
                }
            }
        }
    }
}