package com.hfad.runningtracker.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.hfad.runningtracker.R
import com.hfad.runningtracker.db.RunDAO
import com.hfad.runningtracker.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
open class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_RunningTracker)

        setContentView(R.layout.activity_main)


        navigateToTrackingFragmentIfNeeded(intent)

        //setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        bottomNavigationView.setupWithNavController(navHostFragment.findNavController())

        bottomNavigationView.setOnNavigationItemReselectedListener {
            //No operation
        }

        navHostFragment.findNavController()
            .addOnDestinationChangedListener{ _,destination, _ ->
                when(destination.id){
                    R.id.settingsFragment,R.id.runFragment,R.id.statisticsFragment ->
                        bottomNavigationView.visibility= View.VISIBLE
                    else->bottomNavigationView.visibility=View.GONE
                }
            }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            navigateToTrackingFragmentIfNeeded(intent)
        }
    }

    private fun navigateToTrackingFragmentIfNeeded(intent: Intent){
        if(intent.action ==ACTION_SHOW_TRACKING_FRAGMENT){
            navHostFragment.findNavController().navigate(R.id.action_global_trackingFragment)
        }
    }
}