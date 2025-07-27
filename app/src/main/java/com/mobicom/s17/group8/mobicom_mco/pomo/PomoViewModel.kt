package com.mobicom.s17.group8.mobicom_mco.pomo

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.concurrent.TimeUnit

class PomoViewModel : ViewModel() {

    enum class TimerState { FOCUS, SHORT_BREAK, LONG_BREAK }

    // --- State Management LiveData ---
    private val _timerState = MutableLiveData(TimerState.FOCUS)
    val timerState: LiveData<TimerState> = _timerState

    private val _isTimerRunning = MutableLiveData(false)
    val isTimerRunning: LiveData<Boolean> = _isTimerRunning

    private val _timeLeftInMillis = MutableLiveData<Long>()
    val timeLeftInMillis: LiveData<Long> = _timeLeftInMillis

    private val _progress = MutableLiveData(1.0f)
    val progress: LiveData<Float> = _progress

    private val _taskName = MutableLiveData<String?>("Task Name") // Start with a default
    val taskName: LiveData<String?> = _taskName



    // --- Timer Settings ---
    private var focusDuration = TimeUnit.MINUTES.toMillis(25)
    private var shortBreakDuration = TimeUnit.MINUTES.toMillis(5)
    private var longBreakDuration = TimeUnit.MINUTES.toMillis(15)
    private var totalTime: Long = focusDuration // This will be the denominator for progress
    private var sessionsUntilLongBreak = 4
    private val _currentSessionCount = MutableLiveData(1)
    val currentSessionCount: LiveData<Int> = _currentSessionCount

    private var timer: CountDownTimer? = null

    init {
        // Initialize the timer with the default focus duration
        _timeLeftInMillis.value = focusDuration
    }

    fun toggleTimer() {
        if (_isTimerRunning.value == true) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        _isTimerRunning.value = true
        // Use the current totalTime for progress calculation
        val currentTotalTime = totalTime

        timer = object : CountDownTimer(_timeLeftInMillis.value ?: currentTotalTime, 50) {
            override fun onTick(millisUntilFinished: Long) {
                _timeLeftInMillis.value = millisUntilFinished
                // --- FIX 3: CORRECTLY CALCULATE AND UPDATE PROGRESS ---
                _progress.value = millisUntilFinished.toFloat() / currentTotalTime
            }

            override fun onFinish() {
                // To prevent a flicker, manually set final state before moving on
                _timeLeftInMillis.value = 0L
                _progress.value = 0f
                _isTimerRunning.value = false
                moveToNextState()
            }
        }.start()
    }

    private fun pauseTimer() {
        timer?.cancel()
        _isTimerRunning.value = false
    }

    fun resetTimer() {
        timer?.cancel()
        _isTimerRunning.value = false
        // Reset time and progress based on the current state
        updateDurationsAndResetUI()
    }


    private fun moveToNextState() {
        val nextState = when (_timerState.value) {
            TimerState.FOCUS -> {
                // Focus session just ended
                val currentCount = _currentSessionCount.value ?: 1
                if (currentCount >= sessionsUntilLongBreak) {
                    // Time for a long break
                    _currentSessionCount.value = 1 // Reset for the next cycle
                    TimerState.LONG_BREAK
                } else {
                    // Time for a short break
                    TimerState.SHORT_BREAK
                }
            }
            TimerState.SHORT_BREAK -> {
                // A short break just ended, time to focus again
                _currentSessionCount.value = (_currentSessionCount.value ?: 0) + 1
                TimerState.FOCUS
            }
            TimerState.LONG_BREAK -> {
                // A long break just ended, time to focus again
                TimerState.FOCUS
            }
            else -> TimerState.FOCUS // Default case
        }

        _timerState.value = nextState

        updateDurationsAndResetUI()

        startTimer()
    }

    fun updateSettings(task: String, focus: Int, short: Int, long: Int, sessions: Int) {
        _taskName.value = task.ifBlank { null }
        focusDuration = TimeUnit.MINUTES.toMillis(focus.toLong())
        shortBreakDuration = TimeUnit.MINUTES.toMillis(short.toLong())
        longBreakDuration = TimeUnit.MINUTES.toMillis(long.toLong())
        sessionsUntilLongBreak = sessions // Store the new setting

        // Reset everything to apply new settings
        _currentSessionCount.value = 1
        _timerState.value = TimerState.FOCUS
        resetTimer()
    }

    private fun updateDurationsAndResetUI() {
        totalTime = when (_timerState.value) {
            TimerState.FOCUS -> focusDuration
            TimerState.SHORT_BREAK -> shortBreakDuration
            TimerState.LONG_BREAK -> longBreakDuration
            else -> focusDuration
        }
        _timeLeftInMillis.value = totalTime
        _progress.value = 1.0f
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }
}