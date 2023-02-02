package com.example.prefy.Explore;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prefy.customClasses.FullPost;

import java.util.List;


public class NewExploreGateway implements ExploreRecentUpdateInterface{
    private RecyclerView recView;
    private View view;
    private Context context;
    private ExploreRecAdaptor adaptor;
    private Activity parentActivity;
    private Boolean scrollUpdateLoading;
    private Integer scrollViewOffset;
    private GridLayoutManager gridLayoutManager;
    private Integer pageNumber;


    public NewExploreGateway(RecyclerView recView, View view, Activity parentActivity) {
        this.recView = recView;
        this.view = view;
        this.context = view.getContext();
        this.parentActivity = parentActivity;
    }


    public void displayView(){
        adaptor = new ExploreRecAdaptor(parentActivity);
        pageNumber = 0;
        recView.setHasFixedSize(false);
        recView.setAdapter(adaptor);
        recView.setItemViewCacheSize(18);
        gridLayoutManager = new GridLayoutManager(context, 3){
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                return true;
            }
        };
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adaptor.getItemViewType(position) == adaptor.Explore_HEADER){
                    return 3;
                } else if (adaptor.getItemViewType(position) == adaptor.Explore_RECTWOIMAGES){
                    return 1;
                } else {
                    return 1;
                }
            }
        });
        recView.setLayoutManager(gridLayoutManager);
        initScroll();
    }

    private void initScroll(){
        scrollUpdateLoading = true;
        scrollViewOffset = 0;
        if (parentActivity != null){

        }
        if (recView != null) {
            recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (!scrollUpdateLoading){
                        if (!scrollUpdateLoading) {
                            if (gridLayoutManager != null){
                                if (gridLayoutManager.findLastCompletelyVisibleItemPosition() >= adaptor.getItemCount() - 7){
                                    updateData(pageNumber);
                                    scrollUpdateLoading = true;
                                }
                            }
                        }
                    }
                }
            });
        }
    }



    public void initData(ExplorePostSet explorePostSet){
        adaptor.initData(explorePostSet);
        this.pageNumber = 1;
        scrollUpdateLoading = false;
    }

    public void updateData(Integer pageNumber){
        ExploreRecentPostUpdater executor = new ExploreRecentPostUpdater(18, pageNumber, this);
        executor.initExecutor();
    }


    public void alterFeaturedPosts(List<FullPost> fullFeaturedPostList){
       adaptor.alterFeaturedPosts(fullFeaturedPostList);
    }

    public void refreshCategories(){
        adaptor.updateCategories();
    }


    @Override
    public void completed(Boolean successful, ExplorePostSet additionalPosts) {
        pageNumber += 1;
        scrollUpdateLoading = false;
        if (successful){
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adaptor.updateData(additionalPosts);
                }
            });

        } else {
            System.out.println("Sdad hello!");
        }
    }
}
