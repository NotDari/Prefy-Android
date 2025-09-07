package com.daribear.prefy.Explore.ExploreCategories.ExploreCategoriesSelector;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


/**
 * Gateway class to manage the explore category selector RecyclerView
 * iot sets up the adapter, layout manager, and binds category titles and images
 */
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


    /**
     * Display the RecyclerView with categories
     * it sets the adapter and uses a 3-column GridLayout
     */
    public void displayView(){
        adaptor = new ExploreCategorySelectorAdaptor(categoryTitle, categoryImages, imageWidth);
        recView.setAdapter(adaptor);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        recView.setLayoutManager(gridLayoutManager);
    }



}
