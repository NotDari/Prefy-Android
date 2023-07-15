package com.daribear.prefy.Popular;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.daribear.prefy.Database.DatabaseHelper;
import com.daribear.prefy.Network.CacheContentTools;
import com.daribear.prefy.customClasses.Posts.FullPost;
import com.daribear.prefy.customClasses.Posts.PopularPost;
import com.daribear.prefy.Profile.User;
import com.daribear.prefy.customClasses.Posts.StandardPost;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class NewCachePopularDataExecutor {
    private ArrayList<FullPost> fullPostList;

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
                fullPostList = new ArrayList<>();
                avoidList = new ArrayList<>();

                DatabaseHelper databaseHelper = DatabaseHelper.getInstance(ApplicationContext);
                SQLiteDatabase db = databaseHelper.getReadableDatabase();

                String popularPostQuery = "SELECT * FROM PopularPostsPopularPosts";
                Cursor popularPostCursor = db.rawQuery(popularPostQuery, null);
                ArrayList<PopularPost> postList = CacheContentTools.getPopularPostList(popularPostCursor);
                for (int i =0; i< postList.size(); i++){
                    FullPost fullPost = new FullPost();
                    fullPost.setStandardPost(postList.get(i));
                    fullPostList.add(fullPost);
                }
                popularPostCursor.close();


                String userQuery = "SELECT * FROM PopularPostsUsers";
                Cursor userCursor = db.rawQuery(userQuery, null);
                ArrayList<User> userList = CacheContentTools.getUserList(userCursor);
                for (int i =0; i < userList.size(); i++){
                    for (int f = 0; f < fullPostList.size(); f++){
                        if (userList.get(i).getId().equals(fullPostList.get(f).getStandardPost().getUserId())){
                            fullPostList.get(f).setUser(userList.get(i));
                        }
                    }
                }
                userCursor.close();



                String voteAvoidQuery = "SELECT * FROM UploadVotes";
                Cursor voteCursor = db.rawQuery(voteAvoidQuery, null);
                if (voteCursor.moveToFirst()){
                    for (int i =0; i < voteCursor.getCount(); i++){
                        avoidList.add(voteCursor.getLong(voteCursor.getColumnIndexOrThrow("PostId")));
                        voteCursor.moveToNext();
                    }
                }
                voteCursor.close();


                Iterator<FullPost> itr = fullPostList.iterator();
                while (itr.hasNext()) {
                    FullPost fullPost = itr.next();
                    if (avoidList.contains(fullPost.getStandardPost().getPostId())){
                        itr.remove();
                    }
                }


                actionComplete();
            }
        });
    }

    private void actionComplete(){
        delegate.completed(true, fullPostList, avoidList);
    }
}
