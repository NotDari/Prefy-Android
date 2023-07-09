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
import androidx.navigation.fragment.findNavController
import com.daribear.prefy.Activities.MainActivity
import com.daribear.prefy.Profile.User
import com.daribear.prefy.R
import com.daribear.prefy.Utils.CurrentTime
import com.daribear.prefy.Utils.JsonUtils.CustomJsonMapper
import com.daribear.prefy.Utils.PlayIntegrity.IntegrityDelegate
import com.daribear.prefy.Utils.PlayIntegrity.IntegrityResponse
import com.daribear.prefy.Utils.PlayIntegrity.PlayIntegrity
import com.daribear.prefy.Utils.ServerAdminSingleton
import com.daribear.prefy.Utils.SharedPreferences.SharedPrefs
import com.daribear.prefy.databinding.FragmentLogInBinding
import com.google.firebase.crashlytics.FirebaseCrashlytics
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.Executors
import kotlin.math.sign


class log_in_fragment : androidx.fragment.app.Fragment(){
    private var _binding: FragmentLogInBinding? = null

    private val binding get() = _binding!!

    var emailDone = false; var profilePDone = false; var usernameDone = false; var fullnameDone = false
    lateinit var sharedprefs: SharedPrefs
    var loginAttempted = false



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        handleBottomText()
        _binding = FragmentLogInBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleLoginButtonPressed()
        changeVisibility()

    }


    private fun changeVisibility(){
        var textVisible = false;
        binding.LogInPasswordEditText.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                val DRAWABLE_RIGHT = 2;
                when (event?.action) {
                    MotionEvent.ACTION_DOWN ->

                        if (event.getRawX() >= (binding.LogInPasswordEditText.getRight() - binding.LogInPasswordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())){
                            if (!textVisible){
                                binding.LogInPasswordEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(binding.LogInPasswordEditText.context, R.drawable.ic_baseline_visibility_off_24), null);
                                binding.LogInPasswordEditText.transformationMethod = SingleLineTransformationMethod.getInstance()
                                textVisible = true
                            } else {
                                binding.LogInPasswordEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(binding.LogInPasswordEditText.context, R.drawable.ic_baseline_visibility_24), null);
                                binding.LogInPasswordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                                textVisible = false

                            }

                        }
                }

                return v?.onTouchEvent(event) ?: true
            }
        })
    }

    private fun handleLoginButtonPressed(){
        val passwordEditText = binding.LogInPasswordEditText
        val emailEditText = binding.LogInEmailEditText
        val loginButton = binding.logInButton
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
                println("Sdad timer:" + CurrentTime.getCurrentTime())
                getToken(login, password)
                //signIn(login, password)
            }
        }


        val forgottenDetailsText = binding.LoginForgotDetailsText;
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

    private fun getToken(email:String, password:String){
        val playIntegrity : PlayIntegrity = PlayIntegrity.getInstance()
        if (playIntegrity.token == null){
            println("Sdad hello!")
            playIntegrity.setIntegrityDelegate(IntegrityDelegate {
                if (it.success){
                    signIn(email, password, it.token)
                } else {
                    playIntegrity.setIntegrityDelegate(null)
                    activity?.runOnUiThread{
                        loginFailed()
                        Toast.makeText(
                            requireActivity(),
                            "Couldn't connect to server",
                            Toast.LENGTH_LONG
                        ).show()
                    }


                }
            })
        } else {
            signIn(email, password, playIntegrity.token)
        }
    }


    private fun signIn(email:String, password:String, token:String){

        val executor = Executors.newSingleThreadExecutor();

        executor.execute{
            val client = OkHttpClient()


            val json = JSONObject()
            json.put("username", email)
            json.put("password", password)
            json.put("token", token)

            val body = RequestBody.create(
                MediaType.parse("application/json"), json.toString()
            )
            val request = Request.Builder()
                .url(ServerAdminSingleton.getInstance().serverAddress +"/login")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build()
            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val token = CustomJsonMapper.getJWTToken(response);
                    val user = CustomJsonMapper.getUser(response);
                    sharedprefs.putStringSharedPref(getString(R.string.save_auth_token_pref), token)
                    if (!emailDone){
                        val request = Request.Builder()
                            .url(ServerAdminSingleton.getInstance().serverAddress + "/prefy/v1/Login/GetEmail")
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
                        println("Sdad timer:" + CurrentTime.getCurrentTime())
                        addPrefs(user)
                    }

                    println("Sdad user:" + user)

                } else {
                    activity?.runOnUiThread {
                        val jsonBodyResponse = CustomJsonMapper.getCustomError(response);
                        if (jsonBodyResponse != null) {
                            when (jsonBodyResponse.customCode) {
                                1,2 -> {
                                    loginFailed()
                                    Toast.makeText(
                                        requireActivity(),
                                        jsonBodyResponse.message,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                3 -> {
                                    val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.signUpFragmentContainerView) as NavHostFragment
                                    val navController = navHostFragment.navController
                                    val bundle : Bundle = Bundle()
                                    bundle.putString("email", email)
                                    bundle.putString("password", password)
                                    navController.navigate(R.id.action_log_in_fragment_to_emailConfirmationFragment, bundle)

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
        sharedprefs.putLongSharedPref(getString(R.string.save_follower_pref), user.followerNumber)
        sharedprefs.putLongSharedPref(getString(R.string.save_following_pref), user.followingNumber)
        ServerAdminSingleton.getInstance().alterLoggedInUser(context)
        usernameDone = true
        fullnameDone = true
        profilePDone = true
        activity?.runOnUiThread {
            checkIfDone()
        }
        FirebaseCrashlytics.getInstance().setUserId(user.id.toString())

    }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}