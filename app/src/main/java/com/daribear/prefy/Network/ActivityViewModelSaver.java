package com.daribear.prefy.Network;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;



import com.daribear.prefy.Database.DatabaseHelper;

import com.daribear.prefy.Popular.NewPopularSystem.NewPopularViewModel;
import com.daribear.prefy.Popular.OldPopularSystem.PopViewModel;
import com.daribear.prefy.Popular.PopularPostSet;
import com.daribear.prefy.Popular.PopularViewModel.PopularModelPackage;
import com.daribear.prefy.customClasses.Converter;
import com.daribear.prefy.customClasses.Posts.PopularPost;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActivityViewModelSaver {
    private Context ApplicationContext;
    private final String popularpostTableName = "PopularPostsPosts";
    private final String popularstandardpostTableName = "PopularPostsPopularPosts";
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
        NewPopularViewModel popViewModel = new NewPopularViewModel();
        popViewModel.init(ApplicationContext);
        PopularModelPackage popularModelPackage = popViewModel.singleDataCheck();
        if (popularModelPackage != null){
            if (popularModelPackage.getPopularPostSet() != null) {
                PopularPostSet popularPostSet = popViewModel.singleDataCheck().getPopularPostSet();
                DatabaseHelper databaseHelper = DatabaseHelper.getInstance(ApplicationContext);
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                if (popularPostSet != null) {
                    if (popularPostSet.getPostList().size() > 0) {
                        //db.execSQL("delete from " + popularpostTableName);
                        db.execSQL("delete from " + popularstandardpostTableName);
                        db.execSQL("delete from " + popularuserTableName);
                        //db.execSQL("delete from " + popularuserInfoTableName);

                        Integer limitCounter = 15;
                        if (popularPostSet.getPostList().size() < limitCounter) {
                            if (popularPostSet.getPostList().size() <= popularPostSet.getUserList().size()) {
                                limitCounter = popularPostSet.getPostList().size();
                            } else {
                                limitCounter = popularPostSet.getUserList().size();
                            }
                        }

                        for (int i = 0; i < limitCounter; i++) {
                            PopularPost popularPost = popularPostSet.getPostList().get(i);
                            if (popularPost.getCurrentVote().equals("none")) {
                                ContentValues standardPostContent = CacheContentTools.getStandardPostContent(popularPost);
                                Cursor countCursor = db.rawQuery("Select * FROM " + popularstandardpostTableName + " WHERE postId = " + popularPost.getPostId(), null);
                                if (countCursor.getCount() <= 0) {
                                    db.insert(popularstandardpostTableName, null, standardPostContent);


                                    ContentValues userContent = CacheContentTools.getUserContent(popularPostSet.getUserList().get(i));
                                    db.insert(popularuserTableName, null, userContent);
                                }

                            }

                        }
                    }
                }
            }
        }


    }






}
