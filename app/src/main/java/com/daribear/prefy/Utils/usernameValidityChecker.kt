package com.daribear.prefy.Utils

import android.util.Log

class usernameValidityChecker {

    fun checkInput(input:String ): Boolean{
        var characterChecker: Boolean = false
        if ( ! input.matches(Regex(".*[^a-z0-9_].*")) ) {
            characterChecker = true;
        }
        //val characterChecker: Boolean = input.matches(Regex(".*[a-z0-9_].*"))
        //val characterChecker: Boolean = Pattern.compile(".*[a-z0-9_].*").matcher(input).find()
        var sizeChecker: Boolean = false
        if (input.length >= 2 && input.length <= 20){
            sizeChecker = true
        }
        Log.d("SdadTag", characterChecker.toString())
        return sizeChecker && characterChecker
    }
}