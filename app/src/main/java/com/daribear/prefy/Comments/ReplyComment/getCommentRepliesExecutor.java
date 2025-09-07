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

/**
 * Class that retrieves the replies to a comment for a specific page.
 * Parses the json received from the backend into a list of comments.
 */
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

    /**
     * Creates a thread which retrieves the replies to the comment with the associated page number + 1
     */
    public void initExecutor(){
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                // get server info and auth token
                serverAddress = ServerAdminSingleton.getInstance().getServerAddress();
                authToken = ServerAdminSingleton.getInstance().getServerAuthToken();

                OkHttpClient client = new OkHttpClient();
                commentList = new ArrayList<>();

                // build request url with comment id and page number
                HttpUrl.Builder httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Comments/GetCommentReplies").newBuilder();
                httpBuilder.addEncodedQueryParameter("commentId", commentId.toString());
                pageNumber += 1;
                httpBuilder.addEncodedQueryParameter("pageNumber", pageNumber.toString());

                // build request
                Request request = new Request.Builder()
                        .url(httpBuilder.build())
                        .method("GET", null)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", authToken)
                        .build();
                // check internet
                if (GetInternet.isInternetAvailable()) {
                    try {
                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()){
                            // parse JSON response into Comment objects
                            String responseString = response.body().string();
                            JSONArray jsonArray = new JSONArray(responseString);
                            for (int i = 0; i < jsonArray.length(); i++){
                                Comment comment = CustomJsonMapper.getCommentFromObject(jsonArray.getJSONObject(i));
                                commentList.add(comment);
                            }
                            getUsersFollowing(); // check follow status
                        }
                        // notify delegate fail
                        else {
                            ErrorChecker.checkForStandardError(response);
                            replyDelegate.complete(false, null);
                        }
                    }
                    // notify delegate fail on exception
                    catch (IOException | JSONException e) {
                        replyDelegate.complete(false, null);
                    }
                }
                // no internet
                else {
                    replyDelegate.complete(false, null);
                }
            }
        });
    }

    /**
     * check which of the users from replies are being followed by the logged in user
     * if reply count is 0 we don't need to check
     */
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

    /**
     * callback from FollowingRetrieving
     * updates each comment user object with whether the logged in suer follows them
     * @param successful request succeeded
     * @param followList map of userIds to following
     * @param type type of request
     */
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
