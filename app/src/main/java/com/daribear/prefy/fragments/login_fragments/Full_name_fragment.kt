package com.daribear.prefy.fragments.login_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daribear.prefy.R
import  kotlinx.android.synthetic.main.fragment_full_name.*



class Full_name_fragment : Fragment() {
        private var username:String = ""
        private val args: Full_name_fragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (args.signUpUsername != null){
            username = args.signUpUsername.toString()
        }
        handleBottomText()
        return inflater.inflate(R.layout.fragment_full_name, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        handleNextButton()
        super.onViewCreated(view, savedInstanceState)
    }

    fun handleNextButton(){
        val nextButton = signUpFullNameNextMaterialButton
        val editText = FullNameEditText
        nextButton.setOnClickListener{
            var fullName: String = editText.text.toString()
            if (!fullName.isEmpty()){
                val action = Full_name_fragmentDirections.actionFullNameFragmentToSignUpEmailFragment()
                action.setSignUpFullName(fullName)
                action.setSignUpUsername(username)
                findNavController().navigate(action)
            }

        }
    }

    private fun handleBottomText(){
        val SignUpLoginTextView = requireActivity().findViewById<RelativeLayout>(R.id.SignUpLoginTextView)
        SignUpLoginTextView.isVisible = false
    }

}