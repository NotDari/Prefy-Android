package com.daribear.prefy.Profile.ProfilePostsRec;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daribear.prefy.Profile.ProfilePostsRec.ProfileRetreiver.ProfilePostsRetreiver.ProfileHandlerInt;
import com.daribear.prefy.Profile.ProfilePostsRec.ProfileRetreiver.WholeProfile;
import com.daribear.prefy.R;
import com.daribear.prefy.customClasses.Posts.StandardPost;

import java.util.ArrayList;

public class ProfilePostsGateway implements ProfileScrolledToPosition, ProfileHandlerInt {
    private ArrayList<StandardPost> postList;
    private Integer RecViewId;
    private View view;
    private Context context;
    private RecyclerView recView;
    private ProfilePostsAdaptor adaptor;
    private String username, profileImageURL;
    private Boolean fromHomeProfile, scrollUpdateLoading;

    public ProfilePostsGateway(ArrayList<StandardPost> postList,Integer recViewId, View view, Context context, String username, String profileImageURL, Boolean fromHomeProfile) {
        this.postList = postList;
        RecViewId = recViewId;
        this.view = view;
        this.context = context;
        this.username = username;
        this.profileImageURL = profileImageURL;
        this.fromHomeProfile = fromHomeProfile;
    }


    public void displayView(){
        recView = view.findViewById(RecViewId);
        this.context = recView.getContext();
        adaptor = new ProfilePostsAdaptor(postList, profileImageURL, username, fromHomeProfile);
        adaptor.setPostList(postList);
        recView.setAdapter(adaptor);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3){
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                lp.width = getWidth() / getSpanCount();
                lp.height = lp.width;
                return true;
            }
        };
        DefaultItemAnimator animator = new DefaultItemAnimator() {
            @Override
            public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder) {
                return false;
            }
        };
        recView.setItemAnimator(animator);
        recView.setLayoutManager(gridLayoutManager);
        recView.setNestedScrollingEnabled(false);
        //initScrollListener(gridLayoutManager);
    }
    /**
    private void initScrollListener(GridLayoutManager gridLayoutManager){
        scrollUpdateLoading = false;
        ScrollView scrollView = view.findViewById(R.id.ProfileScrollView);
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if (i1 > i3){
                        Integer imageHeight = scrollView.getWidth() / 3;
                        Integer scrollViewTotalHeight =scrollView.getChildAt(0).getHeight() - scrollView.getHeight();

                        if (scrollView.getScrollY() >= (scrollViewTotalHeight - (2 *imageHeight))) {
                            if (!scrollUpdateLoading){
                                String uid = adaptor.getUid();
                                String creationDateUID = adaptor.getFirstCreationData();
                                if (!scrollUpdateLoading) {
                                   getMoreData(uid, creationDateUID);
                                }
                                scrollUpdateLoading = true;
                            }


                        }
                }
            }
        });
    }
     */



    public void destroyView(){
        this.view = null;
        //adaptor.viewDestroyed();
        adaptor = null;
        recView = null;
        this.context = null;

    }

    public void addExtraData(ArrayList<StandardPost> postList){
        adaptor.addData(postList);
    }

    private void getMoreData(String uid, String lastCreationDateUID){
        Integer postLimit = Integer.parseInt(view.getContext().getString(R.string.Search_Load_Count)) ;
        if (!scrollUpdateLoading && adaptor.getItemCount() >= postLimit) {
            Integer limit = Integer.parseInt(view.getContext().getString(R.string.Search_Load_Count));
            //ProfileExecutor profileExecutor = new ProfileExecutor(uid, this, true, limit, lastCreationDateUID, true);
            //profileExecutor.initExecutor();
        }
    }
    public void resetData(ArrayList<StandardPost> postList){
        if (postList != null){
            if (postList.size() != adaptor.getItemCount()) {
                adaptor.notifyItemRangeRemoved(0, adaptor.getItemCount());
                adaptor.setPostList(postList);
                adaptor.notifyItemRangeInserted(0, postList.size());
            } else {
                System.out.println("Sdad postList I total Votes" + postList.get(0).getAllVotes());
                adaptor.setPostList(postList);
                adaptor.notifyItemRangeChanged(0, adaptor.getItemCount());
            }
        }


    }



    @Override
    public void taskDone(Boolean successful, WholeProfile wholeProfile) {
        if (successful){
            addExtraData(wholeProfile.getPostListContainer().getPostList());
            scrollUpdateLoading = false;
        }
    }

    @Override
    public void scrolled(String uid, String lastCreationDateUID) {
        //getMoreData(uid, lastCreationDateUID);
    }
}
