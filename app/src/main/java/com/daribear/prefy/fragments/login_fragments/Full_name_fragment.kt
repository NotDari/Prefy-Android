package com.daribear.prefy.fragments.login_fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daribear.prefy.R
import com.daribear.prefy.databinding.FragmentFullNameBinding
import java.util.*


/**
 * The fragment which requires the user to enter their full name  and date of birth for registering
 */
class Full_name_fragment : Fragment() {
    private var _binding: FragmentFullNameBinding? = null

    private val binding get() = _binding!!

        private var username:String = ""
        private val args: Full_name_fragmentArgs by navArgs()
        private lateinit var DOB : Date
        private var yearComparison : Int = 0
        private var dayComparison : Int = 0
        private var monthComparison : Int = 0




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (args.signUpUsername != null){
            username = args.signUpUsername.toString()
        }
        handleBottomText()

        _binding = FragmentFullNameBinding.inflate(inflater, container, false)
        val view = binding.root
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        handleNextButton()
        handleDateOfBirthButton()
        super.onViewCreated(view, savedInstanceState)
    }

    /**
     * The nect button to go to the next fragment
     */
    fun handleNextButton(){
        val nextButton = binding.signUpFullNameNextMaterialButton
        val editText = binding.FullNameEditText
        nextButton.setOnClickListener{
            var fullName: String = editText.text.toString()
            if (!fullName.isEmpty()){
                val currentDate : Date = Calendar.getInstance().time
                val DOB13= GregorianCalendar(yearComparison + 13, monthComparison, dayComparison).time
                if (currentDate.after(DOB13)) {
                    val action =
                        Full_name_fragmentDirections.actionFullNameFragmentToSignUpEmailFragment(DOB)
                    action.setSignUpFullName(fullName)
                    action.setSignUpUsername(username)
                    findNavController().navigate(action)
                } else {
                    Toast.makeText(context, "You have to be over 13 to use this app", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            }

        }
    }

    /**
     * Create the picker to allow the user to enter their date of birth.
     */
    private fun handleDateOfBirthButton(){
        val dobButton = binding.DateOfBirthEditText
        val parentsPermission = binding.GetParentPermissionText
        dobButton.focusable = EditText.NOT_FOCUSABLE
        val c = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { view, year, monthOfYear, dayOfMonth ->
                val monthText : String
                when (monthOfYear + 1){
                    1 -> monthText = "January"
                    2 -> monthText = "February"
                    3 -> monthText = "March"
                    4 -> monthText = "April"
                    5 -> monthText = "May"
                    6 -> monthText = "June"
                    7 -> monthText = "July"
                    8 -> monthText = "August"
                    9 -> monthText = "September"
                    10 -> monthText = "October"
                    11 -> monthText = "November"
                    12 -> monthText = "December"
                    else -> {
                        monthText = "Error"
                    }

                }
                dobButton.setText((dayOfMonth.toString() + " " + (monthText) + " " + (year)))
                val DOB13= GregorianCalendar(year + 13, monthOfYear + 1, dayOfMonth).time
                val DOB18= GregorianCalendar(year + 18, monthOfYear + 1, dayOfMonth).time
                val currentDate : Date = Calendar.getInstance().time
                if (currentDate >= DOB13 && currentDate < DOB18){
                    parentsPermission.visibility = View.VISIBLE
                } else {
                    parentsPermission.visibility = View.GONE
                }
                DOB =GregorianCalendar(year, monthOfYear + 1, dayOfMonth).time
                dayComparison = dayOfMonth
                monthComparison = monthOfYear + 1
                yearComparison = year


            },
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH)
        )
        // at last we are calling show
        // to display our date picker dialog.

        dobButton.setOnClickListener(){
            datePickerDialog.show()
        }
    }

    /**
     * Remove the signup/login text
     */
    private fun handleBottomText(){
        val SignUpLoginTextView = requireActivity().findViewById<RelativeLayout>(R.id.SignUpLoginTextView)
        SignUpLoginTextView.isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}