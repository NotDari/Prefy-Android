package com.daribear.prefy.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.daribear.prefy.R
import com.daribear.prefy.Utils.Utils
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.auth.FirebaseAuth


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
        checkForUpdate()
        handleUsernameLogin()
        createSnackBar()


    }

    private fun createSnackBar(){
        val customCode = this.intent.getIntExtra("customCode", -1);
        val parentLayout: View = findViewById(android.R.id.content)
        if (customCode != -1){
            val text : String
            when (customCode) {
                2 -> text = "Your account has been locked"
                3 -> text = "Your account has been disabled"
                else -> {
                    text = "Unknown error"
                }
            }
            Snackbar.make(parentLayout, text, Snackbar.LENGTH_SHORT).show()
        }
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

    private fun checkForUpdate(){
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.updatePriority() >= 4 /* high priority */
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    0)
            }
        }
    }






}