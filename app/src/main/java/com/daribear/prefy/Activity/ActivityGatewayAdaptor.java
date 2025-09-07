package com.daribear.prefy.Activity;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daribear.prefy.Activity.Comment.CommentActivity;
import com.daribear.prefy.Activity.Comment.CommentRecAdaptor;
import com.daribear.prefy.Activity.Followers.FollowerActivity;
import com.daribear.prefy.Activity.Followers.FollowerRecAdaptor;
import com.daribear.prefy.Activity.Votes.VoteActivity;
import com.daribear.prefy.Activity.Votes.VoteRecAdaptor;

import java.util.ArrayList;

/**
 * THE gateway to the Activity recyclerviews, handle comment votes and followers
 */
public class ActivityGatewayAdaptor {
    private Integer RecViewId;
    private View view;
    private Context context;
    private RecyclerView recView;
    private CommentRecAdaptor commentAdaptor;
    private VoteRecAdaptor voteAdaptor;
    private FollowerRecAdaptor followerAdaptor;

    //constructor
    public ActivityGatewayAdaptor(Integer recViewId, View view, Context context, RecyclerView recView) {
        RecViewId = recViewId;
        this.view = view;
        this.context = context;
        this.recView = recView;
    }

    /**
     * setup recyclerview for comment list
     * @param commentActivityList list of comment data
     */
    public void displayEmptyComment(ArrayList<CommentActivity> commentActivityList){
        recView = view.findViewById(RecViewId);
        this.context = recView.getContext();
        commentAdaptor = new CommentRecAdaptor(commentActivityList);
        recView.setAdapter(commentAdaptor);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context){
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                lp.height = getHeight() / 7;
                return super.checkLayoutParams(lp);
            }
        };
        recView.setLayoutManager(linearLayoutManager);
        recView.setNestedScrollingEnabled(false);
    }

    /**
     * refresh comment adapter data
     * @param commentActivityList list of commentData to update with
     */
    public void updateCommentData(ArrayList<CommentActivity> commentActivityList){
        commentAdaptor.setCommentActivityList(commentActivityList);
        commentAdaptor.notifyDataSetChanged();
    }

    /**
     * setup recyclerview for voteActivity list
     * @param voteActivityList list of starting vote data
     */
    public void displayEmptyVote(ArrayList<VoteActivity> voteActivityList){
        recView = view.findViewById(RecViewId);
        this.context = recView.getContext();
        voteAdaptor = new VoteRecAdaptor(voteActivityList);
        recView.setAdapter(voteAdaptor);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context){
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                lp.height = getHeight() / 7;
                return super.checkLayoutParams(lp);
            }
        };
        recView.setLayoutManager(linearLayoutManager);
        recView.setNestedScrollingEnabled(false);
    }

    /**
     * refresh vote adapter data
     * @param voteActivityList list of voteActivity to update with
     */
    public void updateVoteData(ArrayList<VoteActivity> voteActivityList){
        voteAdaptor.setVoteActivityList(voteActivityList);
        voteAdaptor.notifyDataSetChanged();
    }


    /**
     * setup recyclerview for followActivity list
     * @param followerActivityList list of starting follower data
     */
    public void displayEmptyFollower(ArrayList<FollowerActivity> followerActivityList){
        recView = view.findViewById(RecViewId);
        this.context = recView.getContext();
        followerAdaptor = new FollowerRecAdaptor(followerActivityList);
        recView.setAdapter(followerAdaptor);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context){
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                lp.height = getHeight() / 7;
                return super.checkLayoutParams(lp);
            }
        };
        recView.setLayoutManager(linearLayoutManager);
        recView.setNestedScrollingEnabled(false);
    }

    /**
     * refresh follower adapter data
     * @param followerActivityList list of followActivity to update with
     */
    public void updateFollowerData(ArrayList<FollowerActivity> followerActivityList){
        followerAdaptor.setFollowerActivityList(followerActivityList);
        followerAdaptor.notifyDataSetChanged();
    }




    public void destroyView(){
        this.view = null;
        //adaptor.viewDestroyed();
        commentAdaptor = null;
        recView = null;

    }
}
