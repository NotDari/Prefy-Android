package com.daribear.prefy.Popular.NewPopularSystem;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.daribear.prefy.Database.DatabaseHelper;
import com.daribear.prefy.Network.CacheContentTools;
import com.daribear.prefy.Popular.PopularPost;
import com.daribear.prefy.Popular.PopularPostSet;
import com.daribear.prefy.Profile.User;
import com.daribear.prefy.customClasses.StandardPost;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class NewCachePopularDataExecutor {
    private PopularPostSet popularPostSet;

    private ArrayList<Long> avoidList;

    private Context ApplicationContext;

    private NewCachePopularDataRetreiverInterface delegate;

    public NewCachePopularDataExecutor(Context applicationContext, NewCachePopularDataRetreiverInterface delegate) {
        ApplicationContext = applicationContext;
        this.delegate = delegate;
    }

    public void init(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                popularPostSet = new PopularPostSet();
                avoidList = new ArrayList<>();

                DatabaseHelper databaseHelper = DatabaseHelper.getInstance(ApplicationContext);
                SQLiteDatabase db = databaseHelper.getReadableDatabase();

                String popularPostQuery = "SELECT * FROM PopularPostsPopularPosts";
                Cursor popularPostCursor = db.rawQuery(popularPostQuery, null);
                popularPostSet.setPostList(CacheContentTools.getPopularPostList(popularPostCursor));


                String userQuery = "SELECT * FROM PopularPostsUsers";
                Cursor userCursor = db.rawQuery(userQuery, null);
                popularPostSet.setUserList(CacheContentTools.getUserList(userCursor));



                String voteAvoidQuery = "SELECT * FROM UploadVotes";
                Cursor voteCursor = db.rawQuery(voteAvoidQuery, null);
                if (voteCursor.moveToFirst()){
                    for (int i =0; i < voteCursor.getCount(); i++){
                        avoidList.add(voteCursor.getLong(voteCursor.getColumnIndexOrThrow("PostId")));
                        voteCursor.moveToNext();
                    }
                }

                List<PopularPost> found = new ArrayList<>();
                List<User> userFound = new ArrayList<>();
                for(PopularPost post : popularPostSet.getPostList()){
                    System.out.println("Sdad testing:" + post.getPostId() + " aa:" + avoidList);
                    if(avoidList.contains(post.getPostId())){
                        found.add(post);
                        System.out.println("Sdad minimising!");
                        userFound.add(popularPostSet.getUserList().get(popularPostSet.getPostList().indexOf(post)));
                    }
                }
                popularPostSet.getPostList().removeAll(found);
                popularPostSet.getUserList().removeAll(userFound);


                actionComplete();
            }
        });
    }

    private void actionComplete(){
        delegate.completed(true, popularPostSet, avoidList);
    }
}
