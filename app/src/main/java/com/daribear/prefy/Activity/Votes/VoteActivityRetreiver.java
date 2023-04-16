package com.daribear.prefy.Activity.Votes;

import com.daribear.prefy.Utils.CustomJsonCreator;
import com.daribear.prefy.Utils.CustomJsonMapper;
import com.daribear.prefy.Utils.DefaultCreator;
import com.daribear.prefy.Utils.ErrorChecker;
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

public class VoteActivityRetreiver {
    private voteActivityRetreiverInterface delegate;
    private ArrayList<VoteActivity> voteActivityList;
    private Boolean userRetrieved, postRetrieved;
    private ArrayList<VoteActivity> removePostList;
    private Integer postCounter, userDetailsCounter;
    private String serverAddress, authToken;

    public VoteActivityRetreiver(voteActivityRetreiverInterface delegate) {
        this.delegate = delegate;
    }

    public void initExecutor(){
        voteActivityList = new ArrayList<>();
        userRetrieved = false;
        postRetrieved = false;
        serverAddress = ServerAdminSingleton.getInstance().getServerAddress();
        authToken = ServerAdminSingleton.getInstance().getServerAuthToken();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                HttpUrl.Builder httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Activity/VotesActivity").newBuilder();
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
                        voteActivityList = CustomJsonMapper.getPartialVoteActivityList(response);
                        if (voteActivityList.size() > 0) {
                            getUserDetails();
                            getPostDetails();
                        } else {
                            delegate.votecompleted(true, new ArrayList<>());
                        }

                    }else {
                        ErrorChecker.checkForStandardError(response);
                        requestfailed();
                    }
                } catch (IOException | JSONException e) {
                    requestfailed();
                }
            }
        });
    }

    private void getUserDetails(){
        if (voteActivityList != null){
            ArrayList<Long> userList = new ArrayList<>();
            for (VoteActivity voteActivity : voteActivityList){
                userList.add(voteActivity.getLastUserId());
            }
            if (userList.size() > 0) {
                OkHttpClient client = new OkHttpClient();
                HttpUrl.Builder httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Users/GetUserByIdList").newBuilder();
                httpBuilder.addEncodedQueryParameter("idList", CustomJsonCreator.createArrayStringFromLong(userList));
                Request request = new Request.Builder()
                        .url(httpBuilder.build())
                        .method("GET", null)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", authToken)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        try {
                            JSONArray jsonArray = new JSONArray(response.body().string());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                if (!jsonArray.isNull(i)) {
                                    JSONObject tempObject = jsonArray.getJSONObject(i);
                                    if (tempObject != null) {
                                        voteActivityList.get(i).setUser(CustomJsonMapper.getUserFromObject(tempObject));
                                    }
                                } else {
                                    voteActivityList.get(i).setUser(DefaultCreator.createBlankUser());
                                }
                            }
                            userRetrieved = true;
                            completeSuccess();
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        ErrorChecker.checkForStandardError(response);
                        requestfailed();
                    }
                } catch (IOException | JSONException e) {
                    requestfailed();
                }
            } else {
                userRetrieved = true;
                completeSuccess();
            }

        }
    }
    private void getPostDetails(){
        removePostList = new ArrayList<>();
        postCounter = 0;
        ArrayList<Long> idList = new ArrayList<>();
        for (VoteActivity voteActivity : voteActivityList){
            idList.add(voteActivity.getPostKey());
        }
        if (idList.size() > 0) {
            OkHttpClient client = new OkHttpClient();
            HttpUrl.Builder httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Posts/PostListByIdList").newBuilder();
            httpBuilder.addEncodedQueryParameter("idList", CustomJsonCreator.createArrayStringFromLong(idList));
            Request request = new Request.Builder()
                    .url(httpBuilder.build())
                    .method("GET", null)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", authToken)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject tempObject = jsonArray.getJSONObject(i);
                            if (tempObject == null) {
                                removePostList.add(voteActivityList.get(i));
                            } else {
                                voteActivityList.get(i).setPost(CustomJsonMapper.getPostFromObject(tempObject));

                            }
                        }
                        postRetrieved = true;
                        completeSuccess();
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    ErrorChecker.checkForStandardError(response);
                    requestfailed();
                }
            } catch (IOException | JSONException e) {
                requestfailed();
            }
        } else {
            postRetrieved = true;
            completeSuccess();
        }

    }
    /**

    public void initExecutor(){
        voteActivityList = new ArrayList<>();
        userRetrieved = false;
        postRetrieved = false;
        ff = FirebaseFirestore.getInstance();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                DatabaseReference fDatabase = FirebaseDatabase.getInstance().getReference();

                fDatabase.child("activity").child(uid).child("votes").orderByChild("postCreationDate").limitToLast(10).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()){
                            for (DataSnapshot dataValues : task.getResult().getChildren()){
                                VoteActivity voteActivity = dataValues.getValue(VoteActivity.class);
                                voteActivity.setPostKey(dataValues.getKey());
                                voteActivityList.add(voteActivity);
                            }
                            Collections.reverse(voteActivityList);
                            getUserDetails();
                            getPostDetails();
                        } else{
                            delegate.votecompleted(false, null);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Sdad task failed: onFailureListener" + e);
                    }
                });
            }
        });
    }


    private void getUserDetails(){
        userDetailsCounter = 0;
        if (voteActivityList != null){
            if (voteActivityList.size() > 0){
                System.out.println("Sdad voteActivitySize:" + voteActivityList.size());
                for (int i = 0; i < voteActivityList.size(); i++){
                    int finalI = i;
                    ff.collection("Users").document(voteActivityList.get(i).getLastUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                if (task.getResult().exists()) {
                                    System.out.println("Sdad voteActivitySizeLater:" + voteActivityList.size());
                                    voteActivityList.get(finalI).setUser(FirebaseUtils.retreiveUser(task.getResult()));
                                }
                            } else{
                                delegate.votecompleted(false, null);
                            }
                            userDetailsCounter += 1;
                            if (userDetailsCounter == voteActivityList.size()){
                                System.out.println("Sdad done");
                                userRetrieved = true;
                                completeSuccess();
                                //delegate.completed(true, voteActivityList);
                            }
                        }
                    });

                }
            } else {
                delegate.votecompleted(true, new ArrayList<>());
            }
        }


    }

    private void getPostDetails(){
        removePostList = new ArrayList<>();
        postCounter = 0;
        for (int i = 0; i < voteActivityList.size(); i++){
            int finalI = i;
            ff.collection("Posts").document(voteActivityList.get(i).getPostKey()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        if (task.getResult().exists()) {

                            voteActivityList.get(finalI).setPost(FirebaseUtils.retreiveStandardPost(task.getResult()));
                            postCounter += 1;
                        } else {
                            removePostList.add(voteActivityList.get(finalI));
                            postCounter += 1;
                        }

                    } else{
                        delegate.votecompleted(false, null);
                    }
                    if (postCounter == voteActivityList.size()){
                        postRetrieved = true;
                        completeSuccess();

                    }
                }
            });


        }
    }

     */

    private void requestfailed(){
        delegate.votecompleted(false, null);
    }

    private void completeSuccess(){
        System.out.println("Sdad completeSuccess:" + postRetrieved + userRetrieved);
        if (postRetrieved && userRetrieved){
            if (removePostList != null){
                for (VoteActivity voteActivity : removePostList ){
                    voteActivityList.remove(voteActivity);
                }
            }
            delegate.votecompleted(true, voteActivityList);
        }
    }
}
