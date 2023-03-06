package com.example.prefy.Explore.ExploreCategories.ExploreCategoriesPosts;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.prefy.Explore.ExplorePostSet;
import com.example.prefy.R;


public class ExploreCategoriesPostFragment extends Fragment implements ExploreCategoryInterface {
    private ImageView backButton;
    private TextView titleCategory, noInternetText;
    private String categoryTitle;
    private RecyclerView recView;
    private  ExploreCategoriesPostGateway gateway;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore_categories_post, container, false);
        getTitle();
        getViews(view);
        getData();
        return view;
    }

    private void getTitle(){
        this.categoryTitle = ExploreCategoriesPostFragmentArgs.fromBundle(getArguments()).getCategory();

    }

    private void getData(){
        progressBar.setVisibility(View.VISIBLE);
        ExploreCategoriesRetreiver exploreCategoriesRetreiver = new ExploreCategoriesRetreiver(this, null, categoryTitle, 18);
        exploreCategoriesRetreiver.initExecutor();
    }

    private void noInternet(){
        noInternetText.setVisibility(View.VISIBLE);
        recView.setVisibility(View.GONE);
        noInternetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noInternetText.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                getData();
            }
        });
    }


    private void getViews(View view){
        backButton = view.findViewById(R.id.ExploreCategoriesListListTopBarBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigateUp();
            }
        });
        titleCategory = view.findViewById(R.id.ExploreCategoriesListListTopBarTitle);
        titleCategory.setText(categoryTitle);
        recView = view.findViewById(R.id.ExploreCategoriesListListRecView);
        gateway = new ExploreCategoriesPostGateway(R.id.ExploreCategoriesListListRecView, view, view.getContext());
        gateway.displayView();
        progressBar = view.findViewById(R.id.ExploreCategoriesListListProgressBar);
        noInternetText = view.findViewById(R.id.ExploreCategoriesListListNoInternet);
    }

    @Override
    public void Completed(Boolean successful, ExplorePostSet explorePostSet) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                    if (successful) {
                        gateway.updateData(explorePostSet.getPostList());
                    } else {
                        noInternet();
                    }
                }
            });
        }

    }
}