package com.hfad.runningtracker.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RunDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: Run)

    @Delete
    suspend fun deleteRun(run: Run)

    @Query("SELECT * FROM `running_table)` ORDER BY timestamp DESC")
    fun getAllRunsSortedByDate():LiveData<List<Run>>

    @Query("SELECT * FROM `running_table)` ORDER BY timeInMillis DESC")
    fun getAllRunsSortedBytIMEinMillis():LiveData<List<Run>>

    @Query("SELECT * FROM `running_table)` ORDER BY caloriesBurned DESC")
    fun getAllRunsSortedByCaloriesBurned():LiveData<List<Run>>

    @Query("SELECT * FROM `running_table)` ORDER BY avgSpeedInKMH DESC")
    fun getAllRunsSortedByAverageSpeed():LiveData<List<Run>>

    @Query("SELECT * FROM `running_table)` ORDER BY distanceInMetres DESC")
    fun getAllRunsSortedByDistance():LiveData<List<Run>>

    @Query("SELECT SUM(timeInMillis) from `running_table)`")
    fun getTotalTimeInMillis():LiveData<Long>

    @Query("SELECT SUM(caloriesBurned) from `running_table)`")
    fun getTotalCaloriesBurned():LiveData<Int>

    @Query("SELECT SUM(distanceInMetres) from `running_table)`")
    fun getTotalDistanceInMetres():LiveData<Int>

    @Query("SELECT AVG(avgSpeedInKMH) from `running_table)`")
    fun getAverageSpeed():LiveData<Float>
}