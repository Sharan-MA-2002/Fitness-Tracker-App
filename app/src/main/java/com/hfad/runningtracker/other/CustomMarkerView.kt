package com.hfad.runningtracker.other

import android.content.Context
import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.hfad.runningtracker.db.Run
import kotlinx.android.synthetic.main.marker_view.view.*
import kotlinx.android.synthetic.main.marker_view.view.tvAvgSpeed
import kotlinx.android.synthetic.main.marker_view.view.tvDate
import kotlinx.android.synthetic.main.marker_view.view.tvDistance
import java.text.SimpleDateFormat
import java.util.*

class CustomMarkerView(
    private val runs:List<Run>, c: Context,
    layoutId:Int
):MarkerView(c,layoutId) {

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f,-height.toFloat())
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        if(e==null){
            return
        }
        val currentRunId=e.x.toInt()
        val run=runs[currentRunId]

        val calendar= Calendar.getInstance().apply {
            timeInMillis=run.timestamp
        }
        val dateFormat= SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        tvDate.text=dateFormat.format(calendar.time)

        val avgspeed="${run.avgSpeedInKMH}km/h"
        tvAvgSpeed.text=avgspeed

        val distanceInKm="${run.distanceInMetres/1000f}"
        tvDistance.text=distanceInKm

        tvDuration.text=TrackingUtility.getFormattedStopwatchTime(run.timeInMillis)

        val caloriesBurned="${run.caloriesBurned}kcal"
        tvCaloriesBurned.text=caloriesBurned
    }
}