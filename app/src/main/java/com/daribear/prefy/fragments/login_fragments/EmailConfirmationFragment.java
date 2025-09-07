package com.daribear.prefy.fragments.login_fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daribear.prefy.Activities.MainActivity;
import com.daribear.prefy.R;
import com.daribear.prefy.Utils.JsonUtils.CustomJsonMapper;
import com.daribear.prefy.Utils.LogOutUtil;
import com.daribear.prefy.Utils.PlayIntegrity.IntegrityDelegate;
import com.daribear.prefy.Utils.PlayIntegrity.IntegrityResponse;
import com.daribear.prefy.Utils.PlayIntegrity.PlayIntegrity;
import com.daribear.prefy.Utils.ServerAdminSingleton;
import com.daribear.prefy.Utils.SharedPreferences.SharedPrefs;
import com.daribear.prefy.customClasses.CustomError;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * The fragment which states that the user needs to confirm their user.
 * Calls the endpoint to send a verification email to the server, and then checks if the user has confirmed it.
 */
public class EmailConfirmationFragment extends Fragment {
    private TextView resendEmail;
    private MaterialButton emailConfirmedButton;
    private Boolean resendEmailButtonActive, emailConfirmedButtonActive;
    private Integer buttonResetCooldownTime;
    private String email, password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_email_confirmation, container, false);
        email = (String) getArguments().get("email");
        password = (String) getArguments().get("password");
        resendEmailButtonActive = false;
        emailConfirmedButtonActive = false;
        buttonResetCooldownTime = 0;
        getViews(view);
        initTasks();
        return view;
    }

    private void getViews(View view){
        resendEmail = view.findViewById(R.id.EmailConfFragResend);
        emailConfirmedButton = view.findViewById(R.id.EmailConfFragConfirmButton);

    }

    private void initTasks(){
        setResendEmail();
        emailConfirmed();
    }
    /**
     * Contact the api to resend the email to the user which will allow them to confirm their email.
     */
    private void setResendEmail(){
        resendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!resendEmailButtonActive){
                    if (buttonResetCooldownTime == 00) {
                        resendEmailButtonActive = true;
                        Countdown();
                        Executors.newSingleThreadExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                OkHttpClient client = new OkHttpClient();
                                HttpUrl.Builder httpBuilder = HttpUrl.parse(ServerAdminSingleton.getInstance().getServerAddress() + "/prefy/v1/Registration/ResendConfirmation").newBuilder();
                                httpBuilder.addEncodedQueryParameter("login", email);
                                Request request = new Request.Builder()
                                        .url(httpBuilder.build())
                                        .method("GET", null)
                                        .addHeader("Content-Type", "application/json")
                                        .build();
                                try {
                                    Response response = client.newCall(request).execute();
                                    if (response.isSuccessful()) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getContext(), "Email sent", Toast.LENGTH_SHORT).show();
                                                resendEmailButtonActive = false;
                                            }
                                        });
                                    } else {
                                        resendEmailButtonActive = false;
                                    }
                                } catch (IOException e) {
                                    resendEmailButtonActive = false;
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), ("Please wait " + buttonResetCooldownTime + " seconds"), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    /**
     * Sets up the click listener for the email confirmation button.
     * When clicked, it prevents multiple clicks and starts the process
     * of verifying the user's email by retrieving a Play Integrity token.
     */
    private void emailConfirmed(){
        emailConfirmedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!emailConfirmedButtonActive){
                    emailConfirmedButtonActive = true;
                    getToken();
                }
            }
        });
    }

    /**
     * Attempts to sign the user in, using the play integrity token.
     * If the user does, saves the auth token and details and goes to the mainactivity
     * Else, allow for the email confirmation to be clicked again.
     * @param token play integrity token
     */
    private void signIn(String token){
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("username", email);
                jsonObject.addProperty("password", password);
                jsonObject.addProperty("token", token);
                RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                Request request = new Request.Builder()
                        .url(ServerAdminSingleton.getInstance().getServerAddress() + "/login")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        emailConfirmedButtonActive = false;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String token = response.header("Authorization");
                                SharedPrefs sharedPrefs = new SharedPrefs(getContext());
                                sharedPrefs.putStringSharedPref(getString(R.string.save_auth_token_pref), token);
                                try {
                                    String responseString = response.body().string();
                                    JSONObject jsonObject = new JSONObject(responseString);
                                    sharedPrefs.putLongSharedPref(getString(R.string.save_user_id), jsonObject.getLong("id"));
                                    ServerAdminSingleton.getInstance().alterLoggedInUser(getContext());
                                } catch (JSONException | IOException e){
                                }
                                PlayIntegrity.getInstance().nullInstance();
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            }
                        });
                    } else {
                        CustomError customError = CustomJsonMapper.getCustomError(response);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (customError.getCustomCode()){
                                    case (3):
                                        Toast.makeText(getContext(), "Email not verified", Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        Toast.makeText(getContext(), "Unknown error", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        });


                        emailConfirmedButtonActive = false;
                    }
                } catch (IOException e) {
                    emailConfirmedButtonActive = false;
                }
            }
        });
    }

    /**
     * Retrieves the Play Integrity token for the user.
     *
     * If the token is not already available, it sets a delegate to be called
     * once the token is obtained. If the token is successfully retrieved,
     * it proceeds to sign in the user. If retrieval fails, a toast message
     * is shown and the email confirmation button is re-enabled.
     */
    private void getToken(){
        PlayIntegrity playIntegrity = PlayIntegrity.getInstance();
        if (playIntegrity.getToken() == null){
            playIntegrity.setIntegrityDelegate(new IntegrityDelegate() {
                @Override
                public void complete(IntegrityResponse integrityResponse) {
                    if (integrityResponse.getSuccess()){
                        signIn(integrityResponse.getToken());
                    } else {
                        playIntegrity.setIntegrityDelegate(null);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(requireActivity(), "Couldn't connect to server", Toast.LENGTH_LONG).show();
                                    emailConfirmedButtonActive = false;
                                }
                            });
                        }
                    }
            });
        } else {
            signIn(playIntegrity.getToken());
        }
    }


    private void Countdown(){
        buttonResetCooldownTime = 45;
        new CountDownTimer((buttonResetCooldownTime * 1000) , 1000) {

            public void onTick(long millisUntilFinished) {
                buttonResetCooldownTime -=1;
            }

            public void onFinish() {
            }

        }.start();
    }


}