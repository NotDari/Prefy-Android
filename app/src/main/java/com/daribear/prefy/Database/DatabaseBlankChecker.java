package com.daribear.prefy.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.daribear.prefy.Utils.LogOutUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;

public class DatabaseBlankChecker {

    private static final ArrayList<String> tableList = new ArrayList<>(Arrays.asList("PopularPostsUsers","PopularPostsPopularPosts", "UploadVotes", "UploadReports", "UploadActivityClear", "UploadComments", "UploadDeleteTable", "UploadFollowTable"));

    public static void checkDatabases(Context context){
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = DatabaseHelper.getInstance(context.getApplicationContext()).getWritableDatabase();
                db.beginTransaction();
                Boolean data = false;
                for (int i = 0; i < tableList.size(); i ++){
                    Cursor query = db.rawQuery("Select * FROM " + tableList.get(i), null);
                    if (query.getCount() > 0){
                        data = true;
                    }
                    query.close();
                }
                if (data){
                    LogOutUtil.clearDatabases(context);
                }
            }
        });
    }
}
