package com.example.prefy.fragments.login_fragments

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.text.method.SingleLineTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.prefy.Activities.MainActivity
import com.example.prefy.R
import com.example.prefy.Utils.CustomJsonMapper
import com.example.prefy.Utils.GetInternet
import com.example.prefy.Utils.SharedPrefs
import com.example.prefy.custom_classes.UsersInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_sign_up_email.*
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class sign_up_email_fragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference
    private var username = ""
    private var full_name = ""
    private var email = ""
    private var password = ""
    private val args: sign_up_email_fragmentArgs by navArgs()
    private var userdone = false
    private var fullnamedone = false
    private var authenticationdone = false;
    private var buttonActive = false;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        auth = FirebaseAuth.getInstance()
        if (args.signUpUsername != null){
            username = args.signUpUsername.toString()
        }
        if (args.signUpFullName != null){
            full_name = args.signUpFullName.toString()
        }
        return inflater.inflate(R.layout.fragment_sign_up_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        handleInput()
        changeVisibility()
        super.onViewCreated(view, savedInstanceState)

    }

    fun handleInput(){
        val emailEditText = EmailEditText
        val passwordEditText = passwordEditText
        val CreateButton = signUpCreateAccount
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
                Executors.newSingleThreadExecutor().execute(){
                    val client = OkHttpClient()
                    val body = RequestBody.create(
                        MediaType.parse("application/json"), json.toString()
                    )
                    val request = Request.Builder()
                        .url(getString(R.string.Server_base_address) +"/prefy/v1/Registration")
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
                            activity?.runOnUiThread {
                                Toast.makeText(requireActivity(), "Couldn't connect to server", Toast.LENGTH_LONG).show()
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

                /**
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            Log.d("Auth", "createUserWithEmail:success")
                            addUser(email)
                        } else {
                            Log.w("Auth", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(activity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                */
            }
        }
    }

    private fun changeVisibility(){
        var textVisible = false;
        passwordEditText.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                val DRAWABLE_RIGHT = 2;
                when (event?.action) {
                    MotionEvent.ACTION_DOWN ->
                        if (event.getRawX() >= (passwordEditText.getRight() - passwordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())){
                            if (!textVisible){
                                passwordEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(passwordEditText.context, R.drawable.ic_baseline_visibility_off_24), null);
                                passwordEditText.transformationMethod = SingleLineTransformationMethod.getInstance()
                                textVisible = true
                            } else {
                                passwordEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(passwordEditText.context, R.drawable.ic_baseline_visibility_24), null);
                                passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                                textVisible = false

                            }
                        }
                }

                return v?.onTouchEvent(event) ?: true
            }
        })
    }

    fun addUser(email:String){
        db = Firebase.database.reference
        val user = kotlinUser(username, "none")
        val userInfo = UsersInfo(full_name)
        val uid: String
        if (auth.currentUser != null){
            val Fireuser : FirebaseUser = auth.currentUser!!
            uid = Fireuser.uid.toString()
            db.child("users").child(uid).setValue(user).addOnCompleteListener{
                userdone = true
                checkDone()
                }
            db.child("usersInfo").child(uid).setValue(userInfo).addOnCompleteListener{
                fullnamedone = true
                checkDone()
            }
            val authentication: MutableMap<String, Any> = java.util.HashMap()
            authentication[username] = email
            db.child("authentication").setValue(authentication).addOnCompleteListener{
                authenticationdone = true;
                checkDone()
            }
        } else {
            Log.d("Auth", "NO LOGGED IN USER")
        }

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






}