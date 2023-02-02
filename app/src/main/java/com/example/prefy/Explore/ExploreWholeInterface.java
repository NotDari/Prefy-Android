package com.example.prefy.Explore;

import com.example.prefy.customClasses.FullPost;

import java.util.ArrayList;

public interface ExploreWholeInterface  {
    void completed(Boolean successful, Boolean update, ExplorePostSet explorePostSet, ArrayList<FullPost> fullFeaturedPostArrayList);
}
