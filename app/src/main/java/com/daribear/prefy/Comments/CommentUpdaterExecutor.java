package com.daribear.prefy.Comments;

import com.daribear.prefy.Profile.User;
import com.daribear.prefy.Utils.CustomJsonMapper;
import com.daribear.prefy.Utils.ErrorChecker;
import com.daribear.prefy.Utils.GetInternet;
import com.daribear.prefy.Utils.ServerAdminSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CommentUpdaterExecutor {
    private Long postId;
    private CommentRetreiverInterface delegate;
    private ArrayList<User> userList;
    private ArrayList<FullRecComment> fullCommentList;
    private Integer commentCount;
    private Integer currentCommentCount = 0;
    private Integer pageNumber;
    private String serverAddress, authToken;

    public CommentUpdaterExecutor(Long postId, CommentRetreiverInterface delegate, Integer pageNumber) {
        this.postId = postId;
        this.delegate = delegate;
        this.pageNumber = pageNumber;
    }


    public void initExecutor(){
        //TODO update to new
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                serverAddress = ServerAdminSingleton.getInstance().getServerAddress();
                authToken = ServerAdminSingleton.getInstance().getServerAuthToken();
                OkHttpClient client = new OkHttpClient();
                fullCommentList = new ArrayList<>();
                HttpUrl.Builder httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Comments/PostComments").newBuilder();
                httpBuilder.addEncodedQueryParameter("postId", postId.toString());
                pageNumber += 1;
                httpBuilder.addEncodedQueryParameter("pageNumber", pageNumber.toString());
                Request request = new Request.Builder()
                        .url(httpBuilder.build())
                        .method("GET", null)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", authToken)
                        .build();
                if (GetInternet.isInternetAvailable()) {
                    try {
                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()){
                            String responseString = response.body().string();
                            JSONArray jsonArray = new JSONArray(responseString);
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject jsonObjectTemp = jsonArray.getJSONObject(i);
                                FullRecComment fullRecComment = new FullRecComment();
                                fullRecComment.setFullComment(CustomJsonMapper.getFullCommentFromObject(jsonObjectTemp));
                                fullRecComment.setRepliesShown(0);
                                fullRecComment.setMinimised(false);
                                fullCommentList.add(fullRecComment);
                            }
                            success();
                        }else {
                            ErrorChecker.checkForStandardError(response);
                            delegate.complete(false, true, new ArrayList<>(), null);
                        }
                    } catch (IOException | JSONException e) {
                        delegate.complete(false, true, new ArrayList<>(), null);
                    }
                }
                else {
                    delegate.complete(false, true ,new ArrayList<>(), null);
                }
            }
        });
    }






    private void success(){
        delegate.complete(true,true, fullCommentList, pageNumber);
    }
}
