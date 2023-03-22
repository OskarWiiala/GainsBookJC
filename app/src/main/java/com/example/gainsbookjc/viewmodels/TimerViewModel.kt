package com.example.gainsbookjc.viewmodels

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gainsbookjc.CustomTimeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.security.InvalidParameterException

class TimerViewModel : ViewModel() {
    private val _customTimeType =
        MutableStateFlow(CustomTimeType(type = "1 min", value = 60L))
    val customTimeType: StateFlow<CustomTimeType> get() = _customTimeType

    private val _isCountDownVisible = MutableStateFlow(false)
    val isCountDownVisible: StateFlow<Boolean> get() = _isCountDownVisible

    private val _isCountUpVisible = MutableStateFlow(false)
    val isCountUpVisible: StateFlow<Boolean> get() = _isCountUpVisible

    private val _isButtonCountDownVisible = MutableStateFlow(true)
    val isButtonCountDownVisible: StateFlow<Boolean> get() = _isButtonCountDownVisible

    private val _isButtonCountUpVisible = MutableStateFlow(true)
    val isButtonCountUpVisible: StateFlow<Boolean> get() = _isButtonCountUpVisible

    lateinit var timer: CountDownTimer

    private val _startTime = MutableStateFlow(customTimeType.value.value)
    val startTime: StateFlow<Long> get() = _startTime

    private val _secondsRemaining = MutableStateFlow(startTime.value)
    val secondsRemaining: StateFlow<Long> get() = _secondsRemaining

    private val _progressBarValue = MutableStateFlow(1.0f)
    val progressBarValue: StateFlow<Float> get() = _progressBarValue

    private val _countUpSeconds = MutableStateFlow(0L)
    val countUpSeconds: StateFlow<Long> get() = _countUpSeconds

    private val _isCountUpRunning = MutableStateFlow(false)
    val isCountUpRunning: StateFlow<Boolean> get() = _isCountUpRunning

    fun setVisibility(element: String, value: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            when (element) {
                "CountDown" -> _isCountDownVisible.emit(value)
                "CountUp" -> _isCountUpVisible.emit(value)
                "ButtonCountDown" -> _isButtonCountDownVisible.emit(value)
                "ButtonCountUp" -> _isButtonCountUpVisible.emit(value)
                else -> throw InvalidParameterException()
            }
        }
    }

    fun startCountDownTimer(time: Long = 60) {
        viewModelScope.launch {
            // Create a CountDownTimer object with start value and countdown interval every second
            timer = object : CountDownTimer(time * 1000, 1000) {
                override fun onFinish() {
                    timer.cancel()
                    setSecondsRemaining(seconds = 0L)
                }

                override fun onTick(p0: Long) {
                    viewModelScope.launch {
                        // Convert tick value from milliseconds to seconds
                        val tickValueConverted = p0 / 1000.0
                        // Round tickValueConverted up
                        val roundedUp =
                            tickValueConverted.toBigDecimal().setScale(0, RoundingMode.UP).toLong()
                        setSecondsRemaining(roundedUp)
                        // percentage of how much time is left
                        val newValue =
                            (((p0 / 1000.0).toFloat() / customTimeType.value.value))
                        // Set new value for progress bar
                        viewModelScope.launch {
                            _progressBarValue.emit(newValue)
                        }
                    }
                }
            }.start()
        }
    }

    fun startCountUpTimer() {
        viewModelScope.launch {
            setIsCountUpRunning(true)
            while (isCountUpRunning.value) {
                delay(1000L)
                viewModelScope.launch {
                    if (isCountUpRunning.value) {
                        val newValue = countUpSeconds.value + 1L
                        _countUpSeconds.emit(newValue)
                    }
                }
            }
        }
    }

    suspend fun resetCountUpTimer() {
        _countUpSeconds.emit(0L)
    }

    suspend fun setIsCountUpRunning(value: Boolean) {
        _isCountUpRunning.emit(value)
    }

    fun setCustomTimeType(customTimeType: CustomTimeType) {
        viewModelScope.launch(Dispatchers.IO) {
            _customTimeType.emit(customTimeType)
        }
    }

    fun setStartTime(seconds: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _startTime.emit(seconds)
        }
    }

    fun setSecondsRemaining(seconds: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _secondsRemaining.emit(seconds)
        }
    }
}