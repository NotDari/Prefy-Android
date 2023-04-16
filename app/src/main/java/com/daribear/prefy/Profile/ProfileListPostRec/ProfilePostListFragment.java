package com.daribear.prefy.Profile.ProfileListPostRec;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daribear.prefy.R;
import com.daribear.prefy.customClasses.StandardPost;

import java.util.ArrayList;


public class ProfilePostListFragment extends Fragment {
    private String username;
    private String profileImageLink;
    private ArrayList<StandardPost> postList;
    private Integer positionCLicked;
    private ProfilePostListGateway gateway;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_post_list, container, false);
        getData();
        initRecView(view);
        getViews(view);
        return view;
    }

    private void getData(){
        /**
        postList = new ArrayList<>();
        this.username = ProfilePostListFragmentArgs.fromBundle(getArguments()).getUsername();
        this.profileImageLink = ProfilePostListFragmentArgs.fromBundle(getArguments()).getProfileImageLink();
        StandardPost[] postArray = ProfilePostListFragmentArgs.fromBundle(getArguments()).getPostList();
        for (int i = 0; i < postArray.length; i ++){
            postList.add(postArray[i]);
        }
        this.positionCLicked = ProfilePostListFragmentArgs.fromBundle(getArguments()).getPositionClicked();
         */
    }

    private void initRecView(View view){
        gateway = new ProfilePostListGateway(postList, R.id.ProfilePostListRecView, view, view.getContext(), positionCLicked, username, profileImageLink,getActivity());
        gateway.displayView();
    }

    private void getViews(View view){
        ImageView backButton = view.findViewById(R.id.ProfilePostListTopBarBack);
        TextView usernameText = view.findViewById(R.id.ProfilePostListTopBarUsername);
        usernameText.setText(username);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                Navigation.findNavController(view).navigateUp();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        gateway.destroyView();
    }
}