package com.example.prefy.Popular;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.prefy.Network.UploadController.UploadController;
import com.example.prefy.Popular.PopularViewModel.PopViewModel;
import com.example.prefy.PostDropDownDialog;
import com.example.prefy.Profile.User;
import com.example.prefy.R;
import com.example.prefy.Utils.Utils;
import com.example.prefy.Utils.dateSinceSystem;
import com.example.prefy.Votes.VoteImageHandler;
import com.example.prefy.customClasses.FullPost;
import com.example.prefy.customClasses.StandardPost;

import java.util.HashMap;


public class PopularPageFragment extends Fragment{
    private User user;
    private StandardPost post;
    private Boolean autoScroll;
    private PopularPostVote delegate;
    private RelativeLayout leftCLick;
    private RelativeLayout rightClick;
    private ImageView mainImage;
    private TextView totalVotesTextView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popular_page, container, false);
        getData(view);
        initAutoScroll(view);
        initViews(view);
        initVotingSystem();
        return view;
    }

    private void getData(View view){
        Bundle args = getArguments();
        user = args.getParcelable("user");
        post = args.getParcelable("post");
    }

    public void initViews(View view){
        ((TextView) view.findViewById(R.id.PopularItemQuestionText))
                .setText((post.getQuestion()));
        ((TextView) view.findViewById(R.id.PopularItemUsername))
                .setText((user.getUsername()));
        totalVotesTextView = view.findViewById(R.id.PopularItemTotalVotes);
        totalVotesTextView.setText((post.getAllVotes() + " votes"));
        TextView postDateText =((TextView) view.findViewById(R.id.PopularItemPostDate));
        postDateText.setText((dateSinceSystem.getTimeSince(post.getCreationDate())));
        mainImage = view.findViewById(R.id.PopularItemQuestionImage);
        ImageView profileImage = view.findViewById(R.id.PopularItemUserImage);
        if (user.getProfileImageURL() != null ) {
            if (!user.getProfileImageURL().isEmpty() && !user.getProfileImageURL().equals("none")){
                Glide.with(profileImage)
                        .load(user.getProfileImageURL())
                        .circleCrop()
                        .into(profileImage);
            } else {
                Glide.with(profileImage)
                        .load(R.drawable.user_photo)
                        .circleCrop()
                        .into(profileImage);
            }
        } else {
            Glide.with(profileImage)
                    .load(R.drawable.user_photo)
                    .circleCrop()
                    .into(profileImage);
        }
        Glide.with(mainImage)
                .load(post.getImageURL())
                .into(mainImage);

        TextView commentsTextView = view.findViewById(R.id.PopularItemCommentsButton);
        commentsTextView.setText(post.getCommentsNumber().toString());
        commentsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("post", post);
                bundle.putParcelable("user", user);
                Navigation.findNavController(view).navigate(R.id.action_global_commentsFragment, bundle);
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                profileClick(view);
            }
        });
        postDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                profileClick(view);
            }
        });

        ImageView optionsButton = view.findViewById(R.id.PopularItemOptionsButton);
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FullPost fullPost = new FullPost();
                fullPost.setUser(user);
                fullPost.setStandardPost(post);
                PostDropDownDialog dialog = new PostDropDownDialog(view.getContext(), false, getActivity(),fullPost);
                int[] point = new int[2];
                view.getLocationOnScreen(point); // or getLocationInWindow(point)
                int x = point[0];
                int y = point[1];
                Integer bottomNavHeight = getActivity().findViewById(R.id.BottomNav).getHeight();
                dialog.setImageDrawable(mainImage.getDrawable());

                dialog.setCoordinates(0, bottomNavHeight);

                //Rect rectf = new Rect();
                //optionsButton.getGlobalVisibleRect(rectf);
                //dialog.setCoordinates(rectf.right, rectf.top);
                dialog.initDialog();
            }
        });
        rightClick = view.findViewById(R.id.PopularItemImageRightClicker);
        leftCLick = view.findViewById(R.id.PopularItemImageLeftClicker);

    }


    private void initVotingSystem(){
        VoteImageHandler.changeImage(post, mainImage, leftCLick, rightClick, "Popular");

        PopViewModel popViewModel = new PopViewModel();
        popViewModel.init(getContext().getApplicationContext());
        for (int i = 0; i < popViewModel.getPostData().getValue().getPostList().size(); i++){
            StandardPost newPost = popViewModel.getPostData().getValue().getPostList().get(i);
        }

        rightClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (post.getCurrentVote() == "none") {
                    VoteImageHandler.voteSubmitted(post, mainImage, leftCLick, rightClick, "rightClick", "Popular");
                    autoScrollQuestion();
                    HashMap<String, Object> insertMap = new HashMap<>();
                    insertMap.put("PostId", post.getPostId());
                    insertMap.put("Vote", "right");
                    UploadController.saveVote(view.getContext().getApplicationContext(), insertMap);
                    numberAnimator(post.getAllVotes());
                }
            }
        });
        leftCLick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (post.getCurrentVote() == "none") {
                    VoteImageHandler.voteSubmitted(post, mainImage, leftCLick, rightClick, "leftClick", "Popular");
                    autoScrollQuestion();
                    HashMap<String, Object> insertMap = new HashMap<>();
                    insertMap.put("PostId", post.getPostId());
                    insertMap.put("Vote", "left");
                    UploadController.saveVote(view.getContext().getApplicationContext(), insertMap);
                    numberAnimator(post.getAllVotes());
                }
            }
        });
    }

    private void numberAnimator(Integer newNumber) {
        String text = totalVotesTextView.getText().toString().split(" ")[0];
        Integer oldNumber = Integer.parseInt(text);
        System.out.println("Sdad oldNumber:" + oldNumber);
        ValueAnimator animator = ValueAnimator.ofInt(oldNumber, newNumber);
        animator.setDuration(400);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                totalVotesTextView.setText(animation.getAnimatedValue().toString() + " votes");
            }
        });
        if (oldNumber != newNumber) {
            animator.start();
        }
    }



    private void profileClick(View view){
        Bundle bundle = new Bundle();
        bundle.putLong("id", post.getUserId());
        bundle.putParcelable("user", user);
        Navigation.findNavController(view).navigate(R.id.action_global_userProfile, bundle);
    }

    private void initAutoScroll(View view){
        Context context = view.getContext();
        Utils utils = new Utils(context);
        if (utils.loadBoolean(context.getString(R.string.auto_scroll_pref), false)){
            autoScroll = true;
        } else {
            autoScroll = false;
        }
    }

    private void autoScrollQuestion(){
        if (getParentFragment() != null && getParentFragment() instanceof PopularPostVote) {
            delegate = (PopularPostVote) getParentFragment();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    delegate.voted(autoScroll);
                }
            }, 300);

        }
    }


}