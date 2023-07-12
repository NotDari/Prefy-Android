package com.daribear.prefy.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.bumptech.glide.Glide;
import com.daribear.prefy.Activities.login_activity;
import com.daribear.prefy.Activity.ActivityRepository;
import com.daribear.prefy.Ads.AdTracker;
import com.daribear.prefy.Database.DatabaseHelper;
import com.daribear.prefy.Database.InitDatabaseTasks;
import com.daribear.prefy.Explore.ExploreRepository;
import com.daribear.prefy.Popular.NewPopularSystem.NewPopularRepository;
import com.daribear.prefy.Popular.OldPopularSystem.PopularPostsRepository;
import com.daribear.prefy.Profile.CurrentUserRepository;
import com.daribear.prefy.R;
import com.daribear.prefy.Search.SearchRepository;
import com.daribear.prefy.Utils.AdConsentForm.AdConsentForm;
import com.daribear.prefy.Utils.SharedPreferences.Utils;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.UserMessagingPlatform;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;

public class LogOutUtil {
    private static final ArrayList<String> tableList = new ArrayList<>(Arrays.asList("PopularPostsUsers","PopularPostsPopularPosts", "UploadTasks", "UploadVotes", "UploadReports", "UploadActivityClear", "UploadComments", "UploadDeleteTable", "UploadFollowTable"));

    public static void Logout(Activity activity){
        Context context = activity.getApplicationContext();
        clearSharedPrefs(context);
        clearServerAdmin(context);
        resetViewModels(context);
        clearDatabases(context);
        clearCrashlytics();
        clearGlide(context);
        resetConsentForm(activity);
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
        utils.removeSharedPreference(context.getString(R.string.dark_mode_pref));
        utils.removeSharedPreference(context.getString(R.string.auto_scroll_pref));
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
        if (!NewPopularRepository.isInstanceNull()){
            NewPopularRepository.getInstance(context).reset();
        }

    }

    public static void clearDatabases(Context context){
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

    private static void clearCrashlytics(){
        FirebaseCrashlytics.getInstance().setUserId("");
    }

    public static void changeActivity(Activity activity){
        Intent signOutIntent = new Intent(activity, login_activity.class);
        signOutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(signOutIntent);
    }
    public static void changeActivity(Activity activity, Integer customCode){
        Intent signOutIntent = new Intent(activity, login_activity.class);
        signOutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        signOutIntent.putExtra("customCode", customCode);
        activity.startActivity(signOutIntent);
    }

    public static void clearGlide(Context context){
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Glide.get(context).clearDiskCache();
            }
        });
    }

    public static void resetConsentForm(Activity activity){
        ConsentInformation consentInformation = UserMessagingPlatform.getConsentInformation(activity);
        consentInformation.reset();
        AdTracker.getInstance().consentChanged();
        AdConsentForm adConsentForm = new AdConsentForm();
        adConsentForm.checkState(activity);
    }
}
