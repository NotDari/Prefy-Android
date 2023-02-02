package com.example.prefy.Explore;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.prefy.Explore.ExplorePost.ExplorePostDialog;
import com.example.prefy.R;
import com.example.prefy.Utils.Utils;
import com.example.prefy.customClasses.FullPost;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import okhttp3.internal.Util;

public class ExploreHeaderController {
    private View view;
    private List<FullPost> fullFeaturedPosts;
    private ImageButton searchButton;
    private ImageView featured1, featured2, featured3, featured4, featured5;
    private Activity parentActivity;
    private RelativeLayout categoriesLayout;
    private RelativeLayout categoryFirstRelLayout, categorySecondRelLayout, categoryThirdRelLayout;
    private ShapeableImageView categoryFirstImage, categorySecondImage, categoryThirdImage;
    private TextView categoryFirstText, categorySecondText, categoryThirdText, noSponsoredPostsText;
    private FrameLayout categoryFirstLayout, categorySecondLayout, categoryThirdLayout;
    private CategoryClass categoryClass;
    private Integer imageWidth;

    public ExploreHeaderController(View view, List<FullPost> fullFeaturedPosts, Activity parentActivity, CategoryClass categoryClass) {
        this.view = view;
        this.fullFeaturedPosts = fullFeaturedPosts;
        this.parentActivity = parentActivity;
        this.categoryClass = categoryClass;
        init();
    }


    private void init(){
        getViews();
        initSponsoredPage();
        initSearch();
        changeImageVisibilities();
        initCategories();
    }

    private void getViews(){
        searchButton = view.findViewById(R.id.ExploreHostSearchButton);
        featured1 = view.findViewById(R.id.ExploreSponsoredItemsImage1);
        featured2 = view.findViewById(R.id.ExploreSponsoredItemsImage2);
        featured3 = view.findViewById(R.id.ExploreSponsoredItemsImage3);
        featured4 = view.findViewById(R.id.ExploreSponsoredItemsImage4);
        featured5 = view.findViewById(R.id.ExploreSponsoredItemsImage5);
        categoriesLayout = view.findViewById(R.id.ExploreCategoriesImageList);
        categoryFirstRelLayout = view.findViewById(R.id.ExploreCategoriesFirstRelLayout);
        categoryFirstImage = view.findViewById(R.id.ExploreCategoriesFirstImage);
        categoryFirstText = view.findViewById(R.id.ExploreCategoriesFirstText);
        categorySecondRelLayout = view.findViewById(R.id.ExploreCategoriesSecondRelLayout);
        categorySecondImage = view.findViewById(R.id.ExploreCategoriesSecondImage);
        categorySecondText = view.findViewById(R.id.ExploreCategoriesSecondText);
        categoryThirdRelLayout = view.findViewById(R.id.ExploreCategoriesThirdRelLayout);
        categoryThirdImage = view.findViewById(R.id.ExploreCategoriesThirdImage);
        categoryThirdText = view.findViewById(R.id.ExploreCategoriesThirdText);
        categoryFirstLayout = view.findViewById(R.id.ExploreCategoriesFirstImageLayout);
        categorySecondLayout = view.findViewById(R.id.ExploreCategoriesSecondImageLayout);
        categoryThirdLayout = view.findViewById(R.id.ExploreCategoriesThirdImageLayout);
        noSponsoredPostsText = view.findViewById(R.id.ExploreSponsoredNoItems);
    }

    private void initSearch(){
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                Navigation.findNavController(view).navigate(R.id.action_global_searchFragment);
            }
        });
    }

    private void initSponsoredPage(){
        ConstraintLayout sponsoredImageConstraint = view.findViewById(R.id.ExploreSponsoredItemsLayout);
        ArrayList<ImageView> sponsoredImageArrayList = new ArrayList<>();
        Integer count = 5;
        ImageView sponsoredImage1 = view.findViewById(R.id.ExploreSponsoredItemsImage1);ImageView sponsoredImage2 = view.findViewById(R.id.ExploreSponsoredItemsImage2);ImageView sponsoredImage3 = view.findViewById(R.id.ExploreSponsoredItemsImage3);ImageView sponsoredImage4 = view.findViewById(R.id.ExploreSponsoredItemsImage4);ImageView sponsoredImage5 = view.findViewById(R.id.ExploreSponsoredItemsImage5);
        //Also Add the images to the arrayList to initialise
        //ImageView sponsoredImage6 = view.findViewById(R.id.ExploreSponsoredItemsImage6);ImageView sponsoredImage7 = view.findViewById(R.id.ExploreSponsoredItemsImage7);
        sponsoredImageArrayList.addAll(Arrays.asList(sponsoredImage1, sponsoredImage2, sponsoredImage3, sponsoredImage4, sponsoredImage5));
        sponsoredImageConstraint.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (sponsoredImageConstraint.getViewTreeObserver().isAlive()){
                    sponsoredImageConstraint.getViewTreeObserver().removeOnPreDrawListener(this);
                }

                Integer totalWidth = sponsoredImageConstraint.getMeasuredWidth();
                for (ImageView imageView: sponsoredImageArrayList){
                    ViewGroup.LayoutParams lp = imageView.getLayoutParams();
                    lp.width = totalWidth / (count + 1);
                    lp.height = totalWidth / (count + 1);
                    Integer eachSideMargin = totalWidth / ((count + 1) * count);

                    ViewGroup.MarginLayoutParams marginLP = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
                    marginLP.setMarginStart(eachSideMargin);
                    marginLP.setMarginEnd(eachSideMargin);
                    imageView.setLayoutParams(lp);
                    imageView.setLayoutParams(marginLP);
                }
                return false;
            }
        });
    }

    private void initCategories(){
        Context applicationContext = view.getContext().getApplicationContext();
        imageWidth = 0;

        //categoryFirstImage.setImageDrawable(categoryClass.getFirstChoiceDrawable());
        Drawable viewMoreDrawable = AppCompatResources.getDrawable(applicationContext, R.drawable.ic_baseline_keyboard_arrow_right_24);
        categoryThirdImage.setImageDrawable(viewMoreDrawable);
        Utils utils = new Utils(applicationContext);
        if (categoryThirdImage.getDrawable() != null){
            if (utils.loadBoolean(applicationContext.getString(R.string.dark_mode_pref),false)){
                categoryThirdImage.getDrawable().setTint(applicationContext.getResources().getColor(R.color.white));
            } else {
                categoryThirdImage.getDrawable().setTint(applicationContext.getResources().getColor(R.color.black));
            }
        }


        categoryFirstText.setText(categoryClass.getFirstChoiceName());
        categorySecondText.setText(categoryClass.getSecondChoiceName());
        categoryThirdText.setText("More");
        if (utils.loadBoolean(applicationContext.getString(R.string.dark_mode_pref),false)){
            categoryThirdText.setTextColor(applicationContext.getResources().getColor(R.color.white));
        } else {
            categoryThirdText.setTextColor(applicationContext.getResources().getColor(R.color.text_color));
        }

        categoriesLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
        {
            @Override
            public boolean onPreDraw()
            {
                if (categoriesLayout.getViewTreeObserver().isAlive())
                    categoriesLayout.getViewTreeObserver().removeOnPreDrawListener(this);



                Integer width = categoriesLayout.getMeasuredWidth();
                Integer imageWidth = (int) (width*.32);
                Integer imageMargin = (int) (width *.01);
                categoriesLayout.getLayoutParams().height  = imageWidth;

                //Changing image sizes
                ViewGroup.MarginLayoutParams firstMarginLP = (ViewGroup.MarginLayoutParams) categoryFirstLayout.getLayoutParams();
                ViewGroup.MarginLayoutParams secondMarginLP = (ViewGroup.MarginLayoutParams) categorySecondLayout.getLayoutParams();
                ViewGroup.MarginLayoutParams thirdMarginLP = (ViewGroup.MarginLayoutParams) categoryThirdLayout.getLayoutParams();
                categoryFirstLayout.getLayoutParams().height = imageWidth;
                categoryFirstLayout.getLayoutParams().width = imageWidth;
                categoryFirstImage.getLayoutParams().height = (int) ((int) imageWidth * .8);
                categoryFirstImage.getLayoutParams().width = (int) ((int) imageWidth * .8);
                categoryFirstText.getLayoutParams().height = (int) ((int) imageWidth * .2);

                categorySecondLayout.getLayoutParams().width = imageWidth;
                categorySecondLayout.getLayoutParams().height = imageWidth;
                categorySecondImage.getLayoutParams().height = (int) ((int) imageWidth * .8);
                categorySecondImage.getLayoutParams().width = (int) ((int) imageWidth * .8);
                categorySecondText.getLayoutParams().height = (int) ((int) imageWidth * .2);

                categoryThirdLayout.getLayoutParams().width = imageWidth;
                categoryThirdLayout.getLayoutParams().height = imageWidth;
                categoryThirdImage.getLayoutParams().height = (int) ((int) imageWidth * .8);
                categoryThirdImage.getLayoutParams().width = (int) ((int) imageWidth * .8);
                categoryThirdText.getLayoutParams().height = (int) ((int) imageWidth * .2);

                firstMarginLP.setMargins(0, 0, imageMargin, 0);
                secondMarginLP.setMargins(imageMargin, 0, imageMargin, 0);
                thirdMarginLP.setMargins(imageMargin, 0, 0, 0);

                Glide.with(categoryFirstImage)
                        .load(categoryClass.getFirstChoiceDrawable())
                        .priority(Priority.IMMEDIATE)
                        .override(imageWidth, imageWidth)
                        .into(categoryFirstImage);
                Glide.with(categorySecondImage)
                        .load(categoryClass.getSecondChoiceDrawable())
                        .priority(Priority.IMMEDIATE)
                        .override(imageWidth, imageWidth)
                        .into(categorySecondImage);


                ExploreHeaderController.this.imageWidth = imageWidth;



                return true;
            }
        });
        categoryFirstLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("category", categoryClass.getFirstChoiceName());
                Navigation.findNavController(view).navigate(R.id.action_exploreHostFragment_to_exploreCategoriesPostFragment, bundle);
            }
        });
        categorySecondLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("category", categoryClass.getSecondChoiceName());
                Navigation.findNavController(view).navigate(R.id.action_exploreHostFragment_to_exploreCategoriesPostFragment, bundle);
            }
        });
        categoryThirdLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("categoryImageSize", imageWidth);
                Navigation.findNavController(view).navigate(R.id.action_exploreHostFragment_to_exploreCategorySelectorFragment, bundle);
            }
        });
    }

    private void changeImageVisibilities() {
        switch (fullFeaturedPosts.size()) {
            case 0:
                featured1.setImageDrawable(null);
                featured1.setBackgroundColor(Color.TRANSPARENT);
                featured2.setImageDrawable(null);
                featured2.setBackgroundColor(Color.TRANSPARENT);
                featured3.setImageDrawable(null);
                featured3.setBackgroundColor(Color.TRANSPARENT);
                featured4.setImageDrawable(null);
                featured4.setBackgroundColor(Color.TRANSPARENT);
                featured5.setImageDrawable(null);
                featured5.setBackgroundColor(Color.TRANSPARENT);
                featured1.setEnabled(false);
                featured2.setEnabled(false);
                featured3.setEnabled(false);
                featured4.setEnabled(false);
                featured5.setEnabled(false);
                noSponsoredPostsText.setVisibility(View.VISIBLE);
                break;
            case 1:
                imageGlide(fullFeaturedPosts.get(0).getStandardPost().getImageURL(), featured1);
                featured1.setBackgroundColor(Color.TRANSPARENT);
                //featured1.setBackgroundColor(Color.);
                featured2.setImageDrawable(null);
                featured2.setBackgroundColor(Color.TRANSPARENT);
                featured3.setImageDrawable(null);
                featured3.setBackgroundColor(Color.TRANSPARENT);
                featured4.setImageDrawable(null);
                featured4.setBackgroundColor(Color.TRANSPARENT);
                featured5.setImageDrawable(null);
                featured5.setBackgroundColor(Color.TRANSPARENT);
                featured1.setEnabled(true);
                featured2.setEnabled(false);
                featured3.setEnabled(false);
                featured4.setEnabled(false);
                featured5.setEnabled(false);
                noSponsoredPostsText.setVisibility(View.GONE);
                break;
            case 2:
                imageGlide(fullFeaturedPosts.get(0).getStandardPost().getImageURL(), featured1);
                featured1.setBackgroundColor(Color.TRANSPARENT);
                //featured1.setBackgroundColor(Color.);
                imageGlide(fullFeaturedPosts.get(1).getStandardPost().getImageURL(), featured2);
                featured2.setBackgroundColor(Color.TRANSPARENT);
                featured3.setImageDrawable(null);
                featured3.setBackgroundColor(Color.TRANSPARENT);
                featured4.setImageDrawable(null);
                featured4.setBackgroundColor(Color.TRANSPARENT);
                featured5.setImageDrawable(null);
                featured5.setBackgroundColor(Color.TRANSPARENT);
                featured1.setEnabled(true);
                featured2.setEnabled(true);
                featured3.setEnabled(false);
                featured4.setEnabled(false);
                featured5.setEnabled(false);
                noSponsoredPostsText.setVisibility(View.GONE);
                break;
            case 3:
                imageGlide(fullFeaturedPosts.get(0).getStandardPost().getImageURL(), featured1);
                featured1.setBackgroundColor(Color.TRANSPARENT);
                imageGlide(fullFeaturedPosts.get(1).getStandardPost().getImageURL(), featured2);
                featured2.setBackgroundColor(Color.TRANSPARENT);
                imageGlide(fullFeaturedPosts.get(2).getStandardPost().getImageURL(), featured3);
                featured3.setBackgroundColor(Color.TRANSPARENT);
                featured4.setImageDrawable(null);
                featured4.setBackgroundColor(Color.TRANSPARENT);
                featured5.setImageDrawable(null);
                featured5.setBackgroundColor(Color.TRANSPARENT);
                featured1.setEnabled(true);
                featured2.setEnabled(true);
                featured3.setEnabled(true);
                featured4.setEnabled(false);
                featured5.setEnabled(false);
                noSponsoredPostsText.setVisibility(View.GONE);
                break;
            case 4:
                imageGlide(fullFeaturedPosts.get(0).getStandardPost().getImageURL(), featured1);
                featured1.setBackgroundColor(Color.TRANSPARENT);
                imageGlide(fullFeaturedPosts.get(1).getStandardPost().getImageURL(), featured2);
                featured2.setBackgroundColor(Color.TRANSPARENT);
                imageGlide(fullFeaturedPosts.get(2).getStandardPost().getImageURL(), featured3);
                featured3.setBackgroundColor(Color.TRANSPARENT);
                imageGlide(fullFeaturedPosts.get(3).getStandardPost().getImageURL(), featured4);
                featured4.setBackgroundColor(Color.TRANSPARENT);
                featured5.setImageDrawable(null);
                featured5.setBackgroundColor(Color.TRANSPARENT);
                featured1.setEnabled(true);
                featured2.setEnabled(true);
                featured3.setEnabled(true);
                featured4.setEnabled(true);
                featured5.setEnabled(false);
                noSponsoredPostsText.setVisibility(View.GONE);
                break;
            default:
                imageGlide(fullFeaturedPosts.get(0).getStandardPost().getImageURL(), featured1);
                featured1.setBackgroundColor(Color.TRANSPARENT);
                imageGlide(fullFeaturedPosts.get(1).getStandardPost().getImageURL(), featured2);
                featured2.setBackgroundColor(Color.TRANSPARENT);
                imageGlide(fullFeaturedPosts.get(2).getStandardPost().getImageURL(), featured3);
                featured3.setBackgroundColor(Color.TRANSPARENT);
                imageGlide(fullFeaturedPosts.get(3).getStandardPost().getImageURL(), featured4);
                featured4.setBackgroundColor(Color.TRANSPARENT);
                imageGlide(fullFeaturedPosts.get(4).getStandardPost().getImageURL(), featured5);
                featured5.setBackgroundColor(Color.TRANSPARENT);
                featured1.setEnabled(true);
                featured2.setEnabled(true);
                featured3.setEnabled(true);
                featured4.setEnabled(true);
                featured5.setEnabled(true);
                noSponsoredPostsText.setVisibility(View.GONE);
                break;
        }
        setUpFeaturedClicks();
    }

    private void setUpFeaturedClicks(){
        featured1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FeaturedClick(1);
            }
        });
        featured2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FeaturedClick(2);
            }
        });
        featured3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FeaturedClick(3);
            }
        });
        featured4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FeaturedClick(4);
            }
        });
        featured5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FeaturedClick(5);
            }
        });
    }

    private void FeaturedClick(Integer numberClicked){
        ExplorePostDialog explorePostDialog = new ExplorePostDialog(parentActivity, fullFeaturedPosts.get(numberClicked - 1), parentActivity);
        explorePostDialog.initDialog();
    }


    private void imageGlide(String imageLink, ImageView imageView){
            if (imageLink != null){
                Glide.with(imageView)
                        .load(imageLink)
                        .circleCrop()
                        .into(imageView);
            }

        }

    public void updateCategories(CategoryClass categoryClass){
        this.categoryClass = categoryClass;
        categoryFirstText.setText(categoryClass.getFirstChoiceName());
        categorySecondText.setText(categoryClass.getSecondChoiceName());
        Glide.with(categoryFirstImage)
                .load(categoryClass.getFirstChoiceDrawable())
                .priority(Priority.IMMEDIATE)
                .override(imageWidth, imageWidth)
                .into(categoryFirstImage);
        Glide.with(categorySecondImage)
                .load(categoryClass.getSecondChoiceDrawable())
                .priority(Priority.IMMEDIATE)
                .override(imageWidth, imageWidth)
                .into(categorySecondImage);
    }


    public void alterFeaturedPosts(List<FullPost> fullFeaturedPosts){
        this.fullFeaturedPosts = fullFeaturedPosts;
        changeImageVisibilities();
    }


}
