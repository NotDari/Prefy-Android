package com.example.prefy.Profile.ProfilePostsRec;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prefy.Profile.ProfilePostsRec.ProfileRetreiver.ProfilePostsRetreiver.ProfileExecutor;
import com.example.prefy.Profile.ProfilePostsRec.ProfileRetreiver.ProfilePostsRetreiver.ProfileHandlerInt;
import com.example.prefy.Profile.ProfilePostsRec.ProfileRetreiver.WholeProfile;
import com.example.prefy.Profile.User;
import com.example.prefy.R;
import com.example.prefy.customClasses.PostListContainer;
import com.example.prefy.customClasses.StandardPost;

import java.util.ArrayList;

public class NewProfilePostsGateway implements ProfileHandlerInt {
    private ArrayList<StandardPost> postList;
    private View view;
    private Context context;
    private RecyclerView recView;
    private NewProfilePostsAdaptor adaptor;
    private Boolean fromHomeProfile, scrollUpdateLoading;
    private User user;
    private Integer imageHeight = 0;
    private GridLayoutManager gridLayoutManager;

    public NewProfilePostsGateway(RecyclerView recView, View view, User user, Boolean fromHomeProfile) {
        this.recView = recView;
        this.view = view;
        this.context = view.getContext();
        this.fromHomeProfile = fromHomeProfile;
        this.user = user;
    }

    public void displayView(){
        Integer spanCount = 3;
        this.context = recView.getContext();
        PostListContainer postListContainer = new PostListContainer();
        postListContainer.setPostList(new ArrayList<>());
        postListContainer.setPageNumber(0);
        adaptor = new NewProfilePostsAdaptor(postListContainer,user, fromHomeProfile, spanCount);
        recView.setAdapter(adaptor);
        gridLayoutManager = new GridLayoutManager(context, spanCount){
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                Integer adaptorPosition = adaptor.getAdaptorPostition();
                if (adaptor.getItemViewType(adaptorPosition) == adaptor.PROFILE_HEADER){

                } else {
                   // lp.width = getWidth() / getSpanCount();
                }

                return true;
            }
        };
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adaptor.getItemViewType(position) == adaptor.PROFILE_HEADER){
                    return 3;
                } else if (adaptor.getItemViewType(position) == adaptor.PROFILE_RECTWOIMAGES){
                    return 1;
                } else {
                    return 1;
                }
            }
        });


        DefaultItemAnimator animator = new DefaultItemAnimator() {
            @Override
            public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder) {
                return false;
            }
        };
        recView.setItemViewCacheSize(0);
        recView.setItemAnimator(animator);
        recView.setLayoutManager(gridLayoutManager);
        scrollListener();
    }

    public void setInitPosts(ArrayList<StandardPost> postList){
        scrollUpdateLoading = false;
        adaptor.initPostList(postList);
    }

    public void scrollListener(){
        scrollUpdateLoading = true;
        recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0){
                    if (gridLayoutManager.findLastCompletelyVisibleItemPosition() == adaptor.getItemCount() - 7){
                        Long userId = adaptor.getUserId();
                        Integer pageNumber = adaptor.getPageNumber();
                        if (!scrollUpdateLoading) {
                            getMoreData(userId, pageNumber);
                        }
                        scrollUpdateLoading = true;
                    }

                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });



    }

    private void getMoreData(Long userId, Integer pageNumber){
        Integer postLimit = Integer.parseInt(view.getContext().getString(R.string.Search_Load_Count)) ;
        if (!scrollUpdateLoading && ((adaptor.getItemCount()-1) % postLimit == 0)) {
            Integer limit = Integer.parseInt(view.getContext().getString(R.string.Search_Load_Count));
            ProfileExecutor profileExecutor = new ProfileExecutor(userId, this, true, limit, pageNumber, true);
            profileExecutor.initExecutor();
        }
    }

    public User getUser() {
        return user;
    }

    public void updateUserInfo(User user){
        this.user = user;
        adaptor.updateUserInfo(user);

    }


    @Override
    public void taskDone(Boolean successful, WholeProfile wholeProfile) {
        if (successful){
            adaptor.addData(wholeProfile.getPostListContainer());

        } else {
        }
        scrollUpdateLoading = false;
    }
}
