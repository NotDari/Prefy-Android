package com.daribear.prefy.Profile.ProfileListPostRec.New;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daribear.prefy.PostDropDownDialog;
import com.daribear.prefy.Profile.User;
import com.daribear.prefy.R;
import com.daribear.prefy.Utils.ServerAdminSingleton;
import com.daribear.prefy.Utils.GeneralUtils.dateSinceSystem;
import com.daribear.prefy.Votes.VoteHandler;
import com.daribear.prefy.customClasses.Posts.FullPost;
import com.daribear.prefy.customClasses.Posts.StandardPost;

public class ProfilePostListItemFragment extends Fragment {
    private User user;
    private StandardPost post;
    private ImageView userImage, backButton, postImage;
    private TextView userName, postDate, totalVoteCount, question, commentsButtonText;
    private ImageButton optionsButton;

    private RelativeLayout leftClick, rightClick;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_post_list_item, container, false);
        this.user = this.getArguments().getParcelable("user");
        this.post = this.getArguments().getParcelable("post");
        setUpViews(view);
        return view;
    }


    private void initVote(){
        rightClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VoteHandler.voteSubmitted(post, postImage, leftClick, rightClick, "rightClick", "ProfilePostItem");
                VoteHandler.saveVote(view.getContext().getApplicationContext(),post.getPostId(), "right");
                String text = totalVoteCount.getText().toString().split(" ")[0];
                Integer oldNumber = Integer.parseInt(text);
                VoteHandler.numberAnimator(oldNumber, post.getAllVotes() ,totalVoteCount);
            }
        });
        leftClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VoteHandler.voteSubmitted(post, postImage, leftClick, rightClick, "leftClick", "ProfilePostItem");
                VoteHandler.saveVote(view.getContext().getApplicationContext(),post.getPostId(), "left");
                String text = totalVoteCount.getText().toString().split(" ")[0];
                Integer oldNumber = Integer.parseInt(text);
                VoteHandler.numberAnimator(oldNumber, post.getAllVotes() ,totalVoteCount);
            }
        });
    }


    private void setUpViews(View view){
        {
            userImage = view.findViewById(R.id.ProfilePostListItemFragmentUserImage);
            userName = view.findViewById(R.id.ProfilePostListItemFragmentUsername);
            postDate = view.findViewById(R.id.ProfilePostListItemFragmentPostDate);
            totalVoteCount = view.findViewById(R.id.ProfilePostListItemFragmentTotalVotes);
            backButton = view.findViewById(R.id.ProfilePostListItemFragmentBackBarBack);
            question = view.findViewById(R.id.ProfilePostListItemFragmentQuestionText);
            postImage = view.findViewById(R.id.ProfilePostListItemFragmentQuestionImage);
            commentsButtonText = view.findViewById(R.id.ProfilePostListItemFragmentCommentsButton);
            optionsButton = view.findViewById(R.id.ProfilePostListItemFragmentMoreButton);
            leftClick = view.findViewById(R.id.ProfilePostListItemImageLeftClicker);
            rightClick = view.findViewById(R.id.ProfilePostListItemImageRightClicker);
        }

        VoteHandler.changeImage(post,postImage ,leftClick, rightClick, "ProfilePostItem");
        userName.setText(user.getUsername());
        postDate.setText(dateSinceSystem.getTimeSince(post.getCreationDate()));
        totalVoteCount.setText(post.getAllVotes().toString() + " votes");
        question.setText(post.getQuestion());
        commentsButtonText.setText(post.getCommentsNumber().toString());

        initGlide();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(backButton).navigateUp();
            }
        });
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FullPost fullPost = new FullPost();
                fullPost.setUser(user);
                fullPost.setStandardPost(post);
                Boolean loggedUserPost = user.getId().equals(ServerAdminSingleton.getCurrentUser(getContext().getApplicationContext()).getId());
                PostDropDownDialog dialog = new PostDropDownDialog(view.getContext(), loggedUserPost, getActivity(),fullPost, null, null);
                dialog.setImageDrawable(postImage.getDrawable());

                dialog.initDialog();
            }
        });
        commentsButtonText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("post", post);
                bundle.putParcelable("user", user);
                Navigation.findNavController(view).navigate(R.id.action_global_commentsFragment, bundle);
            }
        });
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", user);
                Navigation.findNavController(view).navigate(R.id.action_global_userProfile, bundle);
            }
        });
        initVote();
    }

    private void initGlide(){
        if (user.getProfileImageURL() != null) {
            if (!user.getProfileImageURL().equals("none")) {
                Glide
                        .with(userImage)
                        .load(user.getProfileImageURL())
                        .circleCrop()
                        .into(userImage);
            } else {
                defaultImage();
            }
        } else {
            defaultImage();
        }
        if (post.getImageURL() != null){
            Glide.with(postImage)
                    .load(post.getImageURL())
                    .into(postImage);
        }
    }

    private void defaultImage(){
        Glide
                .with(userImage)
                .load(R.drawable.user_photo)
                .circleCrop()
                .into(userImage);
    }
}