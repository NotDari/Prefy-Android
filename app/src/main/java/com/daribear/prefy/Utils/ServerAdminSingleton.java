package com.daribear.prefy.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.daribear.prefy.Profile.User;
import com.daribear.prefy.R;
import com.daribear.prefy.Utils.SharedPreferences.Utils;

public class ServerAdminSingleton {
    private static ServerAdminSingleton instance;
    private String serverAddress;
    private String serverAuthToken;
    private Long loggedInId;

    private SQLiteDatabase sqLiteDatabase;

    public static ServerAdminSingleton getInstance(){
        if (instance == null){
            instance = new ServerAdminSingleton();
        }
        return instance;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getServerAuthToken() {
        return serverAuthToken;
    }

    public void setServerAuthToken(String serverAuthToken) {
        this.serverAuthToken = serverAuthToken;
    }

    public void alterLoggedInUser(Context context){
        Utils utils = new Utils(context);
        Long userId = utils.loadLong(context.getString(R.string.save_user_id), -1);
        if (userId == -1){
            userId = null;
        }
        this.loggedInId = userId;
    }

    public Long getLoggedInId() {
        return loggedInId;
    }

    public SQLiteDatabase getSqLiteDatabase() {
        return sqLiteDatabase;
    }

    public void setSqLiteDatabase(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
    }

    public static User getCurrentUser(Context appContext){
        Utils utils = new Utils(appContext);
        User user = new User();
        user.setProfileImageURL(utils.loadString(appContext.getString(R.string.save_profileP_pref), ""));
        user.setUsername(utils.loadString(appContext.getString(R.string.save_username_pref), ""));
        user.setId(utils.loadLong(appContext.getString(R.string.save_user_id), 0));
        user.setVerified(utils.loadBoolean(appContext.getString(R.string.save_verified_pref), false));
        user.setVk(utils.loadString(appContext.getString(R.string.save_vk_pref), ""));
        user.setInstagram(utils.loadString(appContext.getString(R.string.save_instagram_pref), ""));
        user.setTwitter(utils.loadString(appContext.getString(R.string.save_twitter_pref), ""));
        user.setPrefsNumber(utils.loadLong(appContext.getString(R.string.save_prefCount_pref), 0));
        user.setVotesNumber(utils.loadLong(appContext.getString(R.string.save_voteCount_pref), 0));
        user.setPostsNumber(utils.loadLong(appContext.getString(R.string.save_postCount_pref), 0));
        user.setFullname(utils.loadString(appContext.getString(R.string.save_fullname_pref), ""));
        return user;
    }
}
