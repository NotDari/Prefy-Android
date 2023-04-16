package com.daribear.prefy.Application;

import android.app.Application;

import com.daribear.prefy.R;
import com.daribear.prefy.Utils.ServerAdminSingleton;


public class PrefyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ServerAdminSingleton.getInstance().setServerAddress(this.getString(R.string.Server_base_address));




    }

}
