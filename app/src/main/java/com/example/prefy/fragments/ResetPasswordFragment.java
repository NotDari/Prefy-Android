package com.example.prefy.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.prefy.Explore.FeaturedPost;
import com.example.prefy.Explore.FullFeaturedPost;
import com.example.prefy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ResetPasswordFragment extends Fragment {
    private MaterialButton submitButton, backButton;
    private EditText detailsEditText;

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
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (details != null){
            if (!details.isEmpty()){
                if (details.contains("@")){
                    auth.sendPasswordResetEmail(details)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ResetPasswordFragment.this.getContext(), "Email Sent", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ResetPasswordFragment.this.getContext(), "Failed to find account", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else {
                    DatabaseReference fDatabase = FirebaseDatabase.getInstance().getReference();
                    fDatabase.child("authentication").child(details).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()){
                                String email = task.getResult().getValue().toString();
                                auth.sendPasswordResetEmail(email)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(ResetPasswordFragment.this.getContext(), "Email Sent", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(ResetPasswordFragment.this.getContext(), "Failed to find account" + email, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(ResetPasswordFragment.this.getContext(), "Failed to find account", Toast.LENGTH_SHORT).show();
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