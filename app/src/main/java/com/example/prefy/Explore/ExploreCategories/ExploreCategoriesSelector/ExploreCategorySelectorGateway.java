package com.example.prefy.Explore.ExploreCategories.ExploreCategoriesSelector;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExploreCategorySelectorGateway {
    private ArrayList<String> categoryTitle;
    private ArrayList<Integer> categoryImages;
    private RecyclerView recView;
    private ExploreCategorySelectorAdaptor adaptor;
    private Context context;
    private Integer imageWidth;

    public ExploreCategorySelectorGateway(ArrayList<String> categoryTitle, ArrayList<Integer> categoryImages, RecyclerView recView, Integer imageWidth) {
        this.categoryTitle = categoryTitle;
        this.categoryImages = categoryImages;
        this.recView = recView;
        this.context = recView.getContext();
        this.imageWidth = imageWidth;
    }

    public void displayView(){
        adaptor = new ExploreCategorySelectorAdaptor(categoryTitle, categoryImages, imageWidth);
        recView.setAdapter(adaptor);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        recView.setLayoutManager(gridLayoutManager);
    }



}
