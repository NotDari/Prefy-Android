package com.daribear.prefy.Popular.PopularViewModel;

import com.daribear.prefy.Utils.JsonUtils.CustomJsonMapper;
import com.daribear.prefy.Utils.ErrorChecker;
import com.daribear.prefy.Utils.ServerAdminSingleton;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PopularActivityRetreiver {
    private PopularActivityRetrieverInterface delegate;


    public PopularActivityRetreiver(PopularActivityRetrieverInterface delegate) {
        this.delegate = delegate;
    }

    public void init(){
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                HttpUrl.Builder httpBuilder = HttpUrl.parse(ServerAdminSingleton.getInstance().getServerAddress() + "/prefy/v1/Activity/GeneralActivity").newBuilder();
                Request request = new Request.Builder()
                        .url(httpBuilder.build())
                        .method("GET", null)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", ServerAdminSingleton.getInstance().getServerAuthToken())
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()){
                        try {
                            delegate.taskCompleted(true, CustomJsonMapper.getPopularActivityFromResponse(response));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }else {
                        ErrorChecker.checkForStandardError(response);
                        delegate.taskCompleted(false, null);
                    }
                } catch (IOException | JSONException e) {
                    delegate.taskCompleted(false, null);
                }
            }
        });
    }

}
