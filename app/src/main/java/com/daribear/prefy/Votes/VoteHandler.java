package com.daribear.prefy.Votes;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daribear.prefy.Network.UploadController.UploadController;
import com.daribear.prefy.R;
import com.daribear.prefy.customClasses.StandardPost;

import java.lang.ref.WeakReference;
import java.sql.SQLOutput;
import java.util.HashMap;

public class VoteHandler {



    public static void changeImage(StandardPost post, ImageView imageView, RelativeLayout leftImageView, RelativeLayout rightImageView, String viewType){
       View popularItemImageRightClickerBar, popularItemImageLeftClickerBar;
       TextView leftBarText, rightBarText;
       VoteCircleShape leftCircularProgress, rightCircularProgress;
       ImageView leftVoteMark, rightVoteMark;

       if (post != null){
           if (post.getCurrentVote() != null){
               WeakReference<Context> context = new WeakReference<>(imageView.getContext());
               String currentVote = post.getCurrentVote();
               //These are defining the necessary views
               {
                   if (viewType.equals("Popular")) {
                       popularItemImageRightClickerBar = rightImageView.findViewById(R.id.PopularItemImageRightClickerBar);
                       popularItemImageLeftClickerBar = leftImageView.findViewById(R.id.PopularItemImageLeftClickerBar);
                       leftBarText = leftImageView.findViewById(R.id.PopularItemImageLeftClickerText);
                       rightBarText = rightImageView.findViewById(R.id.PopularItemImageRightClickerText);
                       leftCircularProgress = leftImageView.findViewById(R.id.PopularItemImageLeftClickerProgressCircle);
                       rightCircularProgress = rightImageView.findViewById(R.id.PopularItemImageRightClickerProgressCircle);
                       leftVoteMark = leftImageView.findViewById(R.id.PopularItemImageLeftClickerVoteMark);
                       rightVoteMark = rightImageView.findViewById(R.id.PopularItemImageRightClickerVoteMark);
                   } else if (viewType.equals("ExploreDialog")) {
                       popularItemImageRightClickerBar = rightImageView.findViewById(R.id.ExploreDialogItemImageRightClickerBar);
                       popularItemImageLeftClickerBar = leftImageView.findViewById(R.id.ExploreDialogItemImageLeftClickerBar);
                       leftBarText = leftImageView.findViewById(R.id.ExploreDialogItemImageLeftClickerText);
                       rightBarText = rightImageView.findViewById(R.id.ExploreDialogItemImageRightClickerText);
                       leftCircularProgress = leftImageView.findViewById(R.id.ExploreDialogItemImageLeftClickerProgressCircle);
                       rightCircularProgress = rightImageView.findViewById(R.id.ExploreDialogItemImageRightClickerProgressCircle);
                       leftVoteMark = leftImageView.findViewById(R.id.ExploreDialogItemImageLeftClickerVoteMark);
                       rightVoteMark = rightImageView.findViewById(R.id.ExploreDialogItemImageRightClickerVoteMark);
                   } else if (viewType.equals("Categories")){
                       popularItemImageRightClickerBar = rightImageView.findViewById(R.id.ProfilePostListListItemImageRightClickerBar);
                       popularItemImageLeftClickerBar = leftImageView.findViewById(R.id.ProfilePostListListItemImageLeftClickerBar);
                       leftBarText = leftImageView.findViewById(R.id.ProfilePostListListItemImageLeftClickerText);
                       rightBarText = rightImageView.findViewById(R.id.ProfilePostListListItemImageRightClickerText);
                       leftCircularProgress = leftImageView.findViewById(R.id.ProfilePostListListItemImageLeftClickerProgressCircle);
                       rightCircularProgress = rightImageView.findViewById(R.id.ProfilePostListListItemImageRightClickerProgressCircle);
                       leftVoteMark = leftImageView.findViewById(R.id.ProfilePostListListItemImageLeftClickerVoteMark);
                       rightVoteMark = rightImageView.findViewById(R.id.ProfilePostListListItemImageRightClickerVoteMark);
                   } else if (viewType.equals("ProfilePostItem")){
                       popularItemImageRightClickerBar = rightImageView.findViewById(R.id.ProfilePostListItemImageRightClickerBar);
                       popularItemImageLeftClickerBar = leftImageView.findViewById(R.id.ProfilePostListItemImageLeftClickerBar);
                       leftBarText = leftImageView.findViewById(R.id.ProfilePostListItemImageLeftClickerText);
                       rightBarText = rightImageView.findViewById(R.id.ProfilePostListItemImageRightClickerText);
                       leftCircularProgress = leftImageView.findViewById(R.id.ProfilePostListItemImageLeftClickerProgressCircle);
                       rightCircularProgress = rightImageView.findViewById(R.id.ProfilePostListItemImageRightClickerProgressCircle);
                       leftVoteMark = leftImageView.findViewById(R.id.ProfilePostListItemImageLeftClickerVoteMark);
                       rightVoteMark = rightImageView.findViewById(R.id.ProfilePostListItemImageRightClickerVoteMark);
                   } else {
                       popularItemImageRightClickerBar = rightImageView.findViewById(R.id.PopularItemImageRightClickerBar);
                       popularItemImageLeftClickerBar = leftImageView.findViewById(R.id.PopularItemImageLeftClickerBar);
                       leftBarText = leftImageView.findViewById(R.id.PopularItemImageLeftClickerText);
                       rightBarText = rightImageView.findViewById(R.id.PopularItemImageRightClickerText);
                       leftCircularProgress = leftImageView.findViewById(R.id.PopularItemImageLeftClickerProgressCircle);
                       rightCircularProgress = rightImageView.findViewById(R.id.PopularItemImageRightClickerProgressCircle);
                       leftVoteMark = leftImageView.findViewById(R.id.PopularItemImageLeftClickerVoteMark);
                       rightVoteMark = rightImageView.findViewById(R.id.PopularItemImageRightClickerVoteMark);
                   }
               }
               Integer leftVotes = post.getLeftVotes();
               Integer rightVotes = post.getRightVotes();
               Integer allVotes = post.getAllVotes();

               if (!currentVote.equals("none") && !currentVote.equals("skip")){
                   imageView.setColorFilter(context.get().getColor(R.color.black_lowOpacity));
                   Integer leftPercentage = (int) (((double) leftVotes / (double) allVotes) * 100);
                   Integer rightPercentage = (int) (((double) rightVotes / (double) allVotes) * 100);
                   imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
                   {
                       @Override
                       public boolean onPreDraw()
                       {
                           if (imageView.getViewTreeObserver().isAlive())
                               imageView.getViewTreeObserver().removeOnPreDrawListener(this);

                           //This just does the calculations, and gets the bar widths and heights
                           Integer TotalHeightAvailable = (int) (imageView.getHeight() * 0.4);
                           Integer barWidth = (int) (imageView.getWidth() * .03125);
                           Integer progressIndicatorSize = (int) (imageView.getWidth() * .14);
                           Integer leftBarHeight =  ((leftPercentage * TotalHeightAvailable) / 100);
                           Integer rightBarHeight = ((rightPercentage * TotalHeightAvailable) / 100);
                           ViewGroup.LayoutParams rightBarLp = popularItemImageRightClickerBar.getLayoutParams();
                           rightBarLp.height = rightBarHeight;
                           rightBarLp.width = barWidth;
                           ViewGroup.LayoutParams leftBarLp = popularItemImageLeftClickerBar.getLayoutParams();
                           leftBarLp.height = leftBarHeight;
                           leftBarLp.width = barWidth;
                           popularItemImageLeftClickerBar.setLayoutParams(leftBarLp);
                           popularItemImageRightClickerBar.setLayoutParams(rightBarLp);
                           popularItemImageRightClickerBar.setBackground(context.get().getDrawable(R.drawable.popdialog));
                           popularItemImageLeftClickerBar.setBackground(context.get().getDrawable(R.drawable.popdialog));


                           ViewGroup.LayoutParams leftCircleLp = leftCircularProgress.getLayoutParams();
                           leftCircleLp.width = (int) (progressIndicatorSize * 2.2);
                           leftCircleLp.height = (int) (progressIndicatorSize * 2.2);
                           leftCircularProgress.setLayoutParams(leftCircleLp);
                           ViewGroup.LayoutParams rightCircleLp = rightCircularProgress.getLayoutParams();
                           rightCircleLp.width = (int) (progressIndicatorSize * 2.2);
                           rightCircleLp.height = (int) (progressIndicatorSize * 2.2);
                           rightCircularProgress.setLayoutParams(rightCircleLp);
                           rightCircularProgress.setVoteCircleParameters(progressIndicatorSize, rightPercentage);
                           leftCircularProgress.setVoteCircleParameters(progressIndicatorSize, leftPercentage);
                           return true;
                       }
                   });
                   popularItemImageRightClickerBar.setVisibility(View.VISIBLE);
                   popularItemImageLeftClickerBar.setVisibility(View.VISIBLE);
                   leftCircularProgress.setVisibility(View.VISIBLE);
                   rightCircularProgress.setVisibility(View.VISIBLE);
                   leftCircularProgress.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
                   {
                       @Override
                       public boolean onPreDraw()
                       {
                           if (leftCircularProgress.getViewTreeObserver().isAlive())
                               leftCircularProgress.getViewTreeObserver().removeOnPreDrawListener(this);
                            //Set the width and the height will change automatically to fit the aspect ratio
                           Float widthChanger = .8F * leftCircularProgress.getWidth();

                           ViewGroup.LayoutParams leftVoteLp = leftVoteMark.getLayoutParams();
                           ViewGroup.LayoutParams rightVoteLp = leftVoteMark.getLayoutParams();

                           Float heightChanger = widthChanger * 1.263157895F;


                           leftVoteLp.height = heightChanger.intValue();
                           leftVoteLp.width = widthChanger.intValue();


                           rightVoteLp.height = heightChanger.intValue();
                           rightVoteLp.width = widthChanger.intValue();

                           rightVoteMark.setLayoutParams(rightVoteLp);
                           leftVoteMark.setLayoutParams(leftVoteLp);
                           return true;
                       }
                   });
                   //Individual changes depending on whether the vote is left or right
                   Integer leftButtonMainColour;Integer rightButtonMainColour;Integer rightButtonEdgeColour;Integer leftButtonEdgeColour;
                   if (rightVotes > leftVotes){
                       popularItemImageRightClickerBar.setBackgroundTintList(context.get().getColorStateList(R.color.red));
                       popularItemImageLeftClickerBar.setBackgroundTintList(context.get().getColorStateList(R.color.white));
                       leftButtonMainColour = context.get().getColor(R.color.very_transparent_white);
                       leftButtonEdgeColour = context.get().getColor(R.color.kinda_transparent_white);
                       rightButtonMainColour = context.get().getColor(R.color.very_transparent_red);
                       rightButtonEdgeColour = context.get().getColor(R.color.kinda_transparent_red);
                    } else {
                       popularItemImageLeftClickerBar.setBackgroundTintList(context.get().getColorStateList(R.color.red));
                       popularItemImageRightClickerBar.setBackgroundTintList(context.get().getColorStateList(R.color.white));
                       leftButtonMainColour = context.get().getColor(R.color.very_transparent_red);
                       leftButtonEdgeColour = context.get().getColor(R.color.kinda_transparent_red);
                       rightButtonMainColour = context.get().getColor(R.color.very_transparent_white);
                       rightButtonEdgeColour = context.get().getColor(R.color.kinda_transparent_white);
                       //leftCircularProgress.setBackgroundColor(context.get().getColor(R.color.teal_200));
                    }
                   leftCircularProgress.setColours(leftButtonEdgeColour, leftButtonMainColour);
                   rightCircularProgress.setColours(rightButtonEdgeColour, rightButtonMainColour );
                   if (currentVote.equals("right")){
                       rightVoteMark.setVisibility(View.VISIBLE);
                       leftVoteMark.setVisibility(View.GONE);
                   }else if (currentVote.equals("left")){
                       rightVoteMark.setVisibility(View.GONE);
                       leftVoteMark.setVisibility(View.VISIBLE);
                   }

                   leftBarText.setVisibility(View.VISIBLE);
                   rightBarText.setVisibility(View.VISIBLE);
                   leftBarText.setText(leftPercentage.toString() + " %");
                   rightBarText.setText(rightPercentage.toString() + " % ");

               }
           }
       }
   }

    public static void voteSubmitted(StandardPost post, ImageView imageView, RelativeLayout leftImageView, RelativeLayout rightImageView, String sideClicked, String viewType){
        String currentVote = post.getCurrentVote();

        if (currentVote != null){
            if (currentVote.equals("none") || currentVote.equals("skip")){
                startVoteDetails(post, imageView, leftImageView, rightImageView, sideClicked, viewType);
            }
        } else {
            startVoteDetails(post, imageView, leftImageView, rightImageView, sideClicked, viewType);
        }

    }

    private static void startVoteDetails(StandardPost post, ImageView imageView, RelativeLayout leftImageView, RelativeLayout rightImageView, String sideClicked, String viewType){
        WeakReference<Context> context = new WeakReference<>(imageView.getContext());
        View popularItemImageRightClickerBar, popularItemImageLeftClickerBar;
        TextView leftBarText, rightBarText;
        VoteCircleShape leftCircularProgress, rightCircularProgress;
        ImageView leftVoteMark, rightVoteMark;
        {
            if (viewType.equals("Popular")) {
                popularItemImageRightClickerBar = rightImageView.findViewById(R.id.PopularItemImageRightClickerBar);
                popularItemImageLeftClickerBar = leftImageView.findViewById(R.id.PopularItemImageLeftClickerBar);
                leftBarText = leftImageView.findViewById(R.id.PopularItemImageLeftClickerText);
                rightBarText = rightImageView.findViewById(R.id.PopularItemImageRightClickerText);
                leftCircularProgress = leftImageView.findViewById(R.id.PopularItemImageLeftClickerProgressCircle);
                rightCircularProgress = rightImageView.findViewById(R.id.PopularItemImageRightClickerProgressCircle);
                leftVoteMark = leftImageView.findViewById(R.id.PopularItemImageLeftClickerVoteMark);
                rightVoteMark = rightImageView.findViewById(R.id.PopularItemImageRightClickerVoteMark);
            } else if (viewType.equals("ExploreDialog")) {
                popularItemImageRightClickerBar = rightImageView.findViewById(R.id.ExploreDialogItemImageRightClickerBar);
                popularItemImageLeftClickerBar = leftImageView.findViewById(R.id.ExploreDialogItemImageLeftClickerBar);
                leftBarText = leftImageView.findViewById(R.id.ExploreDialogItemImageLeftClickerText);
                rightBarText = rightImageView.findViewById(R.id.ExploreDialogItemImageRightClickerText);
                leftCircularProgress = leftImageView.findViewById(R.id.ExploreDialogItemImageLeftClickerProgressCircle);
                rightCircularProgress = rightImageView.findViewById(R.id.ExploreDialogItemImageRightClickerProgressCircle);
                leftVoteMark = leftImageView.findViewById(R.id.ExploreDialogItemImageLeftClickerVoteMark);
                rightVoteMark = rightImageView.findViewById(R.id.ExploreDialogItemImageRightClickerVoteMark);
            } else if (viewType.equals("Categories")){
                popularItemImageRightClickerBar = rightImageView.findViewById(R.id.ProfilePostListListItemImageRightClickerBar);
                popularItemImageLeftClickerBar = leftImageView.findViewById(R.id.ProfilePostListListItemImageLeftClickerBar);
                leftBarText = leftImageView.findViewById(R.id.ProfilePostListListItemImageLeftClickerText);
                rightBarText = rightImageView.findViewById(R.id.ProfilePostListListItemImageRightClickerText);
                leftCircularProgress = leftImageView.findViewById(R.id.ProfilePostListListItemImageLeftClickerProgressCircle);
                rightCircularProgress = rightImageView.findViewById(R.id.ProfilePostListListItemImageRightClickerProgressCircle);
                leftVoteMark = leftImageView.findViewById(R.id.ProfilePostListListItemImageLeftClickerVoteMark);
                rightVoteMark = rightImageView.findViewById(R.id.ProfilePostListListItemImageRightClickerVoteMark);
            } else if (viewType.equals("ProfilePostItem")){
                popularItemImageRightClickerBar = rightImageView.findViewById(R.id.ProfilePostListItemImageRightClickerBar);
                popularItemImageLeftClickerBar = leftImageView.findViewById(R.id.ProfilePostListItemImageLeftClickerBar);
                leftBarText = leftImageView.findViewById(R.id.ProfilePostListItemImageLeftClickerText);
                rightBarText = rightImageView.findViewById(R.id.ProfilePostListItemImageRightClickerText);
                leftCircularProgress = leftImageView.findViewById(R.id.ProfilePostListItemImageLeftClickerProgressCircle);
                rightCircularProgress = rightImageView.findViewById(R.id.ProfilePostListItemImageRightClickerProgressCircle);
                leftVoteMark = leftImageView.findViewById(R.id.ProfilePostListItemImageLeftClickerVoteMark);
                rightVoteMark = rightImageView.findViewById(R.id.ProfilePostListItemImageRightClickerVoteMark);
            }else {
                popularItemImageRightClickerBar = rightImageView.findViewById(R.id.PopularItemImageRightClickerBar);
                popularItemImageLeftClickerBar = leftImageView.findViewById(R.id.PopularItemImageLeftClickerBar);
                leftBarText = leftImageView.findViewById(R.id.PopularItemImageLeftClickerText);
                rightBarText = rightImageView.findViewById(R.id.PopularItemImageRightClickerText);
                leftCircularProgress = leftImageView.findViewById(R.id.PopularItemImageLeftClickerProgressCircle);
                rightCircularProgress = rightImageView.findViewById(R.id.PopularItemImageRightClickerProgressCircle);
                leftVoteMark = leftImageView.findViewById(R.id.PopularItemImageLeftClickerVoteMark);
                rightVoteMark = rightImageView.findViewById(R.id.PopularItemImageRightClickerVoteMark);
            }
        }
        Integer leftVotes = post.getLeftVotes();
        Integer rightVotes = post.getRightVotes();
        Integer allVotes = post.getAllVotes() + 1;
        String currentVote = post.getCurrentVote();

        if (sideClicked != null){
            if (sideClicked.equals("rightClick")){
                currentVote = "right";
                rightVotes += 1;
                post.setRightVotes(rightVotes);
            } else if (sideClicked.equals("leftClick")){
                currentVote = "left";
                leftVotes += 1;
                post.setLeftVotes(leftVotes);
            } else if (sideClicked.equals("skip")){
                currentVote = "skip";
            }
            post.setCurrentVote(currentVote);
            post.setAllVotes(allVotes);
        }


        imageView.setColorFilter(context.get().getColor(R.color.black_lowOpacity));
        Integer leftPercentage = (int) (((double) leftVotes / (double) allVotes) * 100);
        Integer rightPercentage = (int) (((double) rightVotes / (double) allVotes) * 100);

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(1000);
        popularItemImageLeftClickerBar.setVisibility(View.VISIBLE);
        popularItemImageRightClickerBar.setVisibility(View.VISIBLE);
        popularItemImageRightClickerBar.setBackgroundColor(context.get().getColor(R.color.red));
        popularItemImageRightClickerBar.startAnimation(AnimationUtils.loadAnimation(context.get(),R.anim.bottom_to_top));
        {
            Integer TotalHeightAvailable = (int) (imageView.getHeight() * 0.4);
            Integer barWidth = (int) (imageView.getWidth() * .03125);
            Integer leftBarHeight =  ((leftPercentage * TotalHeightAvailable) / 100);
            Integer rightBarHeight = ((rightPercentage * TotalHeightAvailable) / 100);
            ViewGroup.LayoutParams rightBarLp = popularItemImageRightClickerBar.getLayoutParams();
            rightBarLp.height = rightBarHeight;
            rightBarLp.width = barWidth;
            ViewGroup.LayoutParams leftBarLp = popularItemImageLeftClickerBar.getLayoutParams();
            leftBarLp.height = leftBarHeight;
            leftBarLp.width = barWidth;
            popularItemImageLeftClickerBar.setLayoutParams(leftBarLp);
            popularItemImageRightClickerBar.setLayoutParams(rightBarLp);
            popularItemImageRightClickerBar.setBackground(context.get().getDrawable(R.drawable.popdialog));
            popularItemImageLeftClickerBar.setBackground(context.get().getDrawable(R.drawable.popdialog));
        }


        {
            Integer progressIndicatorSize = (int) (imageView.getWidth() * .14);
            ViewGroup.LayoutParams leftCircleLp = leftCircularProgress.getLayoutParams();
            leftCircleLp.width = (int) (progressIndicatorSize * 2.2);
            leftCircleLp.height = (int) (progressIndicatorSize * 2.2);
            leftCircularProgress.setLayoutParams(leftCircleLp);
            ViewGroup.LayoutParams rightCircleLp = rightCircularProgress.getLayoutParams();
            rightCircleLp.width = (int) (progressIndicatorSize * 2.2);
            rightCircleLp.height = (int) (progressIndicatorSize * 2.2);
            rightCircularProgress.setLayoutParams(rightCircleLp);
            rightCircularProgress.setVoteCircleParameters(progressIndicatorSize, rightPercentage);
            leftCircularProgress.setVoteCircleParameters(progressIndicatorSize, leftPercentage);
            popularItemImageRightClickerBar.setVisibility(View.VISIBLE);
            popularItemImageLeftClickerBar.setVisibility(View.VISIBLE);
            leftCircularProgress.setVisibility(View.VISIBLE);
            rightCircularProgress.setVisibility(View.VISIBLE);
            leftCircularProgress.setColours(context.get().getColor(R.color.kinda_transparent_red), context.get().getColor(R.color.very_transparent_red));
            rightCircularProgress.setColours(context.get().getColor(R.color.kinda_transparent_white), context.get().getColor(R.color.very_transparent_white));


            Float widthChanger = .8F * (int) (int) (imageView.getWidth() * .14);

            ViewGroup.LayoutParams leftVoteLp = leftVoteMark.getLayoutParams();
            ViewGroup.LayoutParams rightVoteLp = leftVoteMark.getLayoutParams();

            Float heightChanger = widthChanger * 1.263157895F;


            leftVoteLp.height = heightChanger.intValue();
            leftVoteLp.width = widthChanger.intValue();


            rightVoteLp.height = heightChanger.intValue();
            rightVoteLp.width = widthChanger.intValue();
            System.out.println("Sdad heightChanger:" + leftCircularProgress.getWidth());

            rightVoteMark.setLayoutParams(rightVoteLp);
            leftVoteMark.setLayoutParams(leftVoteLp);
        }




        Integer leftButtonMainColour;Integer rightButtonMainColour;Integer rightButtonEdgeColour;Integer leftButtonEdgeColour;
        if (rightVotes > leftVotes){
            popularItemImageRightClickerBar.setBackgroundTintList(context.get().getColorStateList(R.color.red));
            popularItemImageLeftClickerBar.setBackgroundTintList(context.get().getColorStateList(R.color.white));
            leftButtonMainColour = context.get().getColor(R.color.very_transparent_white);
            leftButtonEdgeColour = context.get().getColor(R.color.kinda_transparent_white);
            rightButtonMainColour = context.get().getColor(R.color.very_transparent_red);
            rightButtonEdgeColour = context.get().getColor(R.color.kinda_transparent_red);
        } else {
            popularItemImageLeftClickerBar.setBackgroundTintList(context.get().getColorStateList(R.color.red));
            popularItemImageRightClickerBar.setBackgroundTintList(context.get().getColorStateList(R.color.white));
            leftButtonMainColour = context.get().getColor(R.color.very_transparent_red);
            leftButtonEdgeColour = context.get().getColor(R.color.kinda_transparent_red);
            rightButtonMainColour = context.get().getColor(R.color.very_transparent_white);
            rightButtonEdgeColour = context.get().getColor(R.color.kinda_transparent_white);
            //leftCircularProgress.setBackgroundColor(context.get().getColor(R.color.teal_200));
        }

        leftCircularProgress.setColours(leftButtonEdgeColour, leftButtonMainColour);
        rightCircularProgress.setColours(rightButtonEdgeColour, rightButtonMainColour );

        if (currentVote.equals("right")){
            System.out.println("Sdad right");
            rightVoteMark.setVisibility(View.VISIBLE);
            leftVoteMark.setVisibility(View.GONE);
        }else if (currentVote.equals("left")){
            rightVoteMark.setVisibility(View.GONE);
            leftVoteMark.setVisibility(View.VISIBLE);
        }

        leftBarText.setVisibility(View.VISIBLE);
        rightBarText.setVisibility(View.VISIBLE);
        leftBarText.setText(leftPercentage.toString() + " %");
        rightBarText.setText(rightPercentage.toString() + " % ");
        rightBarText.startAnimation(fadeIn);
    }

    public static void numberAnimator(Integer oldNumber, Integer newNumber, TextView textView) {
        ValueAnimator animator = ValueAnimator.ofInt(oldNumber, newNumber);
        animator.setDuration(400);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                textView.setText(animation.getAnimatedValue().toString() + " votes");
            }
        });
        if (oldNumber != newNumber) {
            animator.start();
        }
    }


    public static void saveVote(Context context, Long id, String vote){
        HashMap<String, Object> insertMap = new HashMap<>();
        insertMap.put("PostId", id);
        insertMap.put("Vote", vote);
        UploadController.saveVote(context, insertMap);
    }
}
