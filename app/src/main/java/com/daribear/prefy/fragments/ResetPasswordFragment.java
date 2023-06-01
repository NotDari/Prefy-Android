package com.daribear.prefy.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.daribear.prefy.R;
import com.daribear.prefy.Utils.ServerAdminSingleton;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ResetPasswordFragment extends Fragment {
    private MaterialButton submitButton, backButton;
    private EditText detailsEditText;

    private Boolean emailLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        initViews(view);
        initButtons(view);
        return view;
    }

    private void initViews(View view){
        submitButton = view.findViewById(R.id.resetPassWordSubmitButton);
        backButton = view.findViewById(R.id.resetPasswordBackButton);
        detailsEditText = view.findViewById(R.id.resetPasswordDetailsEditText);
    }

    private void initButtons(View view){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigateUp();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initSubmit(detailsEditText.getText().toString());
            }
        });
    }

    private void initSubmit(String details){
        emailLoading = false;
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (details != null){
            if (!details.isEmpty()){
                if (!emailLoading) {
                    emailLoading = true;
                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            OkHttpClient okHttpClient = new OkHttpClient();
                            HttpUrl.Builder httpBuilder = HttpUrl.parse(ServerAdminSingleton.getInstance().getServerAddress() + "/prefy/v1/Login/ResetPassword").newBuilder();
                            httpBuilder.addEncodedQueryParameter("login", details);
                            RequestBody body = RequestBody.create(MediaType.parse("application/json"), "");
                            Request request = new Request.Builder()
                                    .url(httpBuilder.build())
                                    .method("POST", body)
                                    .addHeader("Content-Type", "application/json")
                                    .build();
                            try {
                                Response response = okHttpClient.newCall(request).execute();
                                if (response.isSuccessful()) {
                                    if (!isDetached()){
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(ResetPasswordFragment.this.getContext(), "Email Sent", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                } else {
                                    if (!isDetached()){
                                        System.out.println("Sdad failed:" + response.body().string());
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(ResetPasswordFragment.this.getContext(), "Failed to find account", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            } catch (IOException e) {
                                if (!isDetached()){
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ResetPasswordFragment.this.getContext(), "Unknown Error", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }
                    });
                }

            } else {
                Toast.makeText(ResetPasswordFragment.this.getContext(), "Please enter a valid username", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(ResetPasswordFragment.this.getContext(), "Please enter a valid username", Toast.LENGTH_SHORT).show();
        }
    }






}