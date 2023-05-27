package com.daribear.prefy.Comments;

import com.daribear.prefy.Profile.User;
import com.daribear.prefy.Utils.CustomJsonMapper;
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




    public void initExecutor(){
        System.out.println("Sdad successHI!");
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
                httpBuilder.addEncodedQueryParameter("pageNumber", "0");
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
                                System.out.println("Sdad commentText:" + fullRecComment.getFullComment().getComment().getText() + " " + fullRecComment.getFullComment().getComment().getCommentId());
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




    private void success(){
        delegate.complete(true, false, fullCommentList, 0);
    }

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
