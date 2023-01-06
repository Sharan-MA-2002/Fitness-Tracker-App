package com.hfad.runningtracker.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hfad.runningtracker.Adapters.RunAdapter
import com.hfad.runningtracker.R
import com.hfad.runningtracker.SearchActivity
import com.hfad.runningtracker.db.Run
import com.hfad.runningtracker.other.Constants.REQUEST_CODE_LOCATION_PERMISSIONS
import com.hfad.runningtracker.other.SortType
import com.hfad.runningtracker.other.TrackingUtility
import com.hfad.runningtracker.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_run.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class RunFragment:Fragment(R.layout.fragment_run),EasyPermissions.PermissionCallbacks {

    private  val viewModel:MainViewModel by viewModels()
    private lateinit var runAdapter:RunAdapter
    private var searchView: SearchView? =null
    //private lateinit var list:List<Run>
    //private lateinit var tempList:List<Run>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestPermissions()
        setupRecyclerView()

        //tempList= arrayListOf<Run>()
       // list= arrayListOf<Run>()




        searchView?.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //(tempList as ArrayList<Run>).clear()
                //filterList(newText)
                val search=SearchActivity()
                search.filterList(newText)
                return true
            }

        })

        when(viewModel.sortType){
            SortType.DATE->spFilter.setSelection(0)
            SortType.RUNNING_TIME->spFilter.setSelection(1)
            SortType.DISTANCE->spFilter.setSelection(2)
            SortType.AVG_SPEED->spFilter.setSelection(3)
            SortType.CALORIES_BURNED->spFilter.setSelection(4)
        }
        spFilter.onItemSelectedListener=object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0-> viewModel.sortRuns(SortType.DATE)
                    1-> viewModel.sortRuns(SortType.RUNNING_TIME)
                    2-> viewModel.sortRuns(SortType.DISTANCE)
                    3-> viewModel.sortRuns(SortType.AVG_SPEED)
                    4-> viewModel.sortRuns(SortType.CALORIES_BURNED)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        viewModel.runs.observe(viewLifecycleOwner, Observer {
            runAdapter.submitList(it)
        })

        fab.setOnClickListener {
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }
    }

    /*private fun filterList(text: String?) {

       list.forEach {
           if (text != null) {
               it.timestamp.toString().contains(text.toLowerCase(Locale.getDefault()))
               tempList.a
           }
       }

        if (tempList.isEmpty()) {
            Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show()
        } else {
            runAdapter.setFilteredList(tempList)
        }
    }*/


    private fun setupRecyclerView()=rvRuns.apply{
        runAdapter= RunAdapter()
        adapter=runAdapter
        layoutManager=LinearLayoutManager(requireContext())
    }

    private fun requestPermissions(){
        if(TrackingUtility.hasLocationPermissions(requireContext())){
            return
        }
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.Q){
            EasyPermissions.requestPermissions(
                this,"You need to accept permissions to use this app.",
                REQUEST_CODE_LOCATION_PERMISSIONS,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
        else{
            EasyPermissions.requestPermissions(
                this,"You need to accept permissions to use this app.",
                REQUEST_CODE_LOCATION_PERMISSIONS,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            AppSettingsDialog.Builder(this).build().show()
        }
        else{
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }
}

