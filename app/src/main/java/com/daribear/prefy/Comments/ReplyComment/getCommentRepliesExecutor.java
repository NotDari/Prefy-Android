package com.daribear.prefy.Comments.ReplyComment;

import com.daribear.prefy.Comments.Comment;
import com.daribear.prefy.Utils.CustomJsonMapper;
import com.daribear.prefy.Utils.ErrorChecker;
import com.daribear.prefy.Utils.GetInternet;
import com.daribear.prefy.Utils.ServerAdminSingleton;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class getCommentRepliesExecutor {
    private Integer pageNumber;
    private Long commentId;
    private String serverAddress, authToken;
    private ArrayList<Comment> commentList;

    private ReplyDelegate delegate;

    public getCommentRepliesExecutor(Integer pageNumber, Long commentId, ReplyDelegate delegate) {
        this.pageNumber = pageNumber;
        this.commentId = commentId;
        this.delegate = delegate;
    }

    public void initExecutor(){
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                serverAddress = ServerAdminSingleton.getInstance().getServerAddress();
                authToken = ServerAdminSingleton.getInstance().getServerAuthToken();
                OkHttpClient client = new OkHttpClient();
                commentList = new ArrayList<>();
                HttpUrl.Builder httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Comments/GetCommentReplies").newBuilder();
                httpBuilder.addEncodedQueryParameter("commentId", commentId.toString());
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
                                Comment comment = CustomJsonMapper.getCommentFromObject(jsonArray.getJSONObject(i));
                                commentList.add(comment);
                            }
                            delegate.complete(true, commentList);
                        }else {
                            ErrorChecker.checkForStandardError(response);
                            delegate.complete(false, null);
                        }
                    } catch (IOException | JSONException e) {
                        delegate.complete(false, null);
                    }
                }
                else {
                    delegate.complete(false, null);
                }
            }
        });

    }
}
