package com.example.newnetworker

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.Menu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.example.newnetworker.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private val DATABASE_URL = "https://newnetworker-56eb7-default-rtdb.firebaseio.com/"
    val playerCountLiveData = MutableLiveData<Int>()
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var toolbarExitTransition: Slide
    private lateinit var bottomNavExitTransition: Slide

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fetchPlayerCount()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbarMain)

        val bottomNavView: BottomNavigationView = binding.bottomNavigation
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_game, R.id.nav_chatting, R.id.nav_profile, R.id.nav_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavView.setupWithNavController(navController)

        toolbarExitTransition = Slide(Gravity.TOP).apply {
            duration = 300 // Adjust duration as needed
            addTarget(binding.appBarMain.toolbarMain) // Replace with your Toolbar ID
        }

        // Slide out from bottom for BottomNavigationView
        bottomNavExitTransition = Slide(Gravity.BOTTOM).apply {
            duration = 300
            addTarget(binding.bottomNavigation) // Replace with your BottomNavigationView ID
        }

        window.exitTransition = toolbarExitTransition
        window.exitTransition = bottomNavExitTransition

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun fetchPlayerCount() {
        val database = FirebaseDatabase.getInstance(DATABASE_URL)
        val playersRef = database.getReference("Players")
        playersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                playerCountLiveData.value = snapshot.childrenCount.toInt()            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}