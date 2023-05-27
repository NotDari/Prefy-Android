package com.daribear.prefy.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.daribear.prefy.Ads.AdTracker
import com.daribear.prefy.R
import com.daribear.prefy.Utils.ServerAdminSingleton
import com.daribear.prefy.Utils.Utils
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.android.synthetic.main.profile_header_item.*


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
        getDefaultSettings()
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

    private fun getDefaultSettings(){
        FirebaseApp.initializeApp(this)
        FirebaseRemoteConfig.getInstance().apply {


            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(20)
                .build()
            setConfigSettingsAsync(configSettings)

            setDefaultsAsync(R.xml.remote_config_defaults)
            ServerAdminSingleton.getInstance().serverAddress = FirebaseRemoteConfig.getInstance().getString("Api_link")
            println("Sdad server: " + ServerAdminSingleton.getInstance().serverAddress)
            AdTracker.getInstance().setTotals(FirebaseRemoteConfig.getInstance().getLong("interstitial_popular_frequency").toInt(), FirebaseRemoteConfig.getInstance().getLong("interstitial_other_frequency").toInt())
            fetchAndActivate().addOnCompleteListener { task ->
                val updated = task.result
                if (task.isSuccessful) {
                    val updated = task.result
                    if (updated) {
                        ServerAdminSingleton.getInstance().serverAddress =
                            FirebaseRemoteConfig.getInstance().getString("Api_link")
                        println("Sdad server updated: " + ServerAdminSingleton.getInstance().serverAddress)
                    }
                } else {
                    Log.d("TAG", "Config params updated: $updated")
                }
            }
        }
    }





}