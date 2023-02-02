package com.example.prefy.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.prefy.Activity.ActivityRepository;
import com.example.prefy.Database.DatabaseHelper;
import com.example.prefy.Database.InitDatabaseTasks;
import com.example.prefy.Explore.ExploreRepository;
import com.example.prefy.Popular.PopularViewModel.PopularPostsRepository;
import com.example.prefy.Profile.CurrentUserRepository;
import com.example.prefy.R;
import com.example.prefy.Search.SearchRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;

public class LogOutUtil {
    private static final ArrayList<String> tableList = new ArrayList<>(Arrays.asList("PopularPostsUsers","PopularPostsStandardPosts", "UploadTasks", "UploadVotes", "UploadActivityClear", "UploadComments" ));

    public static void Logout(Context context){
        clearSharedPrefs(context);
        clearServerAdmin(context);
        resetViewModels(context);
        clearDatabases(context);
    }

    private static void clearSharedPrefs(Context context){
        Utils utils = new Utils(context);
        utils.removeSharedPreference(context.getString(R.string.save_username_pref));
        utils.removeSharedPreference(context.getString(R.string.save_email_pref));
        utils.removeSharedPreference(context.getString(R.string.save_profileP_pref));
        utils.removeSharedPreference(context.getString(R.string.save_fullname_pref));
        utils.removeSharedPreference(context.getString(R.string.save_password_pref));
        utils.removeSharedPreference(context.getString(R.string.save_instagram_pref));
        utils.removeSharedPreference(context.getString(R.string.save_twitter_pref));
        utils.removeSharedPreference(context.getString(R.string.save_vk_pref));
        utils.removeSharedPreference(context.getString(R.string.save_auth_token_pref));
        utils.removeSharedPreference(context.getString(R.string.save_user_id));
    }

    private static void clearServerAdmin(Context context){
        ServerAdminSingleton.getInstance().setServerAuthToken(null);
        ServerAdminSingleton.getInstance().alterLoggedInUser(context);
    }

    private static void resetViewModels(Context context){
        if (!ActivityRepository.isInstanceNull()){
            ActivityRepository.getInstance().reset();
        }
        if (!ExploreRepository.isInstanceNull()){
            ExploreRepository.getInstance().reset();
        }
        if (!SearchRepository.isInstanceNull()){
            SearchRepository.getInstance().reset();
        }
        if (!CurrentUserRepository.isInstanceNull()){
            CurrentUserRepository.getInstance().reset();
        }
        if (!PopularPostsRepository.isInstanceNull()){
            PopularPostsRepository.getInstance(context).reset();
        }

    }

    private static void clearDatabases(Context context){
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = DatabaseHelper.getInstance(context.getApplicationContext()).getWritableDatabase();
                db.beginTransaction();
                for (int i = 0; i < tableList.size(); i ++){
                    db.delete(tableList.get(i), null, null);
                }
                InitDatabaseTasks.init(db);
                db.endTransaction();

            }
        });


    }
}
