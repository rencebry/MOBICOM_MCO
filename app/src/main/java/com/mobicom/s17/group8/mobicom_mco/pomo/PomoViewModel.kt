package com.mobicom.s17.group8.mobicom_mco.pomo

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PomoViewModel : ViewModel() {

    enum class TimerState { FOCUS, SHORT_BREAK, LONG_BREAK }

    private var timer: CountDownTimer? = null

    private val _isTimerRunning = MutableLiveData<Boolean>(false)
    val isTimerRunning: LiveData<Boolean> = _isTimerRunning

    private val _timeLeftInMillis = MutableLiveData<Long>()
    val timeLeftInMillis: LiveData<Long> = _timeLeftInMillis

    private val _timerState = MutableLiveData(TimerState.FOCUS)
    val timerState: LiveData<TimerState> = _timerState

    private var totalTime: Long = 25 * 60 * 1000L

    init {
        _timeLeftInMillis.value = totalTime
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
        timer = object : CountDownTimer(_timeLeftInMillis.value ?: totalTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _timeLeftInMillis.value = millisUntilFinished
            }

            override fun onFinish() {
                _isTimerRunning.value = false
                // TODO: switch to next sesh (break and rest)
            }
        }.start()
    }

    private fun pauseTimer() {
        timer?.cancel()
        _isTimerRunning.value = false
    }

    fun resetTimer() {
        timer?.cancel()
        _timeLeftInMillis.value = totalTime // reset to full duration
        _isTimerRunning.value = false
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }
}