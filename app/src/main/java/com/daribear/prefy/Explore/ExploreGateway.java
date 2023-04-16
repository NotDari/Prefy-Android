package com.daribear.prefy.Explore;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daribear.prefy.Explore.ExploreCollection.ExploreCollectionAdaptor;
import com.daribear.prefy.Explore.ExploreList.ExploreListAdaptor;
import com.daribear.prefy.Profile.User;
import com.daribear.prefy.R;
import com.daribear.prefy.customClasses.FullPost;

import java.util.ArrayList;

public class ExploreGateway {
    private RecyclerView recView;
    private View view;
    private Context context;
    private Integer RecViewId,fragmentHeight;
    private ExploreCollectionAdaptor collectionAdaptor;
    private ExploreListAdaptor listAdaptor;
    private Boolean scrollUpdateLoading;
    private String type;
    private Fragment parentFragment;
    //private ScrollView scrollView;
    private NestedScrollView scrollView;
    private View.OnScrollChangeListener onScrollChangeListener;



    public ExploreGateway(RecyclerView recView, View view, Context context, Integer recViewId, Fragment parentFragment) {
        this.recView = recView;
        this.view = view;
        this.context = context;
        RecViewId = recViewId;
        this.parentFragment = parentFragment;
    }

    public void initEmptyCollectionGateway(Activity parentActivity){
        recView = view.findViewById(RecViewId);
        recView.setHasFixedSize(false);
        ExplorePostSet explorePostSet = new ExplorePostSet();
        explorePostSet.setPostList(new ArrayList<>());
        collectionAdaptor = new ExploreCollectionAdaptor(explorePostSet, parentActivity);
        recView.setAdapter(collectionAdaptor);
        recView.setItemViewCacheSize(18);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3){
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                lp.width = getWidth() / getSpanCount();
                lp.height = lp.width;
                return true;
            }
        };
        recView.setLayoutManager(gridLayoutManager);
        recView.setNestedScrollingEnabled(false);
        this.type = "Collection";
        setUpScroll();
    }

    public void updateExploreCollectionData(ExplorePostSet explorePostSet){
        //TODO found an issue with performance, basically what happens is due to being in a scroll view, the recyclerview onBindViewHolder loads all items and doesn't recyle ones off screen.
        scrollUpdateLoading = false;
        ExplorePostSet tempPostSet = new ExplorePostSet();
        ArrayList<FullPost> tempPostList = new ArrayList<>();
        Integer exploreLimit = Integer.parseInt(context.getString(R.string.Explore_Load_Count)) ;
        Integer checkCount = exploreLimit + collectionAdaptor.getItemCount();
        if (explorePostSet.getPostList().size() > checkCount){
            for (int i = 0; i < exploreLimit; i ++){
                tempPostList.add(explorePostSet.getPostList().get(i));
            }
            tempPostSet.setPostList(tempPostList);
            collectionAdaptor.setPosts(tempPostSet);
            collectionAdaptor.notifyDataSetChanged();
        }else {
            collectionAdaptor.setPosts(explorePostSet);
            collectionAdaptor.notifyDataSetChanged();
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) parentFragment.getActivity()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        Integer recViewHeight = (screenWidth / 3) * (explorePostSet.getPostList().size() / 3);
        recView.getLayoutParams().height = (recViewHeight);
    }

    public void initEmptyListGateway(Integer fragmentHeight){
        recView = view.findViewById(RecViewId);
        ExplorePostSet explorePostSet = new ExplorePostSet();
        explorePostSet.setPostList(new ArrayList<>());
        listAdaptor = new ExploreListAdaptor(explorePostSet);
        recView.setItemViewCacheSize(5);
        recView.setAdapter(listAdaptor);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context){
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                lp.height = (int) (fragmentHeight * .9);
                return super.checkLayoutParams(lp);
            }
        };
        recView.setLayoutManager(linearLayoutManager);
        recView.setNestedScrollingEnabled(false);
        this.type = "List";
        this.fragmentHeight = fragmentHeight;
        setUpScroll();
    }

    public void updateExploreListData(ExplorePostSet explorePostSet){
        scrollUpdateLoading = false;
        ExplorePostSet tempPostSet = new ExplorePostSet();
        ArrayList<FullPost> tempPostList = new ArrayList<>();
        ArrayList<User> tempUserList = new ArrayList<>();

        Integer exploreLimit = Integer.parseInt(context.getString(R.string.Explore_Load_Count)) ;
        Integer checkCount = exploreLimit + listAdaptor.getItemCount();
        if (explorePostSet.getPostList().size() > checkCount){

            for (int i = 0; i < exploreLimit; i ++){
                tempPostList.add(explorePostSet.getPostList().get(i));
            }
            tempPostSet.setPostList(tempPostList);
            listAdaptor.setPostList(tempPostSet);
            listAdaptor.notifyDataSetChanged();
        }else {
            listAdaptor.setPostList(explorePostSet);
            listAdaptor.notifyDataSetChanged();
        }


    }

    private void setUpScroll(){
        scrollUpdateLoading = false;
        //scrollView = parentFragment.getView().findViewById(R.id.ExploreHostScrollView);
        ExploreRepository exploreRepo = ExploreRepository.getInstance();
        if (scrollView != null) {
            onScrollChangeListener = new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    if (i1 > i3) {
                        if (type != null) {
                            if (type.equals("Collection")) {
                                Integer imageHeight = scrollView.getWidth() / 3;
                                Integer scrollViewTotalHeight = scrollView.getChildAt(0).getHeight() - scrollView.getHeight();
                                if (scrollView.getScrollY() >= (scrollViewTotalHeight - (2 * imageHeight))) {
                                    if (!scrollUpdateLoading) {
                                        ExplorePostSet explorePostSet = exploreRepo.getExplorePostSetMutable().getValue();
                                        if (explorePostSet.getPostList().size() > collectionAdaptor.getItemCount()){
                                            ExplorePostSet tempPostSet = new ExplorePostSet();
                                            ArrayList<FullPost> tempPostList = new ArrayList<>();
                                            Integer AmountReceivedList = collectionAdaptor.getItemCount() + Integer.parseInt( context.getString(R.string.Explore_Load_Count));
                                            for (int f = 0; f < AmountReceivedList; f ++){
                                                tempPostList.add(explorePostSet.getPostList().get(f));
                                            }
                                            tempPostSet.setPostList(tempPostList);
                                            updateExploreListData(tempPostSet);
                                        } else {
                                            Double lastCreationDate = collectionAdaptor.returnLastCreationDate();
                                            if (!scrollUpdateLoading) {
                                                exploreRepo.updateData(lastCreationDate);
                                            }
                                            scrollUpdateLoading = true;
                                        }

                                    }


                                }
                            }
                            else if (type.equals("List")){
                                Integer imageHeight = (int) (fragmentHeight * .9);
                                Integer scrollViewTotalHeight = scrollView.getChildAt(0).getHeight() - scrollView.getHeight();

                                if (scrollView.getScrollY() >= (scrollViewTotalHeight - (3 * imageHeight))) {
                                    if (!scrollUpdateLoading) {
                                        ExplorePostSet explorePostSet = exploreRepo.getExplorePostSetMutable().getValue();
                                        if (explorePostSet.getPostList().size() > listAdaptor.getItemCount()){
                                            ExplorePostSet tempPostSet = new ExplorePostSet();
                                            ArrayList<FullPost> tempPostList = new ArrayList<>();
                                            ArrayList<User> tempUserList = new ArrayList<>();
                                            Integer AmountReceivedList = listAdaptor.getItemCount() + Integer.parseInt( context.getString(R.string.Explore_Load_Count));
                                            for (int f = 0; f < AmountReceivedList; f ++){
                                                tempPostList.add(explorePostSet.getPostList().get(f));
                                            }
                                            tempPostSet.setPostList(tempPostList);
                                            updateExploreCollectionData(tempPostSet);
                                        } else {
                                            Double lastCreationDate = listAdaptor.returnLastCreationDate();
                                            if (!scrollUpdateLoading) {
                                                exploreRepo.updateData(lastCreationDate);
                                            }
                                            scrollUpdateLoading = true;
                                        }

                                    }


                                }
                            }
                        }

                    }
                }
            };
            scrollView.setOnScrollChangeListener(onScrollChangeListener);
        }
    }

    public void viewPaused(){
        onScrollChangeListener = null;
        this.scrollView.setOnScrollChangeListener(onScrollChangeListener);
        this.scrollView = null;
    }

    public void viewResumed(){
        //this.scrollView = parentFragment.getView().findViewById(R.id.ExploreHostScrollView);
        if (onScrollChangeListener != null && scrollView != null){
            scrollView.setOnScrollChangeListener(onScrollChangeListener);
        }
    }


    public void viewDestroyed(){
        this.view = null;
        this.context = null;
        if (collectionAdaptor != null){
            //collectionAdaptor.viewDestroyed();
        }
    }


}
