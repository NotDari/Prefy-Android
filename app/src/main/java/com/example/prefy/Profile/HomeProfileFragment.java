package com.example.prefy.Profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.prefy.Network.RefreshInternet;
import com.example.prefy.Profile.ProfilePostsRec.NewProfilePostsGateway;
import com.example.prefy.Profile.ProfilePostsRec.ProfileRetreiver.WholeProfile;
import com.example.prefy.R;
import com.example.prefy.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;

public class HomeProfileFragment extends Fragment{
    private String username, imageUrl;
    private Boolean viewDestroyed;
    private SwipeRefreshLayout refreshLayout;
    private Boolean DataAlreadySet;
    private TextView accountNameText, ProfileUsername;
    private NewProfilePostsGateway gateway;
    private CurrentUserViewModel viewModel;
    private Long prefCount, voteCount, postCount, idPref;
    private RecyclerView recView;
    private TextView noInternetText;
    private ProgressBar progressBar;
    private RelativeLayout noPostsLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        getViews(view);
        getData(view);
        handleSharedPrefs(view);
        initRefresh(view);
        return view;
    }

    private void getViews(View view){
        refreshLayout = view.findViewById(R.id.ProfileSwipeRefreshlayout);
        recView = view.findViewById(R.id.ProfileRecView);
        progressBar = view.findViewById(R.id.ProfileProgressBar);
        noInternetText = view.findViewById(R.id.ProfileNoInternet);
        noPostsLayout = view.findViewById(R.id.ProfileFragmentNoPosts);
    }


    private void initRefresh(View view){
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!viewDestroyed) {
                    viewModel.refreshData();
                }
            }
        });
    }

    private void handleSharedPrefs(View view){
        if (!DataAlreadySet) {
            viewDestroyed = false;
            Utils utils = new Utils(view.getContext());
            accountNameText = view.findViewById(R.id.AccountNameText);
            ProfileUsername = view.findViewById(R.id.ProfilePageUsername);
            String fullname = utils.loadString(getString(R.string.save_fullname_pref), "");
            username = utils.loadString(getString(R.string.save_username_pref), "");
            imageUrl = utils.loadString(getString(R.string.save_profileP_pref), "");
            postCount = utils.loadLong(getString(R.string.save_postCount_pref), 0);
            voteCount = utils.loadLong(getString(R.string.save_voteCount_pref), 0);
            Boolean verified = utils.loadBoolean(getString(R.string.save_verified_pref), false);
            //String twitter = utils.loadString(getString(R.string.save), "");
            prefCount = utils.loadLong(getString(R.string.save_prefCount_pref), 0);
            idPref = utils.loadLong(getString(R.string.save_user_id), 0);
            User user = new User();
            user.setProfileImageURL(imageUrl);
            user.setUsername(username);
            user.setId(idPref);
            user.setVerified(verified);
            user.setPrefsNumber(prefCount);
            user.setVotesNumber(voteCount);
            user.setPostsNumber(postCount);
            user.setFullname(fullname);
            initGateway(view, user);
            //progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void initGateway(View view, User user){
        gateway = new NewProfilePostsGateway(recView, view, user, true);
        gateway.displayView();
    }



    private void getData(View view){
        DataAlreadySet = false;
        progressBar.setVisibility(View.VISIBLE);
        viewModel = new ViewModelProvider(HomeProfileFragment.this).get(CurrentUserViewModel.class);
        viewModel.init();
        viewModel.getWholeProfileMutable().observe(getViewLifecycleOwner(), new Observer<WholeProfile>() {
            @Override
            public void onChanged(WholeProfile wholeProfile) {
                if (wholeProfile != null){
                    refreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    noInternetText.setVisibility(View.GONE);
                    if (wholeProfile.getPostListContainer().getPostList() != null) {
                        if (wholeProfile.getPostListContainer().getPostList().size() == 0){
                            noPostsLayout.setVisibility(View.VISIBLE);
                        }
                        gateway.setInitPosts(wholeProfile.getPostListContainer().getPostList());
                    }
                    if (gateway.getUser() != null) {
                        if (wholeProfile.getUser() != null) {
                            if (!wholeProfile.getUser().equals(gateway.getUser())) {
                                gateway.updateUserInfo(wholeProfile.getUser());
                            }
                        }
                    }
                    DataAlreadySet = true;
                    //checkPreviousData(wholeProfile);
                }
            }
        });
        viewModel.getInternetAvailable().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (!DataAlreadySet){
                    if (aBoolean != null){
                        refreshLayout.setRefreshing(false);
                        if (!aBoolean){
                            initNoInternet();
                        }
                    }
                }
            }
        });
    }

    private void initNoInternet(){
        progressBar.setVisibility(View.GONE);
        noInternetText.setVisibility(View.VISIBLE);
        noInternetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noInternetText.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                //viewModel.refreshData();
                RefreshInternet.RefreshInternet();
            }
        });
    }


    /**

    private void checkPreviousData(WholeProfile wholeProfile){
        View view = HomeProfileFragment.this.getView();
        Context context = view.getContext();
        Utils utils = new Utils(context);
        if (postCount != null && prefCount != null && voteCount != null){
            Long tempPostsNumber = wholeProfile.getUserInfo().getPostsNumber();
            if (tempPostsNumber != postCount){
                postCount = tempPostsNumber;
                postCounter.setText(postCount.toString());
                utils.saveLong(getString(R.string.save_postCount_pref), postCount);
            }
            Long tempVotesNumber = wholeProfile.getUserInfo().getVotesNumber();
            if (tempVotesNumber != voteCount){
                voteCount = tempVotesNumber;
                voteCounter.setText(voteCount.toString());
                utils.saveLong(getString(R.string.save_voteCount_pref), voteCount);
            }
            Long tempPrefsNumber = wholeProfile.getUserInfo().getPrefsNumber();
            if (tempPrefsNumber != prefCount){
                prefCount = tempPrefsNumber;
                prefCounter.setText(prefCount.toString());
                utils.saveLong(getString(R.string.save_prefCount_pref), prefCount);
            }
        } else {
            postCount = wholeProfile.getUserInfo().getPostsNumber();
            voteCount = wholeProfile.getUserInfo().getVotesNumber();
            prefCount = wholeProfile.getUserInfo().getPrefsNumber();
            postCounter.setText(postCounter.toString());
            voteCounter.setText(voteCounter.toString());
            prefCounter.setText(prefCount.toString());
        }
        if (wholeProfile.getUserInfo().getBio() != null){
            if (!wholeProfile.getUserInfo().getBio().isEmpty()){
                profileBio.setVisibility(View.VISIBLE);
                profileBio.setText(wholeProfile.getUserInfo().getBio());
                utils.saveString(getString(R.string.save_bio_pref),wholeProfile.getUserInfo().getBio());
            }else{
                profileBio.setVisibility(View.GONE);
                utils.saveString(getString(R.string.save_bio_pref), "");
            }
        } else {
            profileBio.setVisibility(View.GONE);
            utils.saveString(getString(R.string.save_bio_pref), "");
        }
        if (wholeProfile.getUserInfo().getInstagram() != null){
            if (!wholeProfile.getUserInfo().getInstagram().isEmpty()){
                instagramButton.setImageTintList(null);
            }else {
                instagramButton.setImageTintList(context.getColorStateList(R.color.grey));
            }
        }else {
            instagramButton.setImageTintList(context.getColorStateList(R.color.grey));
        }
        if (wholeProfile.getUserInfo().getTwitter() != null){
            if (!wholeProfile.getUserInfo().getTwitter().isEmpty()){
                twitterButton.setImageTintList(null);
            }else {
                twitterButton.setImageTintList(context.getColorStateList(R.color.grey));
            }
        }else {
            twitterButton.setImageTintList(context.getColorStateList(R.color.grey));
        }
        if (wholeProfile.getUserInfo().getVk() != null){
            if (!wholeProfile.getUserInfo().getVk().isEmpty()){
                vkButton.setImageTintList(null);
            }else {
                vkButton.setImageTintList(context.getColorStateList(R.color.grey));
            }
        }else {
            vkButton.setImageTintList(context.getColorStateList(R.color.grey));
        }
        if (wholeProfile.getUser().getUsername() != null){
            //if (!wholeProfile.getUser().getUsername().equals())
        }
        if (wholeProfile.getUserInfo().getVerified() != null){
            if (wholeProfile.getUserInfo().getVerified()){
                verifiedImage.setVisibility(View.VISIBLE);
            } else {
                verifiedImage.setVisibility(View.GONE);
            }
            utils.saveBoolean(getString(R.string.save_verified_pref), wholeProfile.getUserInfo().getVerified());
        }
        if (progressBar.getVisibility() == View.VISIBLE){
            progressBar.setVisibility(View.GONE);
        }
        setPosts(wholeProfile);
    }

     */






    @Override
    public void onDestroyView() {
        super.onDestroyView();
        refreshLayout.setOnRefreshListener(null);
        viewDestroyed = true;
    }





}
