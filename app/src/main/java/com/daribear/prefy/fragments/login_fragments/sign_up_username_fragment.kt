package com.daribear.prefy.fragments.login_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.daribear.prefy.R
import com.daribear.prefy.Utils.ServerAdminSingleton
import com.daribear.prefy.Utils.GeneralUtils.usernameValidityChecker
import com.daribear.prefy.databinding.FragmentSignUpUsernameBinding
import com.google.android.material.button.MaterialButton
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import java.io.IOException
import java.util.concurrent.Executors


class sign_up_username_fragment : Fragment() {
    private var _binding: FragmentSignUpUsernameBinding? = null

    private val binding get() = _binding!!

    private var buttonActive = false



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        handleBottomText()
        _binding = FragmentSignUpUsernameBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handlesignupUsername()
    }

    private fun handlesignupUsername(){
        val userNameNextButton: MaterialButton = binding.signUpNextMaterialButton
        val editText: EditText = binding.usernameEditText
        userNameNextButton.setOnClickListener {
            if (!buttonActive) {
                buttonActive = true
                val userNameInput: String = editText.text.toString().toLowerCase().trimEnd()
                val checkCharacter = usernameValidityChecker()
                if (checkCharacter.checkInput(userNameInput)) {
                    if (userNameInput.isEmpty()) {
                        Toast.makeText(activity, "Username cannot be blank", Toast.LENGTH_SHORT)
                            .show()
                        buttonActive = false
                    } else {
                        Executors.newSingleThreadExecutor().execute() {
                            val client = OkHttpClient()
                            val request = Request.Builder()
                                .url(ServerAdminSingleton.getInstance().serverAddress + "/prefy/v1/Registration/UsernameAvailable?username=" + userNameInput )
                                .method("GET", null)
                                .addHeader("Content-Type", "application/json")
                                .build()
                            try {
                                val response: Response = client.newCall(request).execute()
                                if (response.isSuccessful) {
                                    try {
                                        val usernameAvailable: Boolean =
                                            response.body()?.string().toBoolean()
                                        if (usernameAvailable) {
                                            val directions =
                                                sign_up_username_fragmentDirections.actionSignUpUsernameFragmentToFullNameFragment()
                                            directions.setSignUpUsername(userNameInput)
                                            activity?.runOnUiThread {
                                                findNavController().navigate(directions)
                                            }

                                        } else {
                                            activity?.runOnUiThread {
                                                Toast.makeText(
                                                    activity,
                                                    "Username taken",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                buttonActive = false
                                            }


                                        }
                                    } catch (e: JSONException) {
                                        activity?.runOnUiThread {
                                            Toast.makeText(activity, "Error", Toast.LENGTH_SHORT)
                                                .show()
                                            buttonActive = false
                                        }
                                    }

                                } else {
                                    activity?.runOnUiThread {
                                        Toast.makeText(
                                            activity,
                                            "Couldn't connect to server",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        buttonActive = false
                                    }

                                }
                            } catch (i: IOException) {
                                activity?.runOnUiThread {
                                    Toast.makeText(activity, "No Internet", Toast.LENGTH_SHORT)
                                        .show()
                                    buttonActive = false
                                }


                            }
                        }
                    }
                } else {
                    Toast.makeText(activity, "Invalid username", Toast.LENGTH_SHORT).show()
                    buttonActive = false
                }

            }
        }
    }

    private fun handleBottomText(){
            val SignUpLoginTextView = requireActivity().findViewById<RelativeLayout>(R.id.SignUpLoginTextView)
            SignUpLoginTextView.isVisible = true
            val loginButton: TextView = requireActivity().findViewById(R.id.SignUplogInButton)
            val DontHaveAccount = requireActivity().findViewById<TextView>(R.id.signUpAlreadyHaveanAccount)
            loginButton.text = "Log In"
            DontHaveAccount.text = "Already have an account?"
            loginButton.setOnClickListener {
                val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.signUpFragmentContainerView) as NavHostFragment
                val navController = navHostFragment.navController
                navController.navigate(R.id.action_global_log_in_fragment)
            }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}