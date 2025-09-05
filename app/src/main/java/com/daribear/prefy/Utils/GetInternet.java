package com.daribear.prefy.Utils;

/**
 * Class to check if internet is available
 */
public class GetInternet {


    /**
     * Checks if internet is available by pinging gogole
     * @return whether internet is available.
     */
    public static boolean isInternetAvailable() {
        try {
            String command = "ping -c 1 google.com";
            if (Runtime.getRuntime().exec(command).waitFor() == 0){
                return true;
            }else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }


    }



}
