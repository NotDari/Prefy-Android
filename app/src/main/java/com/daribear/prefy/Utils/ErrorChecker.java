package com.daribear.prefy.Utils;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import lombok.Getter;
import lombok.Setter;
import okhttp3.Response;

public class ErrorChecker {
    @Getter
    @Setter
    public static Activity activity;


    public static void checkForStandardError(Response response) throws IOException, JSONException {
        String responseString = response.body().string();
        JSONObject jsonObject = new JSONObject(responseString);
        String message  = jsonObject.getString("message");
        Integer customCode = jsonObject.getInt("customCode");
        switch (customCode){
            case 2:
            case 3:
                LogOutUtil.Logout(activity);
                LogOutUtil.changeActivity(activity, customCode);
                break;
            case 7:
            case 6:
                LogOutUtil.Logout(activity);
                LogOutUtil.changeActivity(activity);
                break;
            default:
                break;
        }

    }
}
