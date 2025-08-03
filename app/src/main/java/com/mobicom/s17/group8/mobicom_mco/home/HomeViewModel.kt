package com.mobicom.s17.group8.mobicom_mco.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.mobicom.s17.group8.mobicom_mco.database.tasks.Task
import com.mobicom.s17.group8.mobicom_mco.database.tasks.TaskRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HomeViewModel(private val repository: TaskRepository, userId: String) : ViewModel() {

    val upcomingTasks = getUpcomingTasks(userId).asLiveData()

    private fun getUpcomingTasks(userId: String): Flow<List<Task>> {
        val formatter = DateTimeFormatter.ISO_INSTANT

        val today = LocalDate.now()
        val startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant()

        val sevenDaysFromNow = today.plusDays(7)
        val endOfDaySevenDaysFromNow = sevenDaysFromNow.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant()

        val startDateString = formatter.format(startOfDay)
        val endDateString = formatter.format(endOfDaySevenDaysFromNow)

        return repository.getUpcomingTasks(userId, startDateString, endDateString)
    }
}

class HomeViewModelFactory(private val repository: TaskRepository, private val userId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}