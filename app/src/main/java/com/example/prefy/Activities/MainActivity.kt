package com.example.prefy.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.prefy.R
import com.google.android.material.bottomnavigation.BottomNavigationView
/**
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        handleBottomNavigation()
    }

    private fun handleBottomNavigation(){
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.FragmentContainerView) as NavHostFragment
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.BottomNav)
        bottomNavigation.itemIconTintList = null
        val navController = navHostFragment.navController
        bottomNavigation.setupWithNavController(navController)
    }


}
        */