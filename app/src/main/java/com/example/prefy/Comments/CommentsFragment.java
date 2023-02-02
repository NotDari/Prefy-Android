package com.example.prefy.Comments;

import android.app.Activity;
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
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.prefy.Network.UploadController.UploadController;
import com.example.prefy.Profile.User;
import com.example.prefy.R;
import com.example.prefy.Utils.ServerAdminSingleton;
import com.example.prefy.Utils.Utils;
import com.example.prefy.Utils.dateSinceSystem;
import com.example.prefy.customClasses.StandardPost;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;

import io.grpc.Server;


public class CommentsFragment extends Fragment implements CommentReplyClicked{
    private handleCommentsRecView commentsRecView;
    private User user;
    private StandardPost post;
    private Boolean replyActive;
    private String replyUsername;
    private Long replyId;
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

    private void initBackSystem(View view){
        ImageView backButton = view.findViewById(R.id.CommentsTopBarBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });
        alterBottomNavVisibility(true);
        resizeSubmitCommentEditText(view);
    }

    private void alterBottomNavVisibility(Boolean hide){
        BottomNavigationView bottomNavigation = getActivity().findViewById(R.id.BottomNav);
        if (hide){
            bottomNavigation.setVisibility(View.GONE);
        }else {
            bottomNavigation.setVisibility(View.VISIBLE);
        }
    }

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
                    Double time= (double) System.currentTimeMillis();
                    Double date = time / 1000;


                    comment.setText(text);
                    comment.setReplyID(replyId);
                    System.out.println("Sdad replyId:" + replyId);
                    comment.setReplyUsername(replyUsername);
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

                //TODO FIX COMMENTS
                /**
                 *
                 *
                String text = submitCommentEdit.getText().toString();

                String serveraddress = utils.loadString(getString(R.string.Server_base_address), "");
                if (!text.isEmpty()){
                    FirebaseFirestore ff = FirebaseFirestore.getInstance();
                    Double time= (double) System.currentTimeMillis();
                    Double date = time / 1000;
                    Long finalTime = date.longValue();
                    WriteBatch writeBatch = ff.batch();
                    HashMap<String, Object> newComment = new HashMap<>();
                    newComment.put("creationDate", finalTime);
                    newComment.put("postUID", post.getPostId());
                    newComment.put("replyUserUid", replyUid);
                    newComment.put("replyUsername", replyUsername);
                    newComment.put("text", text);
                    newComment.put("uid", FirebaseAuth.getInstance().getUid());
                    DocumentReference commentRef = ff.collection("Comments").document();
                    writeBatch.set(commentRef, newComment);
                    DocumentReference postRef = ff.collection("Posts").document(post.getKey());
                    writeBatch.update(postRef, "commentsNumber", FieldValue.increment(1));
                    writeBatch.commit();

                    Comment comment = new Comment();
                    comment.setText(text);
                    comment.setUserId(FirebaseAuth.getInstance().getUid());
                    comment.setCreationDate(finalTime.doubleValue());
                    comment.setReplyUsername(replyUsername);
                    comment.setReplyUID(replyUid);
                    comment.setKey(commentRef.getId());
                    comment.setPostId(post.getKey());

                    commentsRecView.commentSubmitted(comment);

                    removeReplyLay();
                    submitCommentEdit.getText().clear();
                } else {
                    Toast.makeText(view.getContext(), "Comment can't be empty", Toast.LENGTH_SHORT).show();
                }
                 */
            }
        });
    }

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

    private void initViews(View view){
        ImageView postImage = view.findViewById(R.id.CommentPostImageView);
        initGlide(postImage, post.getImageURL());
        ImageView posterProfileImage = view.findViewById(R.id.CommentsPosterProfileImage);
        if (!user.getProfileImageURL().equals("none")) {
            Glide.with(posterProfileImage)
                    .load(user.getProfileImageURL())
                    .circleCrop()
                    .into(posterProfileImage);
        } else{
            Glide.with(posterProfileImage)
                    .load(R.drawable.user_photo)
                    .circleCrop()
                    .into(posterProfileImage);
        }
        TextView posterUsernameText = view.findViewById(R.id.CommentsPosterProfileUsername);
        posterUsernameText.setText(user.getUsername());
        TextView posterTime = view.findViewById(R.id.CommentsPosterPostDate);
        posterTime.setText(dateSinceSystem.getTimeSince(post.getCreationDate()));
        RecyclerView recyclerView = view.findViewById(R.id.CommentsRecView);

        TextView questionText = view.findViewById(R.id.CommentQuestionText);
        questionText.setText(post.getQuestion());

    }

    private void initGlide(ImageView imageView, String imageURL){
        Glide.with(imageView)
                .load(imageURL)
                .into(imageView);
    }

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

    private void setReplyLay(String replyUsername, Long replyId){
        replyLay.setVisibility(View.VISIBLE);
        replyActive = true;

        this.replyId = replyId;
        this.replyUsername = replyUsername;
        TextView textView = replyLay.findViewById(R.id.CommentsReplyLayText);
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append("Reply : ");
        SpannableString replySpannable= new SpannableString(replyUsername);
        replySpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, replyUsername.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        replySpannable.setSpan(new ForegroundColorSpan(replyLay.getContext().getColor(R.color.text_color)), 0, replyUsername.length(), 0);
        builder.append(replySpannable);
        textView.setText(builder);
    }

    private void removeReplyLay(){
        replyActive = false;
        replyId = null;
        replyUsername = null;
        replyLay.setVisibility(View.GONE);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        alterBottomNavVisibility(false);
        commentsRecView.viewDestroyed();
    }



    @Override
    public void clicked(String replyUsername, Long replyId) {
        replyActive = true;
        setReplyLay(replyUsername, replyId);
    }
}