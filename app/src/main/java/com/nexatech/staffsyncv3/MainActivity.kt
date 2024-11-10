package com.nexatech.staffsyncv3

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize NavController using the correct FragmentContainerView ID
        navController = findNavController(R.id.nav_host_fragment)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Set up BottomNavigationView with NavController
        bottomNavigationView.setupWithNavController(navController)

        // Remove background of BottomNavigationView and disable placeholder item
        bottomNavigationView.background = null
        bottomNavigationView.menu.getItem(2).isEnabled = false // Disables the middle item

        // Handle FloatingActionButton click to navigate to ClockingFragment
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            navController.navigate(R.id.clockingFragment)
            bottomNavigationView.visibility = View.GONE
            fab.hide()
        }

        // Add a listener to show/hide BottomNavigationView based on the destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.splashFragment) {
                // Hide BottomNavigationView when on SplashFragment
                bottomNavigationView.visibility = View.GONE
                fab.hide() // Optional: hide FAB on SplashFragment as well
            }
            else if(destination.id == R.id.loginFragment) {
                bottomNavigationView.visibility = View.GONE
                fab.hide()
            }
            else {
                // Show BottomNavigationView on other fragments
                bottomNavigationView.visibility = View.VISIBLE
                fab.show()
            }
        }
    }
}
