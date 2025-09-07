package com.daribear.prefy.Explore.ExploreCategories.ExploreCategoriesSelector;

import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daribear.prefy.R;

import java.util.ArrayList;


/**
 * Fragment to display the explore category selector screen
 * Handles back navigatson, loads category titles and images aswell as initialises the gateway
 */
public class ExploreCategorySelectorFragment extends Fragment {
    private RecyclerView recView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore_category_selector, container, false);
        getViews(view);
        getData(view);
        return view;
    }

    /**
     * Initialise views and setup top bar back navigation
     * @param view base view of fragment
     */
    private void getViews(View view){
        recView = view.findViewById(R.id.ExploreCategoriesSelectorRecView);
        view.findViewById(R.id.ExploreCategoriesSelectorTopBarBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigateUp();
            }
        });
        TextView titleText = view.findViewById(R.id.ExploreCategoriesSelectorTopBarTitle);
        titleText.setText("Categories");
    }

    /**
     * Load category titles and images from resources
     * Initialise and display the ExploreCategorySelectorGateway
     * @param view base view
     */
    private void getData(View view){
        String[] CategoriesList = view.getContext().getApplicationContext().getResources().getStringArray(R.array.post_categories);
        TypedArray ImagesList = view.getContext().getApplicationContext().getResources().obtainTypedArray(R.array.post_category_images);
        int List_Length = ImagesList.length();
        int[] resIds = new int[List_Length];
        for (int i = 0; i < List_Length; i++)
            resIds[i] = ImagesList.getResourceId(i, 0);
        ImagesList.recycle();

        Integer imageWidth = ExploreCategorySelectorFragmentArgs.fromBundle(this.getArguments()).getCategoryImageSize();
        ArrayList<String> categoryTitles = new ArrayList<String>();
        ArrayList<Integer> categoryImages = new ArrayList<Integer>();
        for (int i = 0; i < CategoriesList.length; i ++){
            categoryTitles.add(CategoriesList[i]);
            categoryImages.add(resIds[i]);
        }
        //initialise gateway and display RecyclerView
        ExploreCategorySelectorGateway gateway = new ExploreCategorySelectorGateway(categoryTitles, categoryImages, recView, imageWidth);
        gateway.displayView();
    }
}