package com.daribear.prefy.Settings.DetailsEdit;

import android.content.Context;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daribear.prefy.R;

import java.util.ArrayList;


public class DetailsEditGatewayFragment extends Fragment {
    private ImageView backButton, saveButton;
    private TextView titleText;
    private LinearLayout linLayout;
    private ArrayList<String> itemList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details_edit_gateway, container, false);
        getViews(view);
        getDetails(view);
        return view;
    }


    private void getViews(View view){
        this.backButton = view.findViewById(R.id.DetailsEditGatewayTopBarBack);
        this.titleText = view.findViewById(R.id.DetailsEditGatewayTopBarTitle);
        linLayout = view.findViewById(R.id.DetailsEditGatewayLinLay);
        initBack();
    }

    private void initBack(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigateUp();
            }
        });
    }

    private void getDetails(View view){
        String details = this.getArguments().getString("detail");
        String titleTextS;
        String[] temporaryArray;
        itemList = new ArrayList<>();
        switch (details){
            case "Personal":
                titleTextS = "Personal Details";
                temporaryArray = view.getContext().getApplicationContext().getResources().getStringArray(R.array.personal_details);
                break;
            default:
            case "SocialMedia":
                titleTextS = "Social Media";
                temporaryArray = view.getContext().getApplicationContext().getResources().getStringArray(R.array.social_media);
                break;
            case "EmailPassword":
                titleTextS = "Email & Password";
                temporaryArray = view.getContext().getApplicationContext().getResources().getStringArray(R.array.email_password);
                break;
        }
        for (String s : temporaryArray){
            itemList.add(s);
        }
        titleText.setText(titleTextS);
        createViews(view);
    }

    private void createViews(View view){
        Context appContext = view.getContext().getApplicationContext();
        for (String s : itemList){
            Button button = (Button) LayoutInflater.from(view.getContext()).inflate(R.layout.edit_details_button, null);
            button.setText(s);
            button.setBackgroundTintList(ContextCompat.getColorStateList(appContext,R.color.fully_transparent));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!DetailsEditGatewayFragment.this.getArguments().getString("detail").equals("EmailPassword")) {
                        Bundle bundle = new Bundle();
                        bundle.putString("detail", s);
                        Navigation.findNavController(v).navigate(R.id.action_detailsEditGatewayFragment_to_detailsEditFragment, bundle);
                    } else {
                        Toast.makeText(appContext, "Not yet implemented", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            linLayout.addView(button);
        }

    }


}