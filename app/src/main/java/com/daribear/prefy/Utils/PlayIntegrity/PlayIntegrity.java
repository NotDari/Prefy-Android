package com.daribear.prefy.Utils.PlayIntegrity;

import android.content.Context;
import android.os.SystemClock;

import androidx.annotation.NonNull;

import com.daribear.prefy.Utils.GetInternet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.integrity.IntegrityManager;
import com.google.android.play.core.integrity.IntegrityManagerFactory;
import com.google.android.play.core.integrity.IntegrityServiceException;
import com.google.android.play.core.integrity.IntegrityTokenRequest;
import com.google.android.play.core.integrity.IntegrityTokenResponse;

import java.security.SecureRandom;
import java.util.concurrent.Executors;

import lombok.Getter;
import lombok.Setter;

public class PlayIntegrity {
    String nonce;
    @Getter
    String token;
    public static PlayIntegrity instance;
    @Setter
    private IntegrityDelegate integrityDelegate;
    private IntegrityManager integrityManager;

    public PlayIntegrity() {
        nonce = generateNonce();
    }

    public static PlayIntegrity getInstance() {
        if (instance == null){
            instance = new PlayIntegrity();
        }
        return instance;
    }

    public void getResponse(Context appContext){
        integrityManager = IntegrityManagerFactory.create(appContext);
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                sendRequest();
            }
        });

    }

    private void sendRequest(){
        if (GetInternet.isInternetAvailable()) {
            Task<IntegrityTokenResponse> integrityTokenResponse = integrityManager.requestIntegrityToken(IntegrityTokenRequest.builder().setNonce(nonce).setCloudProjectNumber(453170257829L).build());
            integrityTokenResponse.addOnSuccessListener(new OnSuccessListener<IntegrityTokenResponse>() {
                @Override
                public void onSuccess(IntegrityTokenResponse integrityTokenResponse) {
                    if (integrityDelegate != null) {
                        integrityDelegate.complete(new IntegrityResponse(true, integrityTokenResponse.token()));
                    }
                    token = integrityTokenResponse.token();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (integrityDelegate != null) {
                        integrityDelegate.complete(new IntegrityResponse(false, null));
                    } else {
                        sendRequest();
                    }
                }
            });
        } else {
            if (integrityDelegate != null){
                integrityDelegate.complete(new IntegrityResponse(false, null));
            }
            pauseTimer();
        }
    }

    private String generateNonce(){
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 256; i++)
        {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }

        return sb.toString();
    }

    private void pauseTimer(){
        SystemClock.sleep(250);
        sendRequest();
    }

    public void nullInstance(){
        instance = null;
    }
}
