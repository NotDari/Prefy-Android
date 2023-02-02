package com.example.prefy.Application;

import android.app.Application;

import com.example.prefy.R;
import com.example.prefy.Utils.ServerAdminSingleton;
import com.example.prefy.Utils.Utils;

import okhttp3.internal.Util;


public class PrefyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ServerAdminSingleton.getInstance().setServerAddress(this.getString(R.string.Server_base_address));




    }

}
