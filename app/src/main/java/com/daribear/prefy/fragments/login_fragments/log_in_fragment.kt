package com.daribear.prefy.fragments.login_fragments

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.text.method.SingleLineTransformationMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.daribear.prefy.Activities.MainActivity
import com.daribear.prefy.Profile.User
import com.daribear.prefy.R
import com.daribear.prefy.Utils.CustomJsonMapper
import com.daribear.prefy.Utils.ServerAdminSingleton
import com.daribear.prefy.Utils.SharedPrefs
import com.daribear.prefy.Utils.Utils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_log_in.*
import okhttp3.*
import java.io.IOException
import java.util.concurrent.Executors


class log_in_fragment : Fragment() {

    var emailDone = false; var profilePDone = false; var usernameDone = false; var fullnameDone = false
    lateinit var sharedprefs:SharedPrefs
    var loginAttempted = false




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        handleBottomText()
        return inflater.inflate(R.layout.fragment_log_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleLoginButtonPressed()
        changeVisibility()

    }


    private fun changeVisibility(){
        var textVisible = false;
        LogInPasswordEditText.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                val DRAWABLE_RIGHT = 2;
                when (event?.action) {
                    MotionEvent.ACTION_DOWN ->
                        if (event.getRawX() >= (LogInPasswordEditText.getRight() - LogInPasswordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())){
                            if (!textVisible){
                                LogInPasswordEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(LogInPasswordEditText.context, R.drawable.ic_baseline_visibility_off_24), null);
                                LogInPasswordEditText.transformationMethod = SingleLineTransformationMethod.getInstance()
                                textVisible = true
                            } else {
                                LogInPasswordEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(LogInPasswordEditText.context, R.drawable.ic_baseline_visibility_24), null);
                                LogInPasswordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                                textVisible = false

                            }
                        }
                }

                return v?.onTouchEvent(event) ?: true
            }
        })
    }

    private fun handleLoginButtonPressed(){
        val passwordEditText = LogInPasswordEditText
        val emailEditText = LogInEmailEditText
        val loginButton = logInButton
        sharedprefs = SharedPrefs(requireActivity().applicationContext)
        loginButton.setOnClickListener{
            if (!loginAttempted) {
                loginAttempted = true
                emailDone = false;
                var login: String
                if (emailEditText.text.toString().contains("@")) {
                    emailDone = true;
                    login = emailEditText.text.toString().trimEnd()
                    sharedprefs.putStringSharedPref(getString(R.string.save_email_pref), login)
                } else {
                    login = emailEditText.text.toString().trimEnd().lowercase()
                }
                val password: String = passwordEditText.text.toString()
                signIn(login, password)
            }
        }

        val forgottenDetailsText = LoginForgotDetailsText;
        forgottenDetailsText.setOnClickListener{
            val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.signUpFragmentContainerView) as NavHostFragment
            val navController = navHostFragment.navController
            navController.navigate(R.id.action_log_in_fragment_to_resetPasswordFragment)
        }

    }

    private fun handleBottomText(){
        val DontHaveAccount = requireActivity().findViewById<TextView>(R.id.signUpAlreadyHaveanAccount)
        val textButton = requireActivity().findViewById<TextView>(R.id.SignUplogInButton)
        DontHaveAccount.text = "Don't have an account?"
        textButton.text = " Sign Up"
        textButton.setOnClickListener{
            val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.signUpFragmentContainerView) as NavHostFragment
            val navController = navHostFragment.navController
            navController.navigate(R.id.action_global_sign_up_username_fragment)
        }
    }

    private fun signIn(email:String, password:String){
        val executor = Executors.newSingleThreadExecutor();
        executor.execute{
            val client = OkHttpClient()

            val json = "{ " + "\"username\" : " + "\"" + email + "\"" + ",\n"+ "\"password\" : \""  + password + "\"" +
                    "}"

            val body = RequestBody.create(
                MediaType.parse("application/json"), json
            )
            val request = Request.Builder()
                .url(getString(R.string.Server_base_address) +"/login")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build()
            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val token = CustomJsonMapper.getJWTToken(response);
                    val user = CustomJsonMapper.getUser(response);
                    sharedprefs.putStringSharedPref(getString(R.string.save_auth_token_pref), token)
                    val utils = Utils(context)
                    if (!emailDone){
                        val request = Request.Builder()
                            .url(getString(R.string.Server_base_address) + "/prefy/v1/Login/GetEmail")
                            .method("GET", null)
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", token)
                            .build()
                        val response: Response = client.newCall(request).execute()
                        if (response.isSuccessful){
                            val email = CustomJsonMapper.getEmail(response)
                            if (email != null){
                                sharedprefs.putStringSharedPref(getString(R.string.save_email_pref), email)
                                emailDone = true


                                println("Sdad email:" + email)
                                addPrefs(user)
                            } else {
                                activity?.runOnUiThread {
                                    loginFailed()
                                    Toast.makeText(requireActivity(), "Couldn't connect to server", Toast.LENGTH_LONG).show()
                                }
                            }
                        }else {
                            activity?.runOnUiThread {
                                loginFailed()
                                Toast.makeText(requireActivity(), "Couldn't connect to server", Toast.LENGTH_LONG).show()
                            }
                        }

                    } else {
                        addPrefs(user)
                    }

                    println("Sdad user:" + user)

                } else {
                    activity?.runOnUiThread {
                        val jsonBodyResponse = CustomJsonMapper.getCustomError(response);
                        if (jsonBodyResponse != null) {
                            when (jsonBodyResponse.customCode) {
                                1,2,3 -> {
                                    loginFailed()
                                    Toast.makeText(
                                        requireActivity(),
                                        jsonBodyResponse.message,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                                else -> {
                                    loginFailed()
                                    Toast.makeText(
                                        requireActivity(),
                                        "Couldn't connect to server",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        } else{
                            loginFailed()
                            Toast.makeText(
                                requireActivity(),
                                "Couldn't connect to server",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                }
            } catch (i: IOException) {
                activity?.runOnUiThread {
                    println("Sdad Error:" + i.cause + " " + i.message)
                    loginFailed()
                    Toast.makeText(requireActivity(), ("Failed to connect to server"), Toast.LENGTH_LONG).show()
                }

            }
        }

        /**
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity(), OnCompleteListener { task ->
            if(task.isSuccessful) {
                downloadprefs2()
            }else {
                Toast.makeText(requireActivity(), "Login Failed", Toast.LENGTH_LONG).show()
            }
        })
        */
    }

    fun addPrefs(user : User){
        sharedprefs.putStringSharedPref(getString(R.string.save_fullname_pref), user.fullname)
        sharedprefs.putLongSharedPref(getString(R.string.save_postCount_pref), user.postsNumber)
        sharedprefs.putLongSharedPref(getString(R.string.save_voteCount_pref), user.votesNumber)
        sharedprefs.putLongSharedPref(getString(R.string.save_prefCount_pref), user.prefsNumber)
        sharedprefs.putStringSharedPref(getString(R.string.save_instagram_pref), user.instagram);
        sharedprefs.putStringSharedPref(getString(R.string.save_twitter_pref), user.twitter);
        sharedprefs.putStringSharedPref(getString(R.string.save_vk_pref), user.vk);
        sharedprefs.putBooleanSharedPref(getString(R.string.save_verified_pref), user.verified)
        sharedprefs.putLongSharedPref(getString(R.string.save_user_id), user.id)
        ServerAdminSingleton.getInstance().alterLoggedInUser(context)
        usernameDone = true
        fullnameDone = true
        profilePDone = true
        activity?.runOnUiThread {
            checkIfDone()
        }
        FirebaseCrashlytics.getInstance().setUserId(user.id.toString())

    }
    /**

    fun downloadprefs2(){
        val Uid = auth.uid.toString()
        if (!emailDone){
            val sharedprefs = SharedPrefs(requireActivity())
            sharedprefs.putStringSharedPref(getString(R.string.save_email_pref), email)
            emailDone = true
        }
        if (!usernameDone && !profilePDone && !fullnameDone){
            ff.collection("Users").document(Uid).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.getResult()
                    if (document.exists()){
                        val sharedprefs = SharedPrefs(requireActivity())
                        sharedprefs.putStringSharedPref(getString(R.string.save_username_pref), username)
                        usernameDone = true;
                        sharedprefs.putStringSharedPref(getString(R.string.save_profileP_pref), profileP)
                        profilePDone = true;

                        fullname = document.get("fullname").toString()
                        prefCount =  document.get("prefsNumber").toString().toLong()
                        var bio =  document.get("bio").toString()
                        if (prefCount == null){
                            prefCount = 0L;
                        }
                        voteCount = document.get("votesNumber").toString().toLong()
                        if (voteCount == null){
                            voteCount = 0L;
                        }
                        postCount = document.get("postsNumber").toString().toLong()
                        if (postCount == null){
                            postCount = 0L;
                        }
                        if (bio != null){
                            sharedprefs.putStringSharedPref(getString(R.string.save_bio_pref), bio)
                        }
                        var instagram = document.get("instagram").toString()
                        var twitter = document.get("twitter").toString()
                        var vk = document.get("vk").toString()
                        if (instagram == null){
                            instagram = "";
                        }; if (twitter == null){
                            twitter = "";
                        }; if (vk == null){
                            vk = "";
                        }

                        var verified = document.get("verified").toString().toBoolean()

                        sharedprefs.putStringSharedPref(getString(R.string.save_fullname_pref), fullname)
                        sharedprefs.putLongSharedPref(getString(R.string.save_postCount_pref), postCount)
                        println("Sdad postCount:" + postCount)
                        sharedprefs.putLongSharedPref(getString(R.string.save_voteCount_pref), voteCount)
                        sharedprefs.putLongSharedPref(getString(R.string.save_prefCount_pref), prefCount)
                        sharedprefs.putStringSharedPref(getString(R.string.save_instagram_pref), instagram);
                        sharedprefs.putStringSharedPref(getString(R.string.save_twitter_pref), twitter);
                        sharedprefs.putStringSharedPref(getString(R.string.save_vk_pref), vk);
                        sharedprefs.putBooleanSharedPref(getString(R.string.save_verified_pref), verified)
                        fullnameDone = true;
                        checkIfDone()
                    } else {
                        Toast.makeText(requireActivity(), "Failed to find user", Toast.LENGTH_SHORT).show()
                    }





                } else {
                    Toast.makeText(requireActivity(), "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun downloadprefs(){
        val Uid = auth.uid.toString()
        if (!usernameDone){
                db.child("users").child(Uid).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()){
                            username = snapshot.child("username").getValue(String::class.java).toString()
                            val sharedprefs = SharedPrefs(requireActivity())
                            sharedprefs.putStringSharedPref(getString(R.string.save_username_pref), username)
                            Toast.makeText(requireActivity(), sharedprefs.getStringSharedPref(getString(R.string.save_username_pref)).toString(), Toast.LENGTH_SHORT).show()
                            usernameDone = true
                            checkIfDone()
                        } else {
                            Toast.makeText(requireActivity(), "Error", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                } )
            }
        if (!emailDone){
                val sharedprefs = SharedPrefs(requireActivity())
                sharedprefs.putStringSharedPref(getString(R.string.save_email_pref), email)
                emailDone = true
            }
        if (!profilePDone){
                db.child("users").child(Uid).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()){
                            profileP = snapshot.child("profileImageURL").getValue(String::class.java).toString()
                            val sharedprefs = SharedPrefs(requireActivity())
                            sharedprefs.putStringSharedPref(getString(R.string.save_profileP_pref), profileP)
                            profilePDone = true
                        } else {
                            Toast.makeText(requireActivity(), "Error", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                } )
            }
        if (!fullnameDone){
                db.child("usersInfo").child(Uid).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()){
                            fullname = snapshot.child("fullname").getValue(String::class.java).toString()
                            prefCount = snapshot.child("prefsNumber").getValue(Long::class.java).toString().toLong();
                            var bio = snapshot.child("bio").getValue(String::class.java).toString();
                            val sharedprefs = SharedPrefs(requireActivity())
                            if (prefCount == null){
                                prefCount = 0L;
                            }
                            voteCount = snapshot.child("votesNumber").getValue(Long::class.java).toString().toLong();
                            if (voteCount == null){
                                voteCount = 0L;
                            }
                            postCount = snapshot.child("postsNumber").getValue(Long::class.java).toString().toLong();
                            if (postCount == null){
                                postCount = 0L;
                            }
                            if (bio != null){
                                sharedprefs.putStringSharedPref(getString(R.string.save_bio_pref), bio)
                            }
                            var instagram = snapshot.child("instagram").getValue(String::class.java).toString();
                            var twitter = snapshot.child("twitter").getValue(String::class.java).toString();
                            var vk = snapshot.child("vk").getValue(String::class.java).toString();
                            if (instagram == null){
                                instagram = "";
                            }; if (twitter == null){
                                twitter = "";
                            }; if (vk == null){
                                vk = "";
                            }

                            sharedprefs.putStringSharedPref(getString(R.string.save_fullname_pref), fullname)
                            sharedprefs.putLongSharedPref(getString(R.string.save_postCount_pref), postCount)
                            println("Sdad postCount:" + postCount)
                            sharedprefs.putLongSharedPref(getString(R.string.save_voteCount_pref), voteCount)
                            sharedprefs.putLongSharedPref(getString(R.string.save_prefCount_pref), prefCount)
                            sharedprefs.putStringSharedPref(getString(R.string.save_instagram_pref), instagram);
                            sharedprefs.putStringSharedPref(getString(R.string.save_twitter_pref), twitter);
                            sharedprefs.putStringSharedPref(getString(R.string.save_vk_pref), vk);
                            fullnameDone = true
                            checkIfDone()
                        } else {
                            Toast.makeText(requireActivity(), "Error", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                } )
            }
    }
    */

    fun loginFailed(){
        loginAttempted = false
    }

    fun checkIfDone(){
        Toast.makeText(requireActivity(), (emailDone.toString() + fullnameDone.toString() + profilePDone.toString() + usernameDone.toString()), Toast.LENGTH_LONG).show()
        if (usernameDone && emailDone && profilePDone && fullnameDone) {
            val intent = Intent(activity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }
}