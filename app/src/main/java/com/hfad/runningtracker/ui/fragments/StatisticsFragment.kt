package com.hfad.runningtracker.ui.fragments

import android.content.SharedPreferences
import android.graphics.Color
import android.icu.text.Transliterator.Position
import android.os.Bundle
import android.util.Size
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.material.snackbar.Snackbar
import com.hfad.runningtracker.R
import com.hfad.runningtracker.other.Constants.KEY_NAME
import com.hfad.runningtracker.other.Constants.KEY_WEIGHT
import com.hfad.runningtracker.other.CustomMarkerView
import com.hfad.runningtracker.other.TrackingUtility
import com.hfad.runningtracker.ui.viewmodels.MainViewModel
import com.hfad.runningtracker.ui.viewmodels.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_setup.*
import kotlinx.android.synthetic.main.fragment_setup.etName
import kotlinx.android.synthetic.main.fragment_setup.etWeight
import kotlinx.android.synthetic.main.fragment_statistics.*
import java.lang.Float
import java.lang.Math.round
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class StatisticsFragment:Fragment(R.layout.fragment_statistics) {

    private  val viewModel: StatisticsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observers()
        val anim1=AnimationUtils.loadAnimation(requireContext(),R.anim.anim_1)
        val anim2=AnimationUtils.loadAnimation(requireContext(),R.anim.anim_2)
        val anim3=AnimationUtils.loadAnimation(requireContext(),R.anim.anim_bounce)
        val anim4=AnimationUtils.loadAnimation(requireContext(),R.anim.anim_fade)
        val anim5=AnimationUtils.loadAnimation(requireContext(),R.anim.anim_slide)
        cardview1.startAnimation(anim5)
        cardview2.startAnimation(anim5)
        cardview3.startAnimation(anim5)
        cardview4.startAnimation(anim5)
        setupBarchart()
    }

    private fun setupBarchart(){
        barChart.xAxis.apply {
            position=XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor= Color.LTGRAY
            textColor=Color.GRAY
            setDrawGridLines(false)
        }
        barChart.axisLeft.apply {
            axisLineColor=Color.LTGRAY
            textColor=Color.GRAY
            setDrawGridLines(false)
        }
        barChart.axisRight.apply {
            axisLineColor = Color.LTGRAY
            textColor = Color.GRAY
            setDrawGridLines(false)
        }
        barChart.apply {
            description.text="Avg Speed over Time"
            description.setPosition(450f,20f)
            description.textSize=Float.valueOf(10f)
            description.textColor=Color.DKGRAY
            legend.isEnabled=false
        }
    }

    private fun observers(){
        viewModel.totalTimeRun.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalTimeRun=TrackingUtility.getFormattedStopwatchTime(it)
                tvTotalTime.text=totalTimeRun
            }
        })
        viewModel.totalDistance.observe(viewLifecycleOwner, Observer {
            it?.let {
                val km= round(it * 1000.0)/1000.0
                val totalDistance2= round(km * 100.0) /100.0
                val totalDistance= round(totalDistance2 * 10.0) / 10.0
                val totalDistanceString="$totalDistance km"
                tvTotalDistance.text=totalDistanceString
                /*val km = it / 1000f
                val totalDistance = round(km * 10) / 10f
                val totalDistanceString = "${totalDistance} km"
                tvTotalDistance.text = totalDistanceString*/
            }
        })

        viewModel.totalAvgSpeed.observe(viewLifecycleOwner, Observer {
            it?.let {
                val avgSpeed= round(it * 1000.0)/1000.0
                val avgSpeed2= round(avgSpeed * 100.0)/100.0
                val avgSpeed3= round(avgSpeed2 * 10.0)/10.0
                val avgSpeedString="$avgSpeed3 km/h"
                tvAverageSpeed.text=avgSpeedString
               /* val roundedAvgSpeed = round(it * 10f) / 10f
                val totalAvgSpeed = "${roundedAvgSpeed} km/h"
                tvAverageSpeed.text = totalAvgSpeed*/
            }
        })

        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner, Observer {
            it?.let {
                val calorie1= round(it * 1000.0)/1000.0
                val calorie2= round(calorie1 * 100.0)/100.0
                val calorie3= round(calorie2 * 10.0)/10.0
                val totalCalorie="$calorie3 kcal"
                tvTotalCalories.text=totalCalorie
                //val totalCaloriesBurned = "${it} kcal"
                //tvTotalCalories.text = totalCaloriesBurned
            }
        })
        viewModel.runsSortedByDate.observe(viewLifecycleOwner, Observer {
            it?.let {
                val allAvgSpeeds=it.indices.map { i->BarEntry(i.toFloat(),it[i].avgSpeedInKMH) }
                val barDataSet=BarDataSet(allAvgSpeeds,"Avg Speed over Time").apply {
                    valueTextColor=Color.BLACK
                    color=ContextCompat.getColor(requireContext(),R.color.purp)
                }
                barChart.data= BarData(barDataSet)
                barChart.marker=CustomMarkerView(it.reversed(),requireContext(),R.layout.marker_view)
                barChart.invalidate()
            }
        })
    }


}