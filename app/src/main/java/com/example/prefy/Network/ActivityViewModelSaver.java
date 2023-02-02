package com.example.prefy.Network;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.prefy.Database.DatabaseHelper;
import com.example.prefy.Popular.PopularPostSet;
import com.example.prefy.Popular.PopularViewModel.PopViewModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActivityViewModelSaver {
    private Context ApplicationContext;
    private final String popularpostTableName = "PopularPostsPosts";
    private final String popularstandardpostTableName = "PopularPostsStandardPosts";
    private final String popularuserInfoTableName = "PopularPostsUserInfo";
    private final String popularuserTableName = "PopularPostsUsers";

    public ActivityViewModelSaver(Context applicationContext) {
        ApplicationContext = applicationContext;
    }

    public void viewDestroyed(){

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                savePopular();
            }
        });
    }

    private void savePopular(){
        PopViewModel popViewModel = new PopViewModel();
        PopularPostSet popularPostSet = popViewModel.singleDataCheck();
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(ApplicationContext);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        if (popularPostSet!= null) {
            if (popularPostSet.getPostList().size() > 0) {
                //db.execSQL("delete from " + popularpostTableName);
                db.execSQL("delete from " + popularstandardpostTableName);
                db.execSQL("delete from " + popularuserTableName);
                //db.execSQL("delete from " + popularuserInfoTableName);

                Integer limitCounter = 15;
                if (popularPostSet.getPostList().size() < limitCounter) {
                    limitCounter = popularPostSet.getPostList().size();
                }
                for (int i = 0; i < limitCounter; i++) {
                    System.out.println("Sdad hello: " + limitCounter);
                    ContentValues standardPostContent = CacheContentTools.getStandardPostContent(popularPostSet.getPostList().get(i));
                    db.insert(popularstandardpostTableName, null, standardPostContent);


                    ContentValues userContent = CacheContentTools.getUserContent(popularPostSet.getUserList().get(i));
                    db.insert(popularuserTableName, null, userContent);

                }
            }
        }

    }






}
