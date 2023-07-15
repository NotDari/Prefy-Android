package com.daribear.prefy.Profile;

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

import com.daribear.prefy.Network.RefreshInternet;
import com.daribear.prefy.Profile.ProfilePostsRec.ProfilePostsGateway;
import com.daribear.prefy.Profile.ProfilePostsRec.ProfileRetreiver.WholeProfile;
import com.daribear.prefy.R;
import com.daribear.prefy.Utils.SharedPreferences.Utils;

public class HomeProfileFragment extends Fragment{
    private String username, imageUrl;
    private Boolean viewDestroyed;
    private SwipeRefreshLayout refreshLayout;
    private Boolean DataAlreadySet;
    private TextView accountNameText, ProfileUsername;
    private ProfilePostsGateway gateway;
    private CurrentUserViewModel viewModel;
    private Long prefCount, voteCount, postCount, idPref, followerCount, followingCount;
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
            followerCount = utils.loadLong(getString(R.string.save_follower_pref), 0);
            followingCount = utils.loadLong(getString(R.string.save_following_pref), 0);
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
            user.setFollowerNumber(followerCount);
            user.setFollowingNumber(followingCount);
            user.setFullname(fullname);
            initGateway(view, user);
            //progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void initGateway(View view, User user){
        gateway = new ProfilePostsGateway(getActivity(),recView, view, user, true);
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
                        } else {
                            noPostsLayout.setVisibility(View.GONE);
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
                RefreshInternet.RefreshInternet(getContext());
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        refreshLayout.setOnRefreshListener(null);
        viewDestroyed = true;
    }





}
