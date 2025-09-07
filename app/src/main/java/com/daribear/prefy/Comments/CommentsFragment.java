package com.daribear.prefy.Comments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daribear.prefy.Network.UploadController.UploadController;
import com.daribear.prefy.Profile.User;
import com.daribear.prefy.R;
import com.daribear.prefy.Utils.CurrentTime;
import com.daribear.prefy.Utils.ServerAdminSingleton;
import com.daribear.prefy.Utils.SharedPreferences.Utils;
import com.daribear.prefy.Utils.GeneralUtils.dateSinceSystem;
import com.daribear.prefy.customClasses.Posts.StandardPost;
import com.google.android.material.bottomnavigation.BottomNavigationView;


/**
 * Fragment to display comments for a post
 * Handles submission, replies, and displaying comment list
 */
public class CommentsFragment extends Fragment implements CommentReplyClicked{
    private handleCommentsRecView commentsRecView;
    private User user;
    private StandardPost post;
    private Boolean replyActive;
    private String replyUsername;
    private Long replyId, subParentId;
    private ConstraintLayout replyLay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        initBackSystem(view);
        getData(view);
        initReplyLay(view);
        return view;
    }

    /**
     * Initialise the back button system and input resize
     * @param view base view of fragment
     */
    private void initBackSystem(View view){
        ImageView backButton = view.findViewById(R.id.CommentsTopBarBack);
        // Navigate up the stack on back pressed
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });
        alterBottomNavVisibility(true);
        resizeSubmitCommentEditText(view);
    }

    /**
     * Show or hide bottom navigation
     * @param hide whether or not to hide
     */
    private void alterBottomNavVisibility(Boolean hide){
        BottomNavigationView bottomNavigation = getActivity().findViewById(R.id.BottomNav);
        if (hide){
            bottomNavigation.setVisibility(View.GONE);
        }else {
            bottomNavigation.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Resize comment input EditText based on screen size
     * @param view base view
     */
    private void resizeSubmitCommentEditText(View view){
        AppCompatEditText submitCommentEdit = view.findViewById(R.id.CommentEditText);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        Integer newheight = (int)((int)(displayMetrics.heightPixels * 0.047) * .95);
        if (submitCommentEdit.getLayoutParams().height < newheight){
            submitCommentEdit.getLayoutParams().height = newheight;
        }
        submitComment(submitCommentEdit, view);
    }

    /**
     * Initialise the submit comment button
     * @param submitCommentEdit EditText for comment input
     * @param view base view
     */
    private void submitComment(AppCompatEditText submitCommentEdit, View view){
        replyActive = false;
        ImageButton submitButton = view.findViewById(R.id.CommentSendButton);
        Utils utils = new Utils(view.getContext());
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Comment comment = new Comment();
                String text = submitCommentEdit.getText().toString();
                if (!text.isEmpty()){
                    Double time= (double) CurrentTime.getCurrentTime();
                    Double date = time / 1000;


                    comment.setText(text);

                    comment.setReplyID(replyId);
                    comment.setReplyUsername(replyUsername);
                    comment.setSubReplyID(subParentId);
                    comment.setCreationDate(date);
                    comment.setPostId(post.getPostId());
                    comment.setUserId(ServerAdminSingleton.getInstance().getLoggedInId());



                    UploadController.saveComment(getActivity().getApplicationContext(), comment);
                    commentsRecView.commentSubmitted(comment);
                    removeReplyLay();
                    submitCommentEdit.getText().clear();



                } else {
                    Toast.makeText(view.getContext(), "Comment can't be empty", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    /**
     * Get data passed to fragment and initialise comment list
     * @param view base view
     */
    private void getData(View view){
        Context context = view.getContext();
        User user = this.getArguments().getParcelable("user");
        StandardPost post = this.getArguments().getParcelable("post");
        this.post = post;
        this.user = user;
        //initViews(view);
        commentsRecView = new handleCommentsRecView(view, user, post, getActivity(), this);
        commentsRecView.initSetup();
    }



    /**
     * Initialise reply layout for showing reply state
     * @param view base view
     */
    private void initReplyLay(View view){
        this.replyLay = view.findViewById(R.id.CommentReplyConsLay);
        ImageButton closeButton = view.findViewById(R.id.CommentsReplyLayClose);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        replyLay.getLayoutParams().height = ((int) (screenHeight * .045));
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeReplyLay();
            }
        });
    }
    /**
     * Set reply layout visible and show which user is being replied to
     * @param replyUsername username being replied to
     * @param replyId id of comment being replied to
     * @param subParentID id of sub-parent comment (for nested replies)
     */
    private void setReplyLay(String replyUsername, Long replyId, Long subParentID){
        replyLay.setVisibility(View.VISIBLE);
        replyActive = true;

        this.replyId = replyId;
        this.replyUsername = replyUsername;
        this.subParentId = subParentID;
        TextView textView = replyLay.findViewById(R.id.CommentsReplyLayText);
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append("Reply : ");
        SpannableString replySpannable= new SpannableString(replyUsername);
        replySpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, replyUsername.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        replySpannable.setSpan(new ForegroundColorSpan(replyLay.getContext().getColor(R.color.text_color)), 0, replyUsername.length(), 0);
        builder.append(replySpannable);
        textView.setText(builder);
    }

    /**
     * Hide reply layout and reset reply state
     */
    private void removeReplyLay(){
        replyActive = false;
        replyId = null;
        replyUsername = null;
        subParentId = null;
        replyLay.setVisibility(View.GONE);
    }

    /**
     * Reset bottom navigation visibility and destroy comment list on fragment destroy
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        alterBottomNavVisibility(false);
        commentsRecView.viewDestroyed();
    }



    /**
     * Handle main comment reply click from callback
     * @param replyUsername username of comment being replied to
     * @param replyId id of comment being replied to
     */
    @Override
    public void mainReplyClicked(String replyUsername, Long replyId) {
        replyActive = true;
        setReplyLay(replyUsername, replyId, null);
    }

    /**
     * Handle sub-reply click (nested reply) from callback
     * @param replyUsername username being replied to
     * @param parentID id of parent comment
     * @param subParentID id of sub-parent comment
     */
    @Override
    public void subReplyClicked(String replyUsername, Long parentID, Long subParentID) {
        replyActive = true;
        setReplyLay(replyUsername, parentID, subParentID);
    }

}