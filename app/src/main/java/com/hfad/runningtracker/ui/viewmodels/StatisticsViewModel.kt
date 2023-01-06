package com.hfad.runningtracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.hfad.runningtracker.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    val mainRepository: MainRepository
):ViewModel(){

    val totalTimeRun=mainRepository.getTotalTimeInMillis()
    val totalDistance=mainRepository.getTotalDistance()
    val totalCaloriesBurned=mainRepository.getTotalCaloriesBurned()
    val totalAvgSpeed=mainRepository.getTotalAverageSpeed()

    val runsSortedByDate=mainRepository.getAllRunsSortedByDate()
}