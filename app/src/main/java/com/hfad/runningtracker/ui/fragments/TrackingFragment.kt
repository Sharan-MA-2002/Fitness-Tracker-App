package com.hfad.runningtracker.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.hfad.runningtracker.R
import com.hfad.runningtracker.db.Run
import com.hfad.runningtracker.other.Constants.ACTION_PAUSE_SERVICE
import com.hfad.runningtracker.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.hfad.runningtracker.other.Constants.ACTION_STOP_SERVICE
import com.hfad.runningtracker.other.Constants.MAP_ZOOM
import com.hfad.runningtracker.other.Constants.POLYLINE_COLOR
import com.hfad.runningtracker.other.Constants.POLYLINE_WIDTH
import com.hfad.runningtracker.other.TrackingUtility
import com.hfad.runningtracker.services.Polyline
import com.hfad.runningtracker.services.TrackingService
import com.hfad.runningtracker.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_tracking.*
import java.util.*
import javax.inject.Inject
import kotlin.math.round
import kotlin.math.sign

const val CANCEL_TRACKING_DIALOG_TAG="CancelDialog"

@AndroidEntryPoint
class TrackingFragment:Fragment(R.layout.fragment_tracking) {

    private  val viewModel: MainViewModel by viewModels()

    private var isTracking=false
    private var pathPoints= mutableListOf<Polyline>()

    private var map:GoogleMap?=null

    private var curTimeInMillis=0L

    private var menu:Menu?=null

    @set:Inject
    private var weight=80f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView?.onCreate(savedInstanceState)

        btnToggleRun.setOnClickListener{
            toggleRun()
        }

        if(savedInstanceState!=null){
            val cancelTrackingDialog=parentFragmentManager.findFragmentByTag(
                CANCEL_TRACKING_DIALOG_TAG) as CancelTrackingDialog?
            cancelTrackingDialog?.setYesListener {
                stopRun()
            }
        }

        btnFinishRun.setOnClickListener{
            zoomToseewholeTrack()
            endRunandsavetoDb()
        }

        mapView.getMapAsync{
            map=it
            addAllPolylines()
        }

        subscribeToObservers()
    }

    private fun subscribeToObservers(){
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })
        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints=it
            addLatestPolyline()
            moveCameraToUser()
        })

        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            curTimeInMillis=it
            val formattedTime=TrackingUtility.getFormattedStopwatchTime(curTimeInMillis,false)
            tvTimer.text=formattedTime
        })
    }

    private fun toggleRun(){
        if(isTracking){
            menu?.getItem(0)?.isVisible=true
            sendCommandToService(ACTION_PAUSE_SERVICE)
        }else{
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_tracking_menu,menu)
        this.menu=menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if(curTimeInMillis>0L){
            this.menu?.getItem(0)?.isVisible=true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.cancelTracking->{
                showCancelTrackingDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showCancelTrackingDialog(){
        CancelTrackingDialog().apply {
            setYesListener {
                stopRun()
            }
        }.show(parentFragmentManager,CANCEL_TRACKING_DIALOG_TAG)
    }

    private fun stopRun(){
        tvTimer.text="00:00:00:00"
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }


    private fun updateTracking(isTracking:Boolean){
        this.isTracking=isTracking
        if(!isTracking && curTimeInMillis > 0L){
            btnToggleRun.text="Start"
            btnFinishRun.visibility=View.VISIBLE
        }else if(isTracking){
            btnToggleRun.text="Stop"
            menu?.getItem(0)?.isVisible=true
            btnFinishRun.visibility=View.GONE
        }
    }


    private fun moveCameraToUser(){
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty())
    map?.animateCamera(
        CameraUpdateFactory.newLatLngZoom(
            pathPoints.last().last(),
            MAP_ZOOM
        )
    )
    }

    private fun addAllPolylines(){
        for(polyline in pathPoints){
            val polylineOptions=PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline(){
        if(pathPoints.isNotEmpty() && pathPoints.last().size>1){
            val preLastLangLng=pathPoints.last()[pathPoints.last().size-2]
        val lastLatLng=pathPoints.last().last()
            val polylineOptions=PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLangLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun sendCommandToService(action:String)=
        Intent(requireContext(), TrackingService::class.java).also{
            it.action=action
            requireContext().startService(it)
        }

    private fun zoomToseewholeTrack(){
        val bounds=LatLngBounds.Builder()
        for(polyline in pathPoints){
            for(pos in polyline){
                bounds.include(pos)
            }
        }
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                mapView.width,
                mapView.height,
                (mapView.height * 0.05f).toInt()
            )
        )
    }

    private fun endRunandsavetoDb(){
        map?.snapshot {
            bmp ->
            var distanceInMetres=0
            for(polyline in pathPoints){
                distanceInMetres+=TrackingUtility.calculatePolylineLength(polyline).toInt()
            }
            val avgSpeed= round((distanceInMetres/1000f) /(curTimeInMillis/1000f/60/60)*10)/10f
            val dateTimestamp=Calendar.getInstance().timeInMillis
            val caloriesBurned=((distanceInMetres/1000f) * weight).toInt()
            val run=Run(bmp,dateTimestamp,avgSpeed,distanceInMetres,curTimeInMillis,caloriesBurned)
            viewModel.insertRun(run)
            Snackbar.make(requireActivity().findViewById(R.id.rootView),"Run saved successfully",
                Snackbar.LENGTH_LONG).show()
            stopRun()
        }
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

}