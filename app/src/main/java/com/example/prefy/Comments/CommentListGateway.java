package com.example.prefy.Comments;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prefy.Profile.User;
import com.example.prefy.R;
import com.example.prefy.Utils.FirebaseUtils;
import com.example.prefy.Utils.Utils;
import com.example.prefy.customClasses.FullPost;
import com.example.prefy.customClasses.StandardPost;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import okhttp3.internal.Util;

public class CommentListGateway {
    private ArrayList<FullRecComment> commentList;
    private Integer RecViewId;
    private View view;
    private Context context;
    private RecyclerView recView;
    private CommentListAdaptor adaptor;
    private FullPost fullPost;
    private handleCommentsRecView handleCommentsRecView;
    private Boolean viewMoreVisible;
    private LinearLayoutManager linearLayoutManager;
    private CommentViewMoreClicked commentViewMoreDelegate;
    private Activity parentActivity;
    private CommentReplyClicked commentDelegate;

    public CommentListGateway(handleCommentsRecView handleCommentsRecView,ArrayList<FullRecComment> commentList, Integer recViewId, View view, Context context, FullPost fullPost, CommentViewMoreClicked commentViewMoreDelegate, Activity parentActivity, CommentReplyClicked commentDelegate) {
        this.handleCommentsRecView = handleCommentsRecView;
        this.commentList = commentList;
        RecViewId = recViewId;
        this.view = view;
        this.context = context;
        this.fullPost = fullPost;
        this.commentViewMoreDelegate = commentViewMoreDelegate;
        this.parentActivity = parentActivity;
        this.commentDelegate = commentDelegate;
    }

    public void displayEmptyView(){
        recView = view.findViewById(RecViewId);
        this.context = recView.getContext();
        adaptor = new CommentListAdaptor(handleCommentsRecView, commentList, fullPost, commentViewMoreDelegate, parentActivity, commentDelegate);
        recView.setAdapter(adaptor);
        linearLayoutManager = new LinearLayoutManager(context){
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {

                return super.checkLayoutParams(lp);
            }
        };
        recView.setLayoutManager(linearLayoutManager);
        viewMoreVisible = false;
        detectViewMore();
    }

    public void addData(ArrayList<FullRecComment> addCommentList){
        adaptor.setCommentList(addCommentList);
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adaptor.notifyDataSetChanged();
            }
        });

    }

    public void initNoInternet(){
        adaptor.initNoInternet();
    }

    public void addComment(FullRecComment comment){
        Integer originalSize = adaptor.getItemCount();
        adaptor.getCommentList().add(comment);
        Integer newSize = adaptor.getItemCount();
        adaptor.notifyDataSetChanged();
    }

    public void commentSubmitted(Comment comment){
        //TODO fix commentSubmitted if necessary
        /**
        FullComment fullComment = new FullComment();
        fullComment.setComment(comment);
        User user = FirebaseUtils.getCurrentUser(context.getApplicationContext());
        fullComment.setUser(user);
        adaptor.getCommentList().add(fullComment);
        adaptor.notifyDataSetChanged();
         */
    }

    public void commentdeleted(Comment comment){
        //TODO fix commentDeleted if necessary
        /**
        FullComment fullComment = new FullComment();
        fullComment.setComment(comment);
        User user = FirebaseUtils.getCurrentUser(context.getApplicationContext());
        fullComment.setUser(user);
        adaptor.getCommentList().remove(comment);
        adaptor.notifyDataSetChanged();
         */
    }

    public void detectViewMore(){
    }

    public void setViewMoreVisibility(Boolean visible){
        adaptor.setViewMoreVisibility(visible);
    }


    public void destroyView(){
        this.view = null;
        //adaptor.viewDestroyed();
        adaptor = null;
        recView = null;

    }
}
