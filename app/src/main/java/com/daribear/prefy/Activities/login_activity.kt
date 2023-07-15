package com.daribear.prefy.Activities

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.daribear.prefy.Ads.AdTracker
import com.daribear.prefy.Database.DatabaseBlankChecker
import com.daribear.prefy.R
import com.daribear.prefy.Utils.PlayIntegrity.PlayIntegrity
import com.daribear.prefy.Utils.ServerAdminSingleton
import com.daribear.prefy.Utils.SharedPreferences.Utils
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.FirebaseApp

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.*
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings


class login_activity : AppCompatActivity() {
    private lateinit var configUpdateListener : ConfigUpdateListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedpreferences =
            Utils(applicationContext)
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
        val sharedPrefs =
            Utils(baseContext);
        if (sharedPrefs.loadLong(getString(R.string.save_user_id), -1) != -1L){
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        } else {
            setContentView(R.layout.activity_login)
            PlayIntegrity.getInstance()
            PlayIntegrity.getInstance().getResponse(applicationContext)
            clearDatabases()
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
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        ServerAdminSingleton.getInstance().serverAddress = remoteConfig.getString("Api_link")
        AdTracker.getInstance().setTotals(remoteConfig.getLong("interstitial_popular_frequency").toInt(), remoteConfig.getLong("interstitial_other_frequency").toInt())
        configUpdateListener = remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate : ConfigUpdate) {
                if (!isDestroyed) {
                    remoteConfig.activate().addOnCompleteListener {
                        if (configUpdate.updatedKeys.contains("Api_link")) {
                            ServerAdminSingleton.getInstance().serverAddress = remoteConfig.getString("Api_link")
                        }
                        var adCounterChanged = 0
                        if (configUpdate.updatedKeys.contains("interstitial_popular_frequency")){
                            AdTracker.getInstance().setPopularTotal(remoteConfig.getLong("interstitial_popular_frequency").toInt())
                            adCounterChanged = 1
                        }
                        if (configUpdate.updatedKeys.contains("interstitial_other_frequency")){
                            AdTracker.getInstance().setOtherTotal(remoteConfig.getLong("interstitial_other_frequency").toInt())
                            adCounterChanged = 1
                        }
                        if (adCounterChanged == 1){
                            AdTracker.getInstance().resetCounts()
                        }
                    }



                }
            }

            override fun onError(error : FirebaseRemoteConfigException) {
                Log.w("TAG", "Config update error with code: " + error.code, error)
            }
        })

    }

    override fun onDestroy() {
        this.configUpdateListener.remove()
        super.onDestroy()
    }

    private fun clearDatabases(){
        DatabaseBlankChecker.checkDatabases(applicationContext)
    }



}