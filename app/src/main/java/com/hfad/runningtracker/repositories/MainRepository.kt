package com.hfad.runningtracker.repositories

import com.hfad.runningtracker.db.Run
import com.hfad.runningtracker.db.RunDAO
import javax.inject.Inject

class MainRepository @Inject constructor(
    val runDao:RunDAO
){
    suspend fun insertRun(run:Run)=runDao.insertRun(run)

    suspend fun deleteRun(run:Run)=runDao.deleteRun(run)

    fun getAllRunsSortedByDate()=runDao.getAllRunsSortedByDate()

    fun getAllRunsSortedByDistance()=runDao.getAllRunsSortedByDistance()

    fun getAllRunsSortedByTimeInMillis()=runDao.getAllRunsSortedBytIMEinMillis()

    fun getAllRunsSortedByAverageSpeed()=runDao.getAllRunsSortedByAverageSpeed()

    fun getAllRunsSortedByCaloriesBurned()=runDao.getAllRunsSortedByCaloriesBurned()

    fun getTotalTimeInMillis()=runDao.getTotalTimeInMillis()

    fun getTotalDistance()=runDao.getTotalDistanceInMetres()

    fun getTotalCaloriesBurned()=runDao.getTotalCaloriesBurned()

    fun getTotalAverageSpeed()=runDao.getAverageSpeed()

}