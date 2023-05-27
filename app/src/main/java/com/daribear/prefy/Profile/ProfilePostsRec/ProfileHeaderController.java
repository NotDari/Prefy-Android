package com.daribear.prefy.Profile.ProfilePostsRec;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daribear.prefy.Network.UploadController.UploadController;
import com.daribear.prefy.Profile.User;
import com.daribear.prefy.R;
import com.daribear.prefy.Utils.ItemAlterer;
import com.daribear.prefy.Utils.ServerAdminSingleton;
import com.daribear.prefy.Utils.Utils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Objects;

public class ProfileHeaderController {
    private View view;
    private User user;
    private Boolean currentUser;
    private ImageButton settingsButton;
    private ImageView backButton, verifiedImage;
    private TextView accountNameText, ProfileUsername;
    private TextView postCounter, followerCounter, followingCounter, profileBio;
    private ImageView twitterButton, vkButton, instagramButton;
    private ProgressBar progressBar;
    private MaterialButton followButton;

    public ProfileHeaderController(User user, Boolean currentUser, View itemview) {
        this.currentUser = currentUser;
        this.view = itemview;
        this.user = user;
        getViews(view);
        initTopBar(view);
        setViews();
    }

    private void getViews(View view){
        settingsButton = view.findViewById(R.id.ProfileSettingsButton);
        backButton = view.findViewById(R.id.ProfileBackButton);
        postCounter = view.findViewById(R.id.ProfilePostsNumber);
        followingCounter = view.findViewById(R.id.ProfileFollowingNumber);
        followerCounter = view.findViewById(R.id.ProfileFollowersNumber);
        profileBio = view.findViewById(R.id.ProfileBio);
        twitterButton = view.findViewById(R.id.ProfileTwitterButton);
        vkButton = view.findViewById(R.id.ProfileVKButton);
        instagramButton = view.findViewById(R.id.ProfileInstagramButton);
        verifiedImage = view.findViewById(R.id.ProfilePageVerifiedImage);
        followButton = view.findViewById(R.id.profileFollowButton);
        if (Objects.equals(user.getId(), ServerAdminSingleton.getInstance().getLoggedInId()) || Objects.equals(user.getId(), -1L)) {
            followButton.setVisibility(View.GONE);
        }
    }

    private void initTopBar(View view){
        if (currentUser) {
            settingsButton.setVisibility(View.VISIBLE);
            settingsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View buttonView) {
                    Navigation.findNavController(view).navigate(R.id.action_global_settingsFragment);
                }
            });
            backButton.setVisibility(View.GONE);
        } else {
            backButton.setVisibility(View.VISIBLE);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.findNavController(backButton).navigateUp();
                }
            });
            settingsButton.setVisibility(View.GONE);
        }
    }

    private void setViews(){
        setUpVerified();
        setUpDetails();
        setUpInfo();
        initFollowing();
    }

    private void setUpVerified(){
        ImageView verifiedImageView = view.findViewById(R.id.ProfilePageVerifiedImage);
        if (user.getVerified()){
            verifiedImageView.setVisibility(View.VISIBLE);
        } else {
            verifiedImageView.setVisibility(View.GONE);
        }
    }

    private void initFollowing(){
        if (user.getFollowing() != null) {
            if (user.getFollowing()) {
                followButton.setText("Following");
                followButton.setBackgroundColor(ContextCompat.getColor(followButton.getContext(), R.color.unfollow_button));
            } else {
                followButton.setText("Follow");
                followButton.setBackgroundColor(ContextCompat.getColor(followButton.getContext(), R.color.follow_button));
            }
        } else {
            followButton.setVisibility(View.GONE);
        }
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.getFollowing()){
                    followButton.setText("Follow");
                    followButton.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.follow_button));
                } else {
                    followButton.setText("Following");
                    followButton.setBackgroundColor(ContextCompat.getColor(followButton.getContext(), R.color.unfollow_button));
                }
                user.setFollowing(!user.getFollowing());
                ItemAlterer.follow(user.getId(), user.getFollowing(), view.getContext().getApplicationContext());
                UploadController.saveFollow(view.getContext().getApplicationContext(), user.getId(), user.getFollowing());
            }
        });
    }


    private void setUpDetails(){
        TextView accountNameText = view.findViewById(R.id.AccountNameText);TextView ProfileUsername = view.findViewById(R.id.ProfilePageUsername);
        accountNameText.setText(user.getFullname());
        String username = user.getUsername();
        ProfileUsername.setText("@" + username);
        String imageUrl = user.getProfileImageURL();
        ShapeableImageView profileImage = view.findViewById(R.id.profileImageView);
        profileBio.setVisibility(View.VISIBLE);
        profileBio.setText(user.getBio());
        Utils utils = new Utils(view.getContext().getApplicationContext());
        if (imageUrl != null) {
            utils.saveString(view.getContext().getString(R.string.save_profileP_pref), imageUrl);
            if (!imageUrl.equals("none")) {
                Glide.with(profileImage)
                        .load(imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .skipMemoryCache(false)
                        .circleCrop()
                        .into(profileImage);
            } else {
                profileImage.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.user_photo));
            }
        }
    }

    private void setUpInfo(){
        postCounter.setVisibility(View.VISIBLE);
        followingCounter.setVisibility(View.VISIBLE);
        followerCounter.setVisibility(View.VISIBLE);
        if ((user.getBio() != null) && (!user.getBio().isEmpty())){
            profileBio.setVisibility(View.VISIBLE);
            profileBio.setText(user.getBio());
        } else {
            profileBio.setVisibility(View.GONE);
        }
        postCounter.setText(user.getPostsNumber().toString());
        followerCounter.setText(user.getFollowerNumber().toString());
        followingCounter.setText(user.getFollowingNumber().toString());
        twitterButton = view.findViewById(R.id.ProfileTwitterButton);
        vkButton = view.findViewById(R.id.ProfileVKButton);
        instagramButton = view.findViewById(R.id.ProfileInstagramButton);
        if (user.getTwitter() != null && !user.getTwitter().isEmpty()){
            twitterButton.setImageDrawable(view.getContext().getDrawable(R.drawable.twitter));
            twitterButton.setImageTintList(null);
            twitterButton.setEnabled(true);
        } else {
            twitterButton.setImageTintList(ContextCompat.getColorStateList(instagramButton.getContext(), R.color.grey));
            twitterButton.setEnabled(false);
        }
        if (user.getInstagram() != null && !user.getInstagram().isEmpty()){
            instagramButton.setImageDrawable(view.getContext().getDrawable(R.drawable.instagram));
            instagramButton.setImageTintList(null);
            instagramButton.setEnabled(true);
        } else {
            instagramButton.setImageTintList(ContextCompat.getColorStateList(instagramButton.getContext(), R.color.grey));
            instagramButton.setEnabled(false);
        }
        if (user.getVk() != null && !user.getVk().isEmpty()){
            vkButton.setImageDrawable(view.getContext().getDrawable(R.drawable.vk));
            vkButton.setImageTintList(null);
            vkButton.setEnabled(true);
        } else {
            vkButton.setImageTintList(ContextCompat.getColorStateList(instagramButton.getContext(), R.color.grey));
            vkButton.setEnabled(false);
        }
        instagramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.instagram.com/" + user.getInstagram();
                goToUrl(instagramButton.getContext(), url);
            }
        });
        twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.twitter.com/" + user.getTwitter();
                goToUrl(twitterButton.getContext(), url);
            }
        });
        vkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.vk.com/" + user.getVk();
                goToUrl(vkButton.getContext(), url);
            }
        });

    }

    private void goToUrl(Context context, String url) {
        try {
            Uri uriUrl = Uri.parse(url);
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
            context.startActivity(launchBrowser);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, " You don't have any browser to open web page", Toast.LENGTH_LONG).show();
        }
    }

    public void updateUserInfo(User user){
        this.user = user;
        setViews();
        if (currentUser) {
            saveUpdatedUserInfo();
        }
    }

    public void saveUpdatedUserInfo(){
        Context ApplicationContext = view.getContext().getApplicationContext();
        Utils utils = new Utils(ApplicationContext);
        utils.saveLong(ApplicationContext.getString(R.string.save_postCount_pref), user.getPostsNumber());
        utils.saveLong(ApplicationContext.getString(R.string.save_voteCount_pref), user.getVotesNumber());
        utils.saveLong(ApplicationContext.getString(R.string.save_prefCount_pref), user.getPrefsNumber());
        utils.saveLong(ApplicationContext.getString(R.string.save_follower_pref), user.getFollowerNumber());
        utils.saveLong(ApplicationContext.getString(R.string.save_following_pref), user.getFollowingNumber());
        utils.saveBoolean(ApplicationContext.getString(R.string.save_verified_pref), user.getVerified());
        utils.saveString(ApplicationContext.getString(R.string.save_fullname_pref), user.getFullname());
        utils.saveString(ApplicationContext.getString(R.string.save_username_pref),user.getUsername());
        utils.saveString(ApplicationContext.getString(R.string.save_bio_pref), user.getBio());
        utils.saveString(ApplicationContext.getString(R.string.save_instagram_pref), user.getInstagram());
        utils.saveString(ApplicationContext.getString(R.string.save_twitter_pref), user.getTwitter());
        utils.saveString(ApplicationContext.getString(R.string.save_vk_pref), user.getVk());

    }




}
