package com.daribear.prefy.Explore;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daribear.prefy.Explore.ExplorePost.ExplorePostDialog;
import com.daribear.prefy.R;
import com.daribear.prefy.customClasses.FullPost;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExploreRecAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ExplorePostSet explorePostSet;
    public final Integer Explore_HEADER = 0;
    public final Integer Explore_RECTWOIMAGES = 1;
    private Activity parentActivity;
    private ExploreHeaderController headerController;
    private List<FullPost> fullFeaturedPostArrayList;
    private String firstChoiceName, secondChoiceName;
    private Integer firstChoiceDrawable, secondChoiceDrawable;



    public ExploreRecAdaptor(Activity parentActivity) {
        this.parentActivity = parentActivity;
        this.explorePostSet = new ExplorePostSet();
        explorePostSet.setPostList(new ArrayList<>());
        getCategories(parentActivity.getApplicationContext());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if (viewType == Explore_HEADER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.explore_header_item, parent, false);
            //ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //view.setLayoutParams(lp);
            holder = new HeaderViewHolder(view);
        } else if (viewType == Explore_RECTWOIMAGES){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_post_list_item, parent, false);
            holder = new TwoImagesViewHolder(view);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity) holder.itemView.getRootView().getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int width = displaymetrics.widthPixels / 3;
            holder.itemView.getLayoutParams().height = width;
            holder.itemView.getLayoutParams().width = width;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_post_list_item, parent, false);
            holder = new TwoImagesViewHolder(view);
        }
        return holder;
    }


    private void getCategories(Context applicationContext){
        String[] CategoriesList = applicationContext.getResources().getStringArray(R.array.post_categories);
        TypedArray ImagesList = applicationContext.getResources().obtainTypedArray(R.array.post_category_images);
        int List_Length = ImagesList.length();
        int[] resIds = new int[List_Length];
        for (int i = 0; i < List_Length; i++)
            resIds[i] = ImagesList.getResourceId(i, 0);
        ImagesList.recycle();
        Integer loopSize;
        if (CategoriesList.length <= resIds.length) {
                loopSize = CategoriesList.length;
        } else {
                loopSize = resIds.length;
            }
            Integer firstChoice = new Random().nextInt(loopSize - 1);
            Integer secondChoice = firstChoice;
            while (secondChoice == firstChoice) {
                secondChoice = new Random().nextInt(loopSize - 1);
            }
        this.firstChoiceName = CategoriesList[firstChoice];
        this.firstChoiceDrawable = resIds[firstChoice];
        this.secondChoiceName = CategoriesList[secondChoice];
        this.secondChoiceDrawable = resIds[secondChoice];
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == Explore_RECTWOIMAGES){
            TwoImagesViewHolder viewHolder = (TwoImagesViewHolder) holder;
            viewHolder.voteCountLay.setVisibility(View.GONE);
            initGlide(viewHolder.imageView, position - 1);
            ItemViewClicked(position- 1, viewHolder);
        } else if (holder.getItemViewType() == Explore_HEADER){
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
            viewHolder.setIsRecyclable(true);
            if (fullFeaturedPostArrayList == null){

                fullFeaturedPostArrayList = new ArrayList<>();
            }
            CategoryClass categoryClass = new CategoryClass(firstChoiceName, secondChoiceName, firstChoiceDrawable, secondChoiceDrawable);
            headerController = new ExploreHeaderController(viewHolder.itemView, fullFeaturedPostArrayList, parentActivity, categoryClass);

            //if (postList.size() == 0) {
            //    profileHeaderController.alterPostExtrasVisibility("ProgressBar");
           // }
        }
    }



    @Override
    public int getItemCount() {
        return 1 + explorePostSet.getPostList().size();
    }

    public void initData(ExplorePostSet explorePostSet) {
        Integer originalSize = this.explorePostSet.getPostList().size();
        this.explorePostSet = new ExplorePostSet();
        if (originalSize > 0){
            notifyItemRangeRemoved(1 , originalSize + 1);
        }
        this.explorePostSet = explorePostSet;
        if (explorePostSet.getPostList().size() > 0) {

            notifyDataSetChanged();
            //notifyItemRangeInserted(1, explorePostSet.getPostList().size());
        }
    }

    public void updateData(ExplorePostSet explorePostSet){
        if (explorePostSet != null & this.explorePostSet != null){
            Integer originalSize = this.explorePostSet.getPostList().size();
            Integer additionItems = explorePostSet.getPostList().size();
            for (int i = 0; i < additionItems; i ++){
                this.explorePostSet.getPostList().add(explorePostSet.getPostList().get(i));
            }
            notifyDataSetChanged();

        }
    }

    public void updateCategories(){
        getCategories(parentActivity.getApplicationContext());
        CategoryClass categoryClass = new CategoryClass(firstChoiceName, secondChoiceName, firstChoiceDrawable, secondChoiceDrawable);
        headerController.updateCategories(categoryClass);
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return Explore_HEADER;
        } else {
            return Explore_RECTWOIMAGES;
        }
    }

    public class TwoImagesViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private ConstraintLayout voteCountLay;

        public TwoImagesViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ProfilePostListItemImage);
            voteCountLay = itemView.findViewById(R.id.ProfilePostListVoteCountConsLay);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {


        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    private void initGlide(ImageView imageView, Integer pos){
        if (explorePostSet.getPostList().get(pos).getStandardPost().getImageURL() != null) {
            Glide
                    .with(imageView)
                    .load(explorePostSet.getPostList().get(pos).getStandardPost().getImageURL())
                    .into(imageView);
        }
    }

    public void alterFeaturedPosts(List<FullPost> fullFeaturedPostList){
        this.fullFeaturedPostArrayList = fullFeaturedPostList;
        if (headerController != null){
            headerController.alterFeaturedPosts(fullFeaturedPostList);
        }
    }

    private void ItemViewClicked(Integer position, RecyclerView.ViewHolder holder){
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExplorePostDialog dialog = new ExplorePostDialog(parentActivity, explorePostSet.getPostList().get(position).getUser(),explorePostSet.getPostList().get(position).getStandardPost(),  parentActivity);
                dialog.initDialog();
            }
        });
    }

    public Double returnLastCreationDate(){
        if (explorePostSet != null){
            if (explorePostSet.getPostList()!= null){
                if (explorePostSet.getPostList().size() > 0){
                    return explorePostSet.getPostList().get(explorePostSet.getPostList().size() - 1).getStandardPost().getCreationDate();
                }
            }
        }
        return null;
    }
}
