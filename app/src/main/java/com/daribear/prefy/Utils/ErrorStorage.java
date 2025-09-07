package com.daribear.prefy.Utils;

/**
 * Storage used to specify errors from the backend.
 */
public class ErrorStorage {

    public enum ErrorType{

        UserDetailsIncorrect(1 , "Invalid Username/Password"),
        UserAccountLocked(2,  "Account is locked"),
        UserAccountDisabled(3,  "Account is disabled"),
        InternalError(4,  "Internal Server Error"),
        UnknownError(5,  "Unknown Error"),
        UserLoggedOut(6,  "User logged out"),
        NOAUTHATTEMPTED(7,  "No Auth attempted"),

        REGEUSERTAKE(8,  "Registration Username Taken"),

        REGEMAILTAKE(9,  "Registration Email Taken");
        public final Integer errorCode;
        public final String message;



        ErrorType(Integer errorCode, String message) {
            this.errorCode = errorCode;
            this.message = message;
        }
    }


}
