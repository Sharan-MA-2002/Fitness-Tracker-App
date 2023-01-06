package com.hfad.runningtracker.ui.fragments

import android.app.Dialog
import android.location.GnssAntennaInfo
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hfad.runningtracker.R

class CancelTrackingDialog:DialogFragment() {

    private  var yesListener:(()->Unit)?=null

    fun setYesListener(listener:()->Unit){
        yesListener=listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(),R.style.AlertDialogTheme)
            .setTitle("Cancel the Run?")
            .setMessage("Are you sure you want to cancel the run and delete all its data?")
            .setIcon(R.drawable.ic_baseline_delete_24)
            .setPositiveButton("Yes"){ _, _ ->
                yesListener?.let {
                    yes->
                    yes()
                }
            }
            .setNegativeButton("No"){ dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create()

    }
}