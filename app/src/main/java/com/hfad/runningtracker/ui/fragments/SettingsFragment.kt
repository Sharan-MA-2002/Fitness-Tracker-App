package com.hfad.runningtracker.ui.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.hfad.runningtracker.R
import com.hfad.runningtracker.other.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.etName
import kotlinx.android.synthetic.main.fragment_settings.etWeight
import kotlinx.android.synthetic.main.fragment_setup.*
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment:Fragment(R.layout.fragment_settings) {


    @Inject
    lateinit var sharedPreferences: SharedPreferences


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFieldsFromSharedPreferences()
        btnApplyChanges.setOnClickListener {
            val success=applyChangesToSharedPreferences()
            if(success){
                Snackbar.make(view,"Saved changes", Snackbar.LENGTH_LONG).show()
            }else{
                Snackbar.make(view,"Please fill out all the fields", Snackbar.LENGTH_LONG).show()
            }
        }


        signOutBtn.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val i = Intent(context, SetupFragment::class.java)
            startActivity(i)
        }
    }

    private fun loadFieldsFromSharedPreferences(){
        val name=sharedPreferences.getString(Constants.KEY_NAME,"")
        val weight=sharedPreferences.getFloat(Constants.KEY_WEIGHT,80f)
        etName.setText(name)
        etWeight.setText(weight.toString())
    }

    private fun applyChangesToSharedPreferences():Boolean{
        val nameText=etName.text.toString()
        val weightText=etWeight.text.toString()
        if(nameText.isEmpty() || weightText.isEmpty()){
            return false
        }
        sharedPreferences.edit()
            .putString(Constants.KEY_NAME,nameText)
            .putFloat(Constants.KEY_WEIGHT,weightText.toFloat())
            .apply()
        //val toolbarText="Let's Go $nameText"
        //requireActivity().tvToolbarTitle.text=toolbarText
        return true
    }
}