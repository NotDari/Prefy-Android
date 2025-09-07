package com.daribear.prefy.Comments;

import com.daribear.prefy.Profile.User;
import com.daribear.prefy.Utils.JsonUtils.CustomJsonMapper;
import com.daribear.prefy.Utils.ErrorChecker;
import com.daribear.prefy.Utils.GetFollowing.FollowingRetrieving;
import com.daribear.prefy.Utils.GetFollowing.GetFollowingDelegate;
import com.daribear.prefy.Utils.GetInternet;
import com.daribear.prefy.Utils.ServerAdminSingleton;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Class to fetch comments for a post
 * Handles network requests, pagination and checking which users are followed
 */
public class getCommentsExecutor implements GetFollowingDelegate {
    private Long postId;
    private CommentRetreiverInterface delegate;
    private ArrayList<User> userList;
    private ArrayList<FullRecComment> fullCommentList;
    private Integer commentCount;
    private Integer currentCommentCount = 0;
    private DocumentSnapshot documentSnapshot;
    private String serverAddress, authToken;


    public getCommentsExecutor(Long postId, CommentRetreiverInterface delegate) {
        this.postId = postId;
        this.delegate = delegate;
    }



    /**
     * Starts a separate thread to fetch comments from the server
     */
    public void initExecutor(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                //Get server info and auth token
                serverAddress = ServerAdminSingleton.getInstance().getServerAddress();
                authToken = ServerAdminSingleton.getInstance().getServerAuthToken();
                OkHttpClient client = new OkHttpClient();
                fullCommentList = new ArrayList<>();

                //Build request URL
                HttpUrl.Builder httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Comments/PostComments").newBuilder();
                httpBuilder.addEncodedQueryParameter("postId", postId.toString());
                httpBuilder.addEncodedQueryParameter("pageNumber", "0");
                Request request = new Request.Builder()
                        .url(httpBuilder.build())
                        .method("GET", null)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", authToken)
                        .build();
                //Check internet availability
                if (GetInternet.isInternetAvailable()) {
                    try {
                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()){
                            String responseString = response.body().string();
                            JSONArray jsonArray = new JSONArray(responseString);
                            //Parse json into FullRecComment objects
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject jsonObjectTemp = jsonArray.getJSONObject(i);
                                FullRecComment fullRecComment = new FullRecComment();
                                fullRecComment.setFullComment(CustomJsonMapper.getFullCommentFromObject(jsonObjectTemp));
                                fullRecComment.setRepliesShown(0);
                                fullRecComment.setMinimised(false);
                                fullCommentList.add(fullRecComment);
                            }
                            getUserFollowing();

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

    /**
     * Check which users are being followed by the logged in user for the retrieved comments
     */
    private void getUserFollowing(){
        if (fullCommentList.size() == 0){
            success();
        } else {
            ArrayList<Long> idList = new ArrayList<>();
            for (int i = 0; i < fullCommentList.size(); i++) {
                idList.add(fullCommentList.get(i).getFullComment().getComment().getUser().getId());
            }

            FollowingRetrieving followingRetrieving = new FollowingRetrieving(idList, this, null);
        }


    }



    /**
     * Called when comments and following info have been successfully fetched
     */
    private void success(){
        delegate.complete(true, false, fullCommentList, 0);
    }

    /**
     * Callback from FollowingRetrieving to update following status of users
     * @param successful whether retrieval was successful
     * @param followList map of user ids to following status
     * @param type type of retrieval
     */
    @Override
    public void completed(Boolean successful, HashMap<Long, Boolean> followList, String type) {
        if (successful) {
            for (Map.Entry<Long, Boolean> entry : followList.entrySet()) {
                Long key = entry.getKey();
                for (int i = 0; i < fullCommentList.size(); i++) {
                    if (Objects.equals(fullCommentList.get(i).getFullComment().getComment().getUser().getId(), key)) {
                        fullCommentList.get(i).getFullComment().getComment().getUser().setFollowing(followList.get(key));
                    }
                }
            }
            success();
        }else {
            delegate.complete(false, false, null, null);
        }
    }
}
