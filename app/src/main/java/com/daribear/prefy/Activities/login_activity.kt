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

/**
 * This is the activities which handles the login/registration of the user.
 * It cycles between fragments to simulate the login process.
 */
class login_activity : AppCompatActivity() {
    private lateinit var configUpdateListener : ConfigUpdateListenerRegistration

    /**
     * Called when fragment creates.
     * Checks if the app should be in dark or light mode and handles the initialisation of other settings.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedpreferences =
            Utils(applicationContext)
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

    /**
     * Creates a snackbar with a custom message if a custom error code was received.
     */
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

    /**
     * This handles the auto-login of a user if a user ID is saved
     * Otherwise, it creates a new login and runs the play integrity check
     */
    private fun handleUsernameLogin(){
        val sharedPrefs =
            Utils(baseContext);
        if (sharedPrefs.loadLong(getString(R.string.save_user_id), -1) != -1L){
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        } else {
            setContentView(R.layout.activity_login)
            //Play integrity check
            PlayIntegrity.getInstance()
            PlayIntegrity.getInstance().getResponse(applicationContext)
            clearDatabases()
        }
    }

    /**
     * Checks if there is a high priority update required on the playstore,
     * which would require immediate prompting of the user
     */
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

    /**
     * Initialises firebase remote config and sets the default values in the app
     * It also updates server addresses and ad frequencies dynamically.
     */
    private fun getDefaultSettings(){
        FirebaseApp.initializeApp(this)
        //Get firebase remote config
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        //Update api link and ad frequency
        ServerAdminSingleton.getInstance().serverAddress = remoteConfig.getString("Api_link")
        AdTracker.getInstance().setTotals(remoteConfig.getLong("interstitial_popular_frequency").toInt(), remoteConfig.getLong("interstitial_other_frequency").toInt())
        //Create listener for dynamic add updating
        configUpdateListener = remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate : ConfigUpdate) {
                if (!isDestroyed) {
                    remoteConfig.activate().addOnCompleteListener {
                        //Listen for api link
                        if (configUpdate.updatedKeys.contains("Api_link")) {
                            ServerAdminSingleton.getInstance().serverAddress = remoteConfig.getString("Api_link")
                        }
                        var adCounterChanged = 0
                        //Listen for popular ad frequency
                        if (configUpdate.updatedKeys.contains("interstitial_popular_frequency")){
                            AdTracker.getInstance().setPopularTotal(remoteConfig.getLong("interstitial_popular_frequency").toInt())
                            adCounterChanged = 1
                        }
                        //Listen for normal ad frequency
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

    /**
     * When the activity is destroyed
     */
    override fun onDestroy() {
        this.configUpdateListener.remove()
        super.onDestroy()
    }

    /**
     * Clears app databases if necessary
     */
    private fun clearDatabases(){
        DatabaseBlankChecker.checkDatabases(applicationContext)
    }



}