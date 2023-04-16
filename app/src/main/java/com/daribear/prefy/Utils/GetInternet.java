package com.daribear.prefy.Utils;

public class GetInternet {




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
