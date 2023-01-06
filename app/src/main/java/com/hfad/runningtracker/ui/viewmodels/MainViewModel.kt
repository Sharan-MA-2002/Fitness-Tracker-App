package com.hfad.runningtracker.ui.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.runningtracker.db.Run
import com.hfad.runningtracker.other.SortType
import com.hfad.runningtracker.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val mainRepository: MainRepository
):ViewModel(){

    private val runSortedByDate=mainRepository.getAllRunsSortedByDate()
    private val runsSortedByCaloriesBurned=mainRepository.getAllRunsSortedByCaloriesBurned()
    private val runsSortedbydistance=mainRepository.getAllRunsSortedByDistance()
    private val runsSortedByTimeinMillis=mainRepository.getAllRunsSortedByTimeInMillis()
    private val runsSortedByAverageSpeed=mainRepository.getAllRunsSortedByAverageSpeed()


    val runs=MediatorLiveData<List<Run>>()

    var sortType=SortType.DATE

    init {
        runs.addSource(runSortedByDate){
            result->
            if(sortType==SortType.DATE){
               result?.let {
                   runs.value=it
               }
            }
        }

        runs.addSource(runsSortedByCaloriesBurned){
                result->
            if(sortType==SortType.CALORIES_BURNED){
                result?.let {
                    runs.value=it
                }
            }
        }

        runs.addSource(runsSortedByAverageSpeed){
                result->
            if(sortType==SortType.AVG_SPEED){
                result?.let {
                    runs.value=it
                }
            }
        }

        runs.addSource(runsSortedbydistance){
                result->
            if(sortType==SortType.DISTANCE){
                result?.let {
                    runs.value=it
                }
            }
        }

        runs.addSource(runsSortedByTimeinMillis){
                result->
            if(sortType==SortType.RUNNING_TIME){
                result?.let {
                    runs.value=it
                }
            }
        }

    }

    fun sortRuns(sortType: SortType)=when(sortType){
        SortType.DATE->runSortedByDate.value?.let{
            runs.value=it
        }
        SortType.RUNNING_TIME->runsSortedByTimeinMillis.value?.let{
            runs.value=it
        }
        SortType.AVG_SPEED->runsSortedByAverageSpeed.value?.let{
            runs.value=it
        }
        SortType.DISTANCE->runsSortedbydistance.value?.let{
            runs.value=it
        }
        SortType.CALORIES_BURNED->runsSortedByCaloriesBurned.value?.let{
            runs.value=it
        }.also {
            this.sortType=sortType
        }
    }
    fun insertRun(run: Run)=viewModelScope.launch{
        mainRepository.insertRun(run)
    }
}