package com.daribear.prefy.Comments;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.daribear.prefy.Profile.User;
import com.daribear.prefy.R;
import com.daribear.prefy.customClasses.Posts.FullPost;
import com.daribear.prefy.customClasses.Posts.StandardPost;

import java.util.ArrayList;

public class handleCommentsRecView implements CommentRetreiverInterface, CommentViewMoreClicked {
    private View baseView, commentSeperatorView;
    private User postUser;
    private StandardPost post;
    private CommentListGateway gateway;
    private RecyclerView recView;
    private Boolean destroyed, fetchmoreData;
    private ArrayList<FullRecComment> wholeList, currentList;
    private Boolean gettingExtraData;
    private Activity parentActivity;
    private CommentReplyClicked commentDelegate;
    private Integer pageNumber;

    public handleCommentsRecView(View baseView, User postUser, StandardPost post, Activity parentActivity, CommentReplyClicked commentDelegate) {
        this.baseView = baseView;
        this.postUser = postUser;
        this.post = post;
        this.parentActivity = parentActivity;
        this.commentDelegate = commentDelegate;
    }

    public void initSetup(){
        getViews();
        initEmptyRecView();
        getData();
        viewMore();
        //hideViewMore();
    }

    private void getViews(){
        recView = baseView.findViewById(R.id.CommentsRecView);
        commentSeperatorView = baseView.findViewById(R.id.CommentSeperatorView);
    }


    private void getData(){
        destroyed = false;
        getCommentsExecutor executor = new getCommentsExecutor(post.getPostId(), this);
        executor.initExecutor();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) baseView.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height40 = (int) ((int) displayMetrics.heightPixels * .4);
        int width = displayMetrics.widthPixels;
        recView.setMinimumHeight(height40);
    }

    private void initEmptyRecView(){
        FullPost fullPost = new FullPost();
        fullPost.setUser(postUser);
        fullPost.setStandardPost(post);
        gateway = new CommentListGateway(this,new ArrayList<>(), R.id.CommentsRecView, baseView, baseView.getContext(), fullPost, this, parentActivity, commentDelegate);
        gateway.displayEmptyView();

    }

    private void setrecData(ArrayList<FullRecComment> commentList, Integer pageNumber){
        this.pageNumber = pageNumber;
        recView.setClickable(true);
        recView.setVisibility(View.VISIBLE);
        ArrayList<FullRecComment> chosenList;
        if (commentList.size() > 5){
            if (commentList.size() >= 10){
                fetchmoreData = true;
            } else {
                fetchmoreData = false;
            }
            gateway.setViewMoreVisibility(true);
            chosenList = new ArrayList<>();
            for (int i = 0; i < 5; i ++){
                chosenList.add(commentList.get(i));
            }
        } else {
            chosenList = (ArrayList<FullRecComment>) commentList.clone();
            gateway.setViewMoreVisibility(false);
        }
        this.wholeList = commentList;
        this.currentList = chosenList;
        gateway.addData(chosenList);

    }


    private void initNoInternet(){
        gateway.initNoInternet();

    }

    private void viewMore(){
        gettingExtraData = false;
    }


    public void commentSubmitted(Comment comment){
        gateway.commentSubmitted(comment);
        recView.setVisibility(View.VISIBLE);
    }

    public void commentDeleted(Comment comment){
        gateway.commentdeleted(comment);
        recView.setVisibility(View.VISIBLE);
    }

    private void getMoreData(){
        CommentUpdaterExecutor commentUpdaterExecutor = new CommentUpdaterExecutor(post.getPostId(), this, pageNumber);
        commentUpdaterExecutor.initExecutor();
    }

    public void refreshData(){
        getCommentsExecutor commentExecutor = new getCommentsExecutor(post.getPostId(), this);
        commentExecutor.initExecutor();
    }



    public void viewDestroyed(){
        destroyed = true;
        gateway.destroyView();
    }

    @Override
    public void viewClicked() {
        System.out.println("Sdad wholeList: " + wholeList.size() + " currentList:" + currentList.size() + gettingExtraData);
        if (wholeList.size() > currentList.size()){
            Integer limit;
            if (currentList.size() + 5 <= wholeList.size()){
                limit = currentList.size() +  5;
            } else {
                limit = wholeList.size();
                gateway.setViewMoreVisibility(false);
            }
            for (int i = (currentList.size()); i < limit; i++){
                gateway.addComment(wholeList.get(i));
            }
            if (wholeList.size() % 10 == 0){
                if (!gettingExtraData) {
                    gettingExtraData = true;
                    getMoreData();
                }
            }
        } else {
            if (currentList.size() <= wholeList.size()) {
                if (wholeList.size() % 10 == 0) {
                    if (!gettingExtraData) {
                        gettingExtraData = true;
                        getMoreData();
                    }
                }
            }
        }
    }

    @Override
    public void complete(Boolean successful, Boolean update, ArrayList<FullRecComment> commentList, Integer pageNumber) {
        if (!destroyed){
            if (!update) {
                if (successful) {
                    setrecData(commentList, pageNumber);
                } else {
                    initNoInternet();
                }
            } else {
                gettingExtraData = false;
                if (successful){
                    if (commentList.size() == 0){
                        gateway.setViewMoreVisibility(false);
                    }
                    for (int i = 0; i < commentList.size(); i ++){
                        wholeList.add(commentList.get(i));
                    }

                }
            }
        }
    }
}
