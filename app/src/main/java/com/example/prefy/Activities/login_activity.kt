package com.example.prefy.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.prefy.R
import com.example.prefy.Utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class login_activity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedpreferences = Utils(applicationContext)
        //Checking if nightmode
        //Checking if nightmode
        if (sharedpreferences.loadBoolean(applicationContext.getString(R.string.dark_mode_pref), false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        super.onCreate(savedInstanceState)
        handleUsernameLogin()


    }

    private fun handleUsernameLogin(){
        val sharedPrefs = Utils(baseContext);
        if (sharedPrefs.loadLong(getString(R.string.save_user_id), -1) != -1L){
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        } else {
            setContentView(R.layout.activity_login)
        }
    }





}