package com.daribear.prefy.Settings.Feedback;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daribear.prefy.Profile.GetUserDetailsExecutor;
import com.daribear.prefy.Profile.ProfilePostsRec.ProfileRetreiver.ProfilePostsRetreiver.ProfileExecutor;
import com.daribear.prefy.R;

import com.daribear.prefy.Utils.CurrentTime;
import com.daribear.prefy.Utils.ErrorChecker;
import com.daribear.prefy.Utils.GetFollowing.FollowingRetrieving;
import com.daribear.prefy.Utils.JsonUtils.CustomJsonMapper;
import com.daribear.prefy.Utils.ServerAdminSingleton;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * This is the fragment where the user sends feedback through the submission of a suggestions.
 *
 */
public class FeedbackFragment extends Fragment {
    private ImageView backButton;
    private TextView continueButton;
    private EditText editText;
    private Boolean suggestionLoading;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        getViews(view);
        return view;
    }

    private void getViews(View view){
        backButton = view.findViewById(R.id.FeedbackTopBarBack);

        initExit(view);
        initContinue(view);
    }

    private void initExit(View view){
        Context context = view.getContext();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });
    }

    /**
     * This is the continue button, which creates the suggestions and sends it to the server.
     * @param view baseview
     */
    private void initContinue(View view){
        editText = view.findViewById(R.id.FeedBackEditText);
        continueButton = view.findViewById(R.id.FeedbackContinueButton);
        suggestionLoading = false;
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().isEmpty()){
                    if (!suggestionLoading){
                        suggestionLoading = true;
                        Suggestion suggestion = new Suggestion();
                        suggestion.setCreationDate(((Long)CurrentTime.getCurrentTime()).doubleValue());
                        suggestion.setSuggestionText(editText.getText().toString());
                        suggestion.setUserId(ServerAdminSingleton.getInstance().getLoggedInId());
                        ObjectMapper objectMapper = new ObjectMapper();
                        Executors.newSingleThreadExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    OkHttpClient client = new OkHttpClient();
                                    RequestBody body = RequestBody.create(MediaType.parse("application/json"), objectMapper.writeValueAsString(suggestion));
                                    Request request = new Request.Builder()
                                            .url(ServerAdminSingleton.getInstance().getServerAddress() + "/prefy/v1/Suggestions/Submit" )
                                            .method("POST", body)
                                            .addHeader("Content-Type", "application/json")
                                            .addHeader("Authorization", ServerAdminSingleton.getInstance().getServerAuthToken())
                                            .build();

                                    Response response = client.newCall(request).execute();
                                    if (response.isSuccessful()){
                                        showToast(true);

                                    }else {
                                        ErrorChecker.checkForStandardError(response);
                                        showToast(false);
                                    }
                                } catch (IOException | JSONException e) {
                                    showToast(false);
                                }
                            }
                        });
                    }



                    HashMap<String, Object> feedbackMap = new HashMap<>();
                    feedbackMap.put("id", ServerAdminSingleton.getInstance().getLoggedInId());
                    feedbackMap.put("Feedback", editText.getText().toString());
                    Double time= (double) CurrentTime.getCurrentTime();
                    Double date = time / 1000;
                    Long finalTime = date.longValue();
                    FirebaseFirestore.getInstance().collection("Feedback").document(finalTime.toString()).set(feedbackMap);
                    Toast.makeText(v.getContext(), "Feedback Sent!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(view).navigateUp();
                }else {
                    Toast.makeText(v.getContext(), "Feedback cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Shows the toast confirming whether the feedback was sent.
     * @param success boolean indicating whether it was a success
     */
    private void showToast(Boolean success){
        if (!isDetached()){
            if (getActivity() != null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (success) {
                            Toast.makeText(getContext(), "Sent Feedback!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to send feedback", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                suggestionLoading = false;
            }
        }
    }


}