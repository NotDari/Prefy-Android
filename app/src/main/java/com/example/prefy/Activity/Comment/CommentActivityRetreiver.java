package com.example.prefy.Activity.Comment;

import android.content.res.Resources;

import com.example.prefy.R;
import com.example.prefy.Utils.CustomJsonMapper;
import com.example.prefy.Utils.ServerAdminSingleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CommentActivityRetreiver {
    private commentRetreiverInterface delegate;

    public CommentActivityRetreiver(commentRetreiverInterface delegate) {
        this.delegate = delegate;
    }

    public void initExecutor(){
        String serverAddress = ServerAdminSingleton.getInstance().getServerAddress();
        String authToken = ServerAdminSingleton.getInstance().getServerAuthToken();
        ArrayList<CommentActivity> commentActivityList = new ArrayList<>();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                HttpUrl.Builder httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Activity/CommentsActivity").newBuilder();
                httpBuilder.addEncodedQueryParameter("pageNumber", "0");
                Request request = new Request.Builder()
                        .url(httpBuilder.build())
                        .method("GET", null)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", authToken)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()){
                        delegate.completed(true, CustomJsonMapper.getCommentActivityList(response));

                    }else {
                        requestfailed();
                    }
                } catch (IOException e) {
                    requestfailed();
                }


                /**
                DatabaseReference fDatabase = FirebaseDatabase.getInstance().getReference();
                fDatabase.child("activity").child(uid).child("comments").orderByChild("creationDate").limitToLast(10).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()){
                            for (DataSnapshot dataValues : task.getResult().getChildren()){
                                CommentActivity commentActivity = dataValues.getValue(CommentActivity.class);
                                commentActivityList.add(commentActivity);
                            }
                            Collections.reverse(commentActivityList);
                            getUserDetails(commentActivityList);
                        } else{
                            delegate.completed(false, null);
                        }
                    }
                });
                 */
            }
        });
    }

    /**
    private void getUserDetails(ArrayList<CommentActivity> commentActivityList){
        FirebaseFirestore ff = FirebaseFirestore.getInstance();
        if (commentActivityList != null){
            if (commentActivityList.size() > 0){
                for (int i = 0; i < commentActivityList.size(); i++){
                    int finalI = i;
                    ff.collection("Users").document(commentActivityList.get(i).getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                commentActivityList.get(finalI).setUser(FirebaseUtils.retreiveUser(task.getResult()));

                            } else{
                                delegate.completed(false, null);
                            }
                            if (finalI == commentActivityList.size() - 1){
                                delegate.completed(true, commentActivityList);
                            }
                        }
                    });


                }
            }else {
                delegate.completed(true, new ArrayList<>());
            }
        }


    }
     */

    private void requestfailed(){
        delegate.completed(false, null);
    }
}
