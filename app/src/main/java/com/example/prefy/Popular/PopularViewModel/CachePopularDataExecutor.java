package com.example.prefy.Popular.PopularViewModel;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.prefy.Application.PrefyApplication;
import com.example.prefy.Database.DatabaseHelper;
import com.example.prefy.Network.CacheContentTools;
import com.example.prefy.Popular.PopularPostSet;

import java.sql.SQLOutput;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class CachePopularDataExecutor {
    private PopularPostSet popularPostSet;
    private Context ApplicationContext;

    private CachePopularDataRetreiverInterface delegate;

    public CachePopularDataExecutor(Context applicationContext, CachePopularDataRetreiverInterface delegate) {
        ApplicationContext = applicationContext;
        this.delegate = delegate;
    }

    public void init(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                popularPostSet = new PopularPostSet();
                DatabaseHelper databaseHelper = DatabaseHelper.getInstance(ApplicationContext);
                SQLiteDatabase db = databaseHelper.getReadableDatabase();

                String popularPostQuery = "SELECT * FROM PopularPostsPopularPosts";
                Cursor popularPostCursor = db.rawQuery(popularPostQuery, null);
                popularPostSet.setPostList(CacheContentTools.getPopularPostList(popularPostCursor));


                String userQuery = "SELECT * FROM PopularPostsUsers";
                Cursor userCursor = db.rawQuery(userQuery, null);
                popularPostSet.setUserList(CacheContentTools.getUserList(userCursor));
                actionComplete();
            }
        });
    }

    private void actionComplete(){
        delegate.completed(true, popularPostSet);
    }
}
