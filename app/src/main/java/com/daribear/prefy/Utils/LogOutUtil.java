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
import com.daribear.prefy.Popular.NewPopularRepository;
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

/**
 * Util use to perform all the tasks that need to be done when logging out.
 * This includes clearing glide cache, clearing databases, the consent form and more
 */
public class LogOutUtil {
    //List of all tables to clear
    private static final ArrayList<String> tableList = new ArrayList<>(Arrays.asList("PopularPostsUsers","PopularPostsPopularPosts", "UploadTasks", "UploadVotes", "UploadReports", "UploadActivityClear", "UploadComments", "UploadDeleteTable", "UploadFollowTable"));

    /**
     *
     * @param activity
     */
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

    /**
     * Removes all the shared preferences to prepare for different user.
     * @param context context with which to access the Utils
     */
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

    /**
     * Clear the server admin, so clears the auth toekn and the logged in user to prepare for a new user
     * @param context context with which to work
     */
    private static void clearServerAdmin(Context context){
        ServerAdminSingleton.getInstance().setServerAuthToken(null);
        ServerAdminSingleton.getInstance().alterLoggedInUser(context);
    }

    /**
     * Resets all the view models to prepare for new users.
     * @param context context with which to use
     */
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

    /**
     * Clears all the databases to prepare for a new user.
     * Launches a seperate thread which handles the deletion.
     * @param context context to use
     */
    public static void clearDatabases(Context context){
        SQLiteDatabase db = DatabaseHelper.getInstance(context.getApplicationContext()).getWritableDatabase();
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                db.beginTransaction();
                for (int i = 0; i < tableList.size(); i ++){
                    db.delete(tableList.get(i), null, null);
                }
                InitDatabaseTasks.init(db);
                db.setTransactionSuccessful();
                db.endTransaction();

            }
        });


    }

    /**
     * Clears the crashlytics data
     */
    private static void clearCrashlytics(){
        FirebaseCrashlytics.getInstance().setUserId("");
    }

    /**
     * Changes the activity to the login activity, so a new user can log in.
     * @param activity the activity this was called from
     */
    public static void changeActivity(Activity activity){
        Intent signOutIntent = new Intent(activity, login_activity.class);
        signOutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(signOutIntent);
    }

    /**
     * Changes the activity to the login activity, so a new user can log in.
     * Contains a custom code
     * @param activity the activity this was called from
     * @param customCode the customCode to add to the intent.
     */
    public static void changeActivity(Activity activity, Integer customCode){
        Intent signOutIntent = new Intent(activity, login_activity.class);
        signOutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        signOutIntent.putExtra("customCode", customCode);
        activity.startActivity(signOutIntent);
    }

    /**
     * Clears the glide cache to prepare for the new user.
     * @param context context with which to access the Utils
     */
    public static void clearGlide(Context context){
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Glide.get(context).clearDiskCache();
            }
        });
    }

    /**
     * Resets the consent form to prepare for a new user, which could require one to be shown
     * @param activity activity which this is running in
     */
    public static void resetConsentForm(Activity activity){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ConsentInformation consentInformation = UserMessagingPlatform.getConsentInformation(activity);
                consentInformation.reset();
                AdTracker.getInstance().consentChanged();
                AdConsentForm adConsentForm = new AdConsentForm();
                adConsentForm.checkState(activity);
            }
        });

    }
}
