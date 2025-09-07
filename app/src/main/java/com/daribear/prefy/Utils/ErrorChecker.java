package com.daribear.prefy.Utils;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import lombok.Getter;
import lombok.Setter;
import okhttp3.Response;

/**
 * Helper class to check for specific errors based on the response string.
 */
public class ErrorChecker {
    @Getter
    @Setter
    public static Activity activity;

    /**
     * Checks for specific errors from
     * @param response
     * @throws IOException if an exception occurs retrieving the json
     * @throws JSONException if there is an error with the json.
     */
    public static void checkForStandardError(Response response) throws IOException, JSONException {
        String responseString = response.body().string();
        JSONObject jsonObject = new JSONObject(responseString);
        String message  = jsonObject.getString("message");
        Integer customCode = jsonObject.getInt("customCode");
        switch (customCode){
            case 2:
            case 3:
                //Logout the user
                LogOutUtil.Logout(activity);
                LogOutUtil.changeActivity(activity, customCode);
                break;
            case 7:
            case 6:
                //Logout the user
                LogOutUtil.Logout(activity);
                LogOutUtil.changeActivity(activity);
                break;
            default:
                break;
        }

    }
}
