package com.example.prefy.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

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
