package com.daribear.prefy.fragments.login_fragments

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.method.SingleLineTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daribear.prefy.Activities.MainActivity
import com.daribear.prefy.R
import com.daribear.prefy.Utils.JsonUtils.CustomJsonMapper
import com.daribear.prefy.Utils.GetInternet
import com.daribear.prefy.Utils.PlayIntegrity.IntegrityDelegate
import com.daribear.prefy.Utils.PlayIntegrity.PlayIntegrity
import com.daribear.prefy.Utils.ServerAdminSingleton
import com.daribear.prefy.Utils.SharedPreferences.SharedPrefs
import com.daribear.prefy.databinding.FragmentSignUpEmailBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.Executors


class sign_up_email_fragment : androidx.fragment.app.Fragment() {
    private var _binding: FragmentSignUpEmailBinding? = null

    private val binding get() = _binding!!

    private lateinit var db: DatabaseReference
    private var username = ""
    private var full_name = ""
    private var email = ""
    private var password = ""
    private lateinit var DOB : Date
    private val args: sign_up_email_fragmentArgs by navArgs()
    private var userdone = false
    private var fullnamedone = false
    private var authenticationdone = false;
    private var buttonActive = false;



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        //auth = FirebaseAuth.getInstance()
        if (args.signUpUsername != null){
            username = args.signUpUsername.toString()
        }
        if (args.signUpFullName != null){
            full_name = args.signUpFullName.toString()
        }
        DOB = args.dob
        _binding = FragmentSignUpEmailBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        handleInput()
        changeVisibility()
        enableLinks()
        super.onViewCreated(view, savedInstanceState)

    }

    fun handleInput(){
        val emailEditText = binding.EmailEditText
        val passwordEditText = binding.passwordEditText
        val CreateButton = binding.signUpCreateAccount
        CreateButton.setOnClickListener{
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            this.email = email
            this.password = password
            if (!email.isEmpty() && ! password.isEmpty() && !buttonActive){
                buttonActive = true
                val json = JSONObject()
                json.put("username", username)
                json.put("email", email)
                json.put("password", password)
                json.put("fullname", full_name)
                val spf = SimpleDateFormat("yyyy-MM-dd");
                val dateOfBirth = spf.format(DOB);
                json.put("DOB", dateOfBirth)
                getToken(json)

            }
        }
    }

    private fun getToken(json: JSONObject){
        val playIntegrity : PlayIntegrity = PlayIntegrity.getInstance()
        if (playIntegrity.token == null){
            println("Sdad hello!")
            playIntegrity.setIntegrityDelegate(IntegrityDelegate {
                if (it.success){
                    json.put("token", it.token)
                    sendRequest(json)
                } else {
                    playIntegrity.setIntegrityDelegate(null)
                    activity?.runOnUiThread{
                        buttonActive = false
                        Toast.makeText(
                            requireActivity(),
                            "Couldn't connect to server",
                            Toast.LENGTH_LONG
                        ).show()
                    }


                }
            })
        } else {
            json.put("token", playIntegrity.token)
            sendRequest(json)
        }
    }

    fun sendRequest(json : JSONObject){
        Executors.newSingleThreadExecutor().execute(){
            val client = OkHttpClient()
            val body = RequestBody.create(
                MediaType.parse("application/json"), json.toString()
            )
            val request = Request.Builder()
                .url(ServerAdminSingleton.getInstance().serverAddress +"/prefy/v1/Registration")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build()
            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    activity?.runOnUiThread {
                        val directions = sign_up_email_fragmentDirections.actionSignUpEmailFragmentToEmailConfirmationFragment(email, password)
                        findNavController().navigate(directions) }

                } else {
                    val customError = CustomJsonMapper.getCustomError(response)
                    var message : String
                    if (customError == null){
                        message = "Couldn't connect to server"
                    }else {
                        when (customError.customCode) {
                            12 -> message = "Email not valid"
                            13 -> message = "Email already in use"
                            else -> {
                                message = "Couldn't connect to server"
                            }
                        }
                    }
                    activity?.runOnUiThread {
                        Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()
                    }
                    buttonActive = false

                }
            } catch (i: IOException) {
                buttonActive = false
                if (!GetInternet.isInternetAvailable()){
                    activity?.runOnUiThread {
                        Toast.makeText(requireActivity(), ("No internet"), Toast.LENGTH_LONG).show()
                    }
                } else {
                    activity?.runOnUiThread {
                        Toast.makeText(requireActivity(), ("Unknown Error"), Toast.LENGTH_LONG).show()
                        val directions = sign_up_email_fragmentDirections.actionGlobalSignUpUsernameFragment()
                        findNavController().navigate(directions)
                    }
                }


            }
        }
    }

    private fun changeVisibility(){
        var textVisible = false;
        binding.passwordEditText.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                val DRAWABLE_RIGHT = 2;
                when (event?.action) {
                    MotionEvent.ACTION_DOWN ->
                        if (event.getRawX() >= (binding.passwordEditText.getRight() - binding.passwordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())){
                            if (!textVisible){
                                binding.passwordEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(binding.passwordEditText.context, R.drawable.ic_baseline_visibility_off_24), null);
                                binding.passwordEditText.transformationMethod = SingleLineTransformationMethod.getInstance()
                                textVisible = true
                            } else {
                                binding.passwordEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(binding.passwordEditText.context, R.drawable.ic_baseline_visibility_24), null);
                                binding.passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                                textVisible = false

                            }
                        }
                }

                return v?.onTouchEvent(event) ?: true
            }
        })
    }

    private fun checkDone(){
        if (authenticationdone && fullnamedone && userdone){
            val sharedprefs = SharedPrefs(requireActivity())
            sharedprefs.putStringSharedPref(getString(R.string.save_username_pref), username)
            sharedprefs.putStringSharedPref(getString(R.string.save_email_pref), email)
            sharedprefs.putStringSharedPref(getString(R.string.save_fullname_pref), full_name)
            sharedprefs.putStringSharedPref(getString(R.string.save_password_pref), password)
            sharedprefs.putStringSharedPref(getString(R.string.save_profileP_pref), "none")
            sharedprefs.putLongSharedPref(getString(R.string.save_prefCount_pref), 0);
            sharedprefs.putLongSharedPref(getString(R.string.save_voteCount_pref), 0);
            sharedprefs.putLongSharedPref(getString(R.string.save_postCount_pref), 0);
            sharedprefs.putStringSharedPref(getString(R.string.save_instagram_pref), "");
            sharedprefs.putStringSharedPref(getString(R.string.save_twitter_pref), "");
            sharedprefs.putStringSharedPref(getString(R.string.save_vk_pref), "");
            val intent = Intent(activity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }


    private fun enableLinks() {
        binding.signUpTermsText?.setMovementMethod(LinkMovementMethod.getInstance())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }





}