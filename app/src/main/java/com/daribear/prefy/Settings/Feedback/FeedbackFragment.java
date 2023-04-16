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

import com.daribear.prefy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class FeedbackFragment extends Fragment {
    private ImageView backButton;
    private TextView continueButton;
    private EditText editText;



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

    private void initContinue(View view){
        editText = view.findViewById(R.id.FeedBackEditText);
        continueButton = view.findViewById(R.id.FeedbackContinueButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().isEmpty()){
                    HashMap<String, Object> feedbackMap = new HashMap<>();
                    feedbackMap.put("uid", FirebaseAuth.getInstance().getUid());
                    feedbackMap.put("Feedback", editText.getText().toString());
                    Double time= (double) System.currentTimeMillis();
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


}