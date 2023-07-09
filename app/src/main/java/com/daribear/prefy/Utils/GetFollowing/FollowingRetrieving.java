package com.daribear.prefy.Utils.GetFollowing;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.daribear.prefy.Utils.JsonUtils.CustomJsonCreator;
import com.daribear.prefy.Utils.ErrorChecker;
import com.daribear.prefy.Utils.ServerAdminSingleton;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FollowingRetrieving {
    private ArrayList<Long> userIdList;
    private GetFollowingDelegate delegate;

    private String type;

    private HashMap<Long, Boolean> followMap;

    public FollowingRetrieving(ArrayList<Long> userIdList, GetFollowingDelegate delegate, String type) {
        this.userIdList = userIdList;
        this.delegate = delegate;
        this.type = type;
        getFollowing();
    }


    private void getFollowing(){
        System.out.println("Sdad followingStarted");
        ArrayList<Long> smallerList = new ArrayList<>();
        for (int i = 0;i < userIdList.size(); i ++){
            if (!smallerList.contains(userIdList.get(i))){
                smallerList.add(userIdList.get(i));
            }
        }
        String serverAddress = ServerAdminSingleton.getInstance().getServerAddress();
        String authToken = ServerAdminSingleton.getInstance().getServerAuthToken();
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Follows/GetFollowing").newBuilder();
        httpBuilder.addEncodedQueryParameter("userId", ServerAdminSingleton.getInstance().getLoggedInId().toString());
        httpBuilder.addEncodedQueryParameter("followerList", CustomJsonCreator.createArrayStringFromLong(smallerList));
        Request request = new Request.Builder()
                .url(httpBuilder.build().url())
                .method("GET", null)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", authToken)
                .build();

        try {
            Response response = client.newCall(request).execute();
            System.out.println("Sdad followingtried" + response.isSuccessful());
            if (response.isSuccessful()){
                try {
                    HashMap followsList = new ObjectMapper().readValue(response.body().string(), HashMap.class);

                    followMap = new HashMap<Long, Boolean>();
                    Iterator hmIterator = followsList.entrySet().iterator();
                    while (hmIterator.hasNext()) {
                        Map.Entry mapElement = (Map.Entry)hmIterator.next();
                        Long key = -1L;
                        Boolean value = null;
                        if (mapElement.getKey() instanceof String){
                            key = Long.parseLong((String) mapElement.getKey());
                        } else if (mapElement.getKey() instanceof Long){
                            key = (Long) mapElement.getKey();
                        }
                        if (mapElement.getValue() instanceof String){
                            value = Boolean.parseBoolean(( String) mapElement.getValue());
                        } else if (mapElement.getValue() instanceof Boolean){
                            value = (Boolean) mapElement.getValue();
                        }
                        try {
                            followMap.put(key, value);
                        } catch (NullPointerException e){
                            System.out.println("Sdad NULL:" + e);
                        }


                    }
                    getDatabase();
                } catch (NullPointerException e){
                    System.out.println("Sdad following exception:" + e);
                    delegate.completed(false, null, type);
                }





            }else {

                ErrorChecker.checkForStandardError(response);

                delegate.completed(false, null, type);
            }
        } catch (IOException | JSONException e) {
            delegate.completed(false, null, type);
        }
    }

    private void getDatabase(){
        SQLiteDatabase database = ServerAdminSingleton.getInstance().getSqLiteDatabase();
        if (database != null){
            Iterator hmIterator = followMap.entrySet().iterator();
            while (hmIterator.hasNext()) {
                Map.Entry<Long, Boolean> mapElement = (Map.Entry<Long, Boolean>) hmIterator.next();
                Long key = mapElement.getKey();
                Boolean value = mapElement.getValue();
                Cursor retrieveCursor = database.rawQuery("SELECT * FROM " + "UploadFollowTable" +
                        " WHERE " + "FollowingUserId" + " = ?", new String[] {key.toString()});
                if (retrieveCursor.moveToFirst()){
                    Boolean databaseValue = (retrieveCursor.getInt(retrieveCursor.getColumnIndexOrThrow("Follow"))) == 1;
                    if (databaseValue != value){
                        followMap.put(key, databaseValue);
                    }
                }



            }
            complete();
        } else {
            complete();
        }

    }


    private void complete(){
        System.out.println("Sdad followMap:" + followMap);
        delegate.completed(true, followMap, type);
    }
}
