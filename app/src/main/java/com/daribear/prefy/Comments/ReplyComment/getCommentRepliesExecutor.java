package com.daribear.prefy.Comments.ReplyComment;

import com.daribear.prefy.Comments.Comment;
import com.daribear.prefy.Comments.CommentDeleted;
import com.daribear.prefy.Utils.JsonUtils.CustomJsonMapper;
import com.daribear.prefy.Utils.ErrorChecker;
import com.daribear.prefy.Utils.GetFollowing.FollowingRetrieving;
import com.daribear.prefy.Utils.GetFollowing.GetFollowingDelegate;
import com.daribear.prefy.Utils.GetInternet;
import com.daribear.prefy.Utils.ServerAdminSingleton;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class getCommentRepliesExecutor implements GetFollowingDelegate {
    private Integer pageNumber;
    private Long commentId;
    private String serverAddress, authToken;
    private ArrayList<Comment> commentList;

    private ReplyDelegate replyDelegate;

    private CommentDeleted commentDeletedDelegate;

    public getCommentRepliesExecutor(Integer pageNumber, Long commentId, ReplyDelegate replyDelegate) {
        this.pageNumber = pageNumber;
        this.commentId = commentId;
        this.replyDelegate = replyDelegate;
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
                            getUsersFollowing();

                        }else {
                            ErrorChecker.checkForStandardError(response);
                            replyDelegate.complete(false, null);
                        }
                    } catch (IOException | JSONException e) {
                        replyDelegate.complete(false, null);
                    }
                }
                else {
                    replyDelegate.complete(false, null);
                }
            }
        });
    }

    private void getUsersFollowing(){
        if (commentList.size() == 0){
            replyDelegate.complete(true, commentList);
        } else {
            ArrayList<Long> idList = new ArrayList<>();
            for (int i = 0; i < commentList.size(); i++) {
                idList.add(commentList.get(i).getUser().getId());
            }
            FollowingRetrieving followingRetrieving = new FollowingRetrieving(idList, this, null);
        }
    }

    @Override
    public void completed(Boolean successful, HashMap<Long, Boolean> followList, String type) {
        if (successful){
            for (Map.Entry<Long, Boolean> entry : followList.entrySet()) {
                Long key = entry.getKey();
                for (int i = 0; i < commentList.size(); i++) {
                    if (Objects.equals(commentList.get(i).getUser().getId(), key)) {
                        commentList.get(i).getUser().setFollowing(followList.get(key));
                    }
                }
            }
            replyDelegate.complete(true, commentList);
        } else {
            replyDelegate.complete(false, null);
        }
    }
}
