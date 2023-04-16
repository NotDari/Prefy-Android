package com.daribear.prefy.Explore.ExploreCategories.ExploreCategoriesSelector;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daribear.prefy.R;

import java.util.ArrayList;

public class ExploreCategorySelectorAdaptor extends RecyclerView.Adapter<ExploreCategorySelectorAdaptor.ViewHolder>{
    private ArrayList<String> categoryTitleList;
    private ArrayList<Integer> categoryImagesList;
    private Integer imageWidth;

    public ExploreCategorySelectorAdaptor(ArrayList<String> categoryTitleList, ArrayList<Integer> categoryImagesList, Integer imageWidth) {
        this.categoryTitleList = categoryTitleList;
        this.categoryImagesList = categoryImagesList;
        this.imageWidth = imageWidth;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.explore_category_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.relLayout.getLayoutParams().width = imageWidth;
        holder.relLayout.getLayoutParams().height = imageWidth;
        holder.imageView.getLayoutParams().height = (int)((int) imageWidth * 0.8);
        holder.imageView.getLayoutParams().width = (int)((int) imageWidth * 0.8);
        holder.textView.getLayoutParams().height = (int)((int) imageWidth * 0.2);
        holder.textView.getLayoutParams().width = imageWidth;
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(categoryTitleList.get(holder.getAdapterPosition()));
        Glide.with(holder.imageView)
                .load(categoryImagesList.get(holder.getAdapterPosition()))
                .into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewClicked(holder.itemView,holder.getAdapterPosition());
            }
        });
    }


    @Override
    public int getItemCount() {
        return categoryTitleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout relLayout;
        private ImageView imageView;
        private TextView textView;
        private FrameLayout frameLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relLayout = itemView.findViewById(R.id.ExploreCategoryImageLayout);
            imageView = itemView.findViewById(R.id.ExploreCategorySelectorImage);
            textView = itemView.findViewById(R.id.ExploreCategorySelectorText);
            frameLayout = itemView.findViewById(R.id.ExploreCategorySelectorItem);
        }
    }

    private void viewClicked(View view,int position){
        Bundle bundle = new Bundle();
        bundle.putString("category", categoryTitleList.get(position));
        Navigation.findNavController(view).navigate(R.id.action_exploreCategorySelectorFragment_to_exploreCategoriesPostFragment, bundle);
    }
}
