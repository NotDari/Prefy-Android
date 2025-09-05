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
import com.daribear.prefy.Utils.ItemAlterer;
import com.daribear.prefy.customClasses.Posts.StandardPost;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Handles the animation and other UI updates when a user votes on a post.
 * Animates the
 */
public class VoteHandler {
    private static final float BAR_WIDTH_RATIO = .03125f;
    private static final float PROGRESS_SIZE_RATIO = .14f;
    private static final float VOTE_MARK_RATIO = .8f;
    private static final float VOTE_MARK_ASPECT = 1.263157895F;

    /**
     * Helper class for simplifying vote views.
     */
    private static class VoteViews {
        View leftBar, rightBar;
        TextView leftText, rightText;
        VoteCircleShape leftCircle, rightCircle;
        ImageView leftMark, rightMark;

        VoteViews(View leftBar, View rightBar, TextView leftText, TextView rightText,
                  VoteCircleShape leftCircle, VoteCircleShape rightCircle,
                  ImageView leftMark, ImageView rightMark) {
            this.leftBar = leftBar;
            this.rightBar = rightBar;
            this.leftText = leftText;
            this.rightText = rightText;
            this.leftCircle = leftCircle;
            this.rightCircle = rightCircle;
            this.leftMark = leftMark;
            this.rightMark = rightMark;
        }
    }

    /**
     * Helper class to get the necessary vote views regardless of which viewType it is
     * @param leftImageView The left side image view containing vote UI elements
     * @param rightImageView The right side image view containing vote UI elements
     * @param viewType      The type of view context such as  ExploreDialog or Categories
     * @return The Voteviews containing the assigned views.
     */
    private static VoteViews getVoteViews(RelativeLayout leftImageView, RelativeLayout rightImageView, String viewType) {
        int leftBarId, rightBarId, leftTextId, rightTextId, leftCircleId, rightCircleId, leftMarkId, rightMarkId;

        switch (viewType) {
            case "ExploreDialog":
                leftBarId = R.id.ExploreDialogItemImageLeftClickerBar;
                rightBarId = R.id.ExploreDialogItemImageRightClickerBar;
                leftTextId = R.id.ExploreDialogItemImageLeftClickerText;
                rightTextId = R.id.ExploreDialogItemImageRightClickerText;
                leftCircleId = R.id.ExploreDialogItemImageLeftClickerProgressCircle;
                rightCircleId = R.id.ExploreDialogItemImageRightClickerProgressCircle;
                leftMarkId = R.id.ExploreDialogItemImageLeftClickerVoteMark;
                rightMarkId = R.id.ExploreDialogItemImageRightClickerVoteMark;
                break;
            case "Categories":
                leftBarId = R.id.ProfilePostListListItemImageLeftClickerBar;
                rightBarId = R.id.ProfilePostListListItemImageRightClickerBar;
                leftTextId = R.id.ProfilePostListListItemImageLeftClickerText;
                rightTextId = R.id.ProfilePostListListItemImageRightClickerText;
                leftCircleId = R.id.ProfilePostListListItemImageLeftClickerProgressCircle;
                rightCircleId = R.id.ProfilePostListListItemImageRightClickerProgressCircle;
                leftMarkId = R.id.ProfilePostListListItemImageLeftClickerVoteMark;
                rightMarkId = R.id.ProfilePostListListItemImageRightClickerVoteMark;
                break;
            case "ProfilePostItem":
                leftBarId = R.id.ProfilePostListItemImageLeftClickerBar;
                rightBarId = R.id.ProfilePostListItemImageRightClickerBar;
                leftTextId = R.id.ProfilePostListItemImageLeftClickerText;
                rightTextId = R.id.ProfilePostListItemImageRightClickerText;
                leftCircleId = R.id.ProfilePostListItemImageLeftClickerProgressCircle;
                rightCircleId = R.id.ProfilePostListItemImageRightClickerProgressCircle;
                leftMarkId = R.id.ProfilePostListItemImageLeftClickerVoteMark;
                rightMarkId = R.id.ProfilePostListItemImageRightClickerVoteMark;
                break;
            default:
                leftBarId = R.id.PopularItemImageLeftClickerBar;
                rightBarId = R.id.PopularItemImageRightClickerBar;
                leftTextId = R.id.PopularItemImageLeftClickerText;
                rightTextId = R.id.PopularItemImageRightClickerText;
                leftCircleId = R.id.PopularItemImageLeftClickerProgressCircle;
                rightCircleId = R.id.PopularItemImageRightClickerProgressCircle;
                leftMarkId = R.id.PopularItemImageLeftClickerVoteMark;
                rightMarkId = R.id.PopularItemImageRightClickerVoteMark;
                break;
        }
        return new VoteViews(
                leftImageView.findViewById(leftBarId),
                rightImageView.findViewById(rightBarId),
                leftImageView.findViewById(leftTextId),
                rightImageView.findViewById(rightTextId),
                leftImageView.findViewById(leftCircleId),
                rightImageView.findViewById(rightCircleId),
                leftImageView.findViewById(leftMarkId),
                rightImageView.findViewById(rightMarkId)
        );
    }


    /**
     * Change the vote image
     * @param post          The StandardPost  being voted on
     * @param imageView     The main image view of the post
     * @param leftImageView The left side image view containing vote UI elements
     * @param rightImageView The right side image view containing vote UI elements
     * @param viewType      The type of view context such as  ExploreDialog or Categories
     */
    public static void changeImage(StandardPost post, ImageView imageView, RelativeLayout leftImageView, RelativeLayout rightImageView, String viewType) {
        if (post == null || post.getCurrentVote() == null) return;

        WeakReference<Context> context = new WeakReference<>(imageView.getContext());
        VoteViews views = getVoteViews(leftImageView, rightImageView, viewType);
        String currentVote = post.getCurrentVote();
        Integer leftVotes = post.getLeftVotes();
        Integer rightVotes = post.getRightVotes();
        Integer allVotes = post.getAllVotes();

        if (!currentVote.equals("none") && !currentVote.equals("skip")) {
            applyVoteUI(views, imageView, leftVotes, rightVotes, allVotes, context, currentVote);
        }
    }

    /**
     * When a vote is submitted it checks if its not a skip or no vote, if its not it starts the animation.
     *
     * @param post          The StandardPost  being voted on
     * @param imageView     The main image view of the post
     * @param leftImageView The left side image view containing vote UI elements
     * @param rightImageView The right side image view containing vote UI elements
     * @param sideClicked   Indicates which side was clicked (left right or skip)
     * @param viewType      The type of view context such as  ExploreDialog or Categories
     */
    public static void voteSubmitted(StandardPost post, ImageView imageView, RelativeLayout leftImageView, RelativeLayout rightImageView, String sideClicked, String viewType) {
        String currentVote = post.getCurrentVote();
        if (currentVote == null || currentVote.equals("none") || currentVote.equals("skip")) {
            startVoteDetails(post, imageView, leftImageView, rightImageView, sideClicked, viewType);
        }
    }

    /**
     * Alters the posts details with the result of the vote.
     *
     * @param post          The StandardPost  being voted on
     * @param imageView     The main image view of the post
     * @param leftImageView The left side image view containing vote UI elements
     * @param rightImageView The right side image view containing vote UI elements
     * @param sideClicked   Indicates which side was clicked (left right or skip)
     * @param viewType      The type of view context such as  ExploreDialog or Categories
     */
    private static void startVoteDetails(StandardPost post, ImageView imageView, RelativeLayout leftImageView, RelativeLayout rightImageView, String sideClicked, String viewType) {
        WeakReference<Context> context = new WeakReference<>(imageView.getContext());
        VoteViews views = getVoteViews(leftImageView, rightImageView, viewType);

        Integer leftVotes = post.getLeftVotes();
        Integer rightVotes = post.getRightVotes();
        Integer allVotes = post.getAllVotes() + 1;
        String currentVote = post.getCurrentVote();

        if ("rightClick".equals(sideClicked)) {
            currentVote = "right";
            post.setRightVotes(++rightVotes);
        } else if ("leftClick".equals(sideClicked)) {
            currentVote = "left";
            post.setLeftVotes(++leftVotes);
        } else if ("skip".equals(sideClicked)) {
            currentVote = "skip";
        }

        post.setCurrentVote(currentVote);
        post.setAllVotes(allVotes);

        applyVoteUI(views, imageView, leftVotes, rightVotes, allVotes, context, currentVote);
    }

    /**
     * Uses the user's current vote to creat an animation for the vote
     * @param views views to use for the animation
     * @param imageView The main image view of the post
     * @param leftVotes number of left votes
     * @param rightVotes number of right votes
     * @param allVotes total number of votes
     * @param context context to use
     * @param currentVote which side the user voted
     */
    private static void applyVoteUI(VoteViews views, ImageView imageView, int leftVotes, int rightVotes, int allVotes, WeakReference<Context> context, String currentVote) {
        int leftPercentage = (int)(((double) leftVotes / allVotes) * 100);
        int rightPercentage = (int)(((double) rightVotes / allVotes) * 100);

        imageView.setColorFilter(context.get().getColor(R.color.black_lowOpacity));

        // Setup bars and circles
        imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (imageView.getViewTreeObserver().isAlive()) imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                int totalHeight = (int) (imageView.getHeight() * 0.4);
                int barWidth = (int) (imageView.getWidth() * BAR_WIDTH_RATIO);
                int progressSize = (int) (imageView.getWidth() * PROGRESS_SIZE_RATIO);

                setupBar(views.leftBar, leftPercentage, totalHeight, barWidth, context);
                setupBar(views.rightBar, rightPercentage, totalHeight, barWidth, context);

                setupCircle(views.leftCircle, progressSize, leftPercentage);
                setupCircle(views.rightCircle, progressSize, rightPercentage);

                setupVoteMark(views.leftMark, views.leftCircle);
                setupVoteMark(views.rightMark, views.rightCircle);

                applyColors(views, leftVotes, rightVotes, context);
                setVoteMarksVisibility(views, currentVote);

                views.leftText.setVisibility(View.VISIBLE);
                views.rightText.setVisibility(View.VISIBLE);
                views.leftText.setText(leftPercentage + " %");
                views.rightText.setText(rightPercentage + " %");
                views.rightText.startAnimation(new AlphaAnimation(0, 1) {{ setInterpolator(new DecelerateInterpolator()); setDuration(1000); }});

                return true;
            }
        });
    }

    /**
     * Sets up the bar of the vote animation.
     *
     * @param bar the view of the bar
     * @param percentage percentage of the image height to display the bar as
     * @param totalHeight height of the image
     * @param barWidth width of the bar
     * @param context context to use
     */
    private static void setupBar(View bar, int percentage, int totalHeight, int barWidth, WeakReference<Context> context) {
        ViewGroup.LayoutParams lp = bar.getLayoutParams();
        lp.height = (percentage * totalHeight) / 100;
        lp.width = barWidth;
        bar.setLayoutParams(lp);
        bar.setBackground(context.get().getDrawable(R.drawable.popdialog));
        bar.setVisibility(View.VISIBLE);
    }

    /**
     * Sets up the circle for the side of the animation.
     * @param circle circle that has been created
     * @param size size of the image
     * @param percentage percentage of the image to set the circle at
     */
    private static void setupCircle(VoteCircleShape circle, int size, int percentage) {
        ViewGroup.LayoutParams lp = circle.getLayoutParams();
        lp.width = (int) (size * 2.2);
        lp.height = (int) (size * 2.2);
        circle.setLayoutParams(lp);
        circle.setVoteCircleParameters(size, percentage);
        circle.setVisibility(View.VISIBLE);
    }

    /**
     * Displays the vote mark on the screen(the check)
     * @param mark the mark to be applied
     * @param circle the circle the mark will be in
     */
    private static void setupVoteMark(ImageView mark, VoteCircleShape circle) {
        int width = (int) (circle.getWidth() * VOTE_MARK_RATIO);
        int height = (int) (width * VOTE_MARK_ASPECT);
        ViewGroup.LayoutParams lp = mark.getLayoutParams();
        lp.width = width;
        lp.height = height;
        mark.setLayoutParams(lp);
    }

    /**
     * Apply the colours to the views of the votes
     * @param views the views class containing all the views
     * @param leftVotes the number of left votes
     * @param rightVotes the number of right votes
     * @param context the context to use
     */
    private static void applyColors(VoteViews views, int leftVotes, int rightVotes, WeakReference<Context> context) {
        int leftMain, leftEdge, rightMain, rightEdge;

        if (rightVotes > leftVotes) {
            views.rightBar.setBackgroundTintList(context.get().getColorStateList(R.color.red));
            views.leftBar.setBackgroundTintList(context.get().getColorStateList(R.color.white));
            leftMain = context.get().getColor(R.color.very_transparent_white);
            leftEdge = context.get().getColor(R.color.kinda_transparent_white);
            rightMain = context.get().getColor(R.color.very_transparent_red);
            rightEdge = context.get().getColor(R.color.kinda_transparent_red);
        } else {
            views.leftBar.setBackgroundTintList(context.get().getColorStateList(R.color.red));
            views.rightBar.setBackgroundTintList(context.get().getColorStateList(R.color.white));
            leftMain = context.get().getColor(R.color.very_transparent_red);
            leftEdge = context.get().getColor(R.color.kinda_transparent_red);
            rightMain = context.get().getColor(R.color.very_transparent_white);
            rightEdge = context.get().getColor(R.color.kinda_transparent_white);
        }

        views.leftCircle.setColours(leftEdge, leftMain);
        views.rightCircle.setColours(rightEdge, rightMain);
    }

    /**
     * Sets whether the tick in the vote should be displayed
     * @param views views to use
     * @param currentVote what the user voted
     */
    private static void setVoteMarksVisibility(VoteViews views, String currentVote) {
        if ("right".equals(currentVote)) {
            views.rightMark.setVisibility(View.VISIBLE);
            views.leftMark.setVisibility(View.GONE);
        } else if ("left".equals(currentVote)) {
            views.rightMark.setVisibility(View.GONE);
            views.leftMark.setVisibility(View.VISIBLE);
        }
    }

    /**
     * The animation from one vote to another
     * @param oldNumber number to animate from
     * @param newNumber number to animate to
     * @param textView the text view to put the animation in
     */
    public static void numberAnimator(Integer oldNumber, Integer newNumber, TextView textView) {
        ValueAnimator animator = ValueAnimator.ofInt(oldNumber, newNumber);
        animator.setDuration(400);
        animator.addUpdateListener(animation -> textView.setText(animation.getAnimatedValue() + " votes"));
        if (!oldNumber.equals(newNumber)) animator.start();
    }

    /**
     * Saves the vote by sending it to the server
     * @param context context to use
     * @param id id of the post
     * @param vote what the user voted
     * @param type type of view
     */
    public static void saveVote(Context context, Long id, String vote, String type){
        HashMap<String, Object> insertMap = new HashMap<>();
        insertMap.put("PostId", id);
        insertMap.put("Vote", vote);
        UploadController.saveVote(context, insertMap);
        ItemAlterer.itemVote(id, vote, context.getApplicationContext(), type);
    }
}
