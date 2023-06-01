package com.daribear.prefy.Comments;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daribear.prefy.Profile.User;
import com.daribear.prefy.Utils.ServerAdminSingleton;
import com.daribear.prefy.customClasses.Posts.FullPost;

import java.util.ArrayList;

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
        FullRecComment fullRecComment = new FullRecComment();
        FullComment fullComment = new FullComment();
        User user = ServerAdminSingleton.getCurrentUser(parentActivity.getApplicationContext());
        comment.setUser(user);
        fullComment.setComment(comment);
        fullComment.setReplyCount(0);
        fullRecComment.setFullComment(fullComment);
        fullRecComment.setRepliesShown(0);
        fullRecComment.setMinimised(false);


        adaptor.getCommentList().add(fullRecComment);
        adaptor.notifyDataSetChanged();

    }

    public void commentdeleted(Comment comment){
       adaptor.removeComment(comment.getCommentId());
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
