package com.daribear.prefy.Utils.GeneralUtils

import android.util.Log

/**
 * Class to check the validity of a username(not whether its already taken).
 * Checks if the username has valid characters.
 */
class usernameValidityChecker {

    /**
     * Checks the input string to make sure it matches the allowd chars which are letters, numbers and _
     */
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
        return sizeChecker && characterChecker
    }
}