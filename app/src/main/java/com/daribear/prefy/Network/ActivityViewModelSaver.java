package com.daribear.prefy.Network;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;



import com.daribear.prefy.Database.DatabaseHelper;

import com.daribear.prefy.Popular.NewPopularViewModel;
import com.daribear.prefy.Popular.PopularPostSet;
import com.daribear.prefy.Popular.PopularViewModel.PopularModelPackage;
import com.daribear.prefy.customClasses.Posts.FullPost;
import com.daribear.prefy.customClasses.Posts.PopularPost;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A helper class which saves data from the repositories into the database, to be cached for when the app is next opened.
 */
public class ActivityViewModelSaver {
    private Context ApplicationContext;
    private final String popularpostTableName = "PopularPostsPosts";
    private final String popularstandardpostTableName = "PopularPostsPopularPosts";
    private final String popularuserInfoTableName = "PopularPostsUserInfo";
    private final String popularuserTableName = "PopularPostsUsers";

    public ActivityViewModelSaver(Context applicationContext) {
        ApplicationContext = applicationContext;
    }

    /**
     * Gets called when the view is destroyed.
     * Calls the savePopulkar function() which saves the data from the popular repositorty/view model into the database.
     */
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
            if (popularModelPackage.getFullPostList() != null) {
                ArrayList<FullPost> fullPostList = popularModelPackage.getFullPostList();
                DatabaseHelper databaseHelper = DatabaseHelper.getInstance(ApplicationContext);
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                if (fullPostList != null) {
                    if (fullPostList.size() > 0) {
                        //db.execSQL("delete from " + popularpostTableName);
                        db.execSQL("delete from " + popularstandardpostTableName);
                        db.execSQL("delete from " + popularuserTableName);
                        //db.execSQL("delete from " + popularuserInfoTableName);

                        Integer limitCounter = 15;
                        if (fullPostList.size() < limitCounter) {
                            if (fullPostList.size() <= fullPostList.size()) {
                                limitCounter = fullPostList.size();
                            } else {
                                limitCounter = fullPostList.size();
                            }
                        }

                        for (int i = 0; i < limitCounter; i++) {
                            FullPost fullPost = fullPostList.get(i);
                            if (fullPost.getStandardPost().getCurrentVote().equals("none")) {
                                ContentValues standardPostContent = CacheContentTools.getStandardPostContent(fullPost.getStandardPost());
                                Cursor countCursor = db.rawQuery("Select * FROM " + popularstandardpostTableName + " WHERE postId = " + fullPost.getStandardPost().getPostId(), null);
                                if (countCursor.getCount() <= 0) {
                                    db.beginTransaction();
                                    db.insert(popularstandardpostTableName, null, standardPostContent);


                                    ContentValues userContent = CacheContentTools.getUserContent(fullPost.getUser());
                                    db.insert(popularuserTableName, null, userContent);
                                    db.setTransactionSuccessful();
                                    db.endTransaction();
                                }
                                countCursor.close();

                            }

                        }
                    }
                }
            }
        }


    }






}
