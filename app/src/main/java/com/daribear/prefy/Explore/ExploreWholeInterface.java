package com.daribear.prefy.Explore;

import com.daribear.prefy.customClasses.Posts.FullPost;

import java.util.ArrayList;

public interface ExploreWholeInterface  {
    void completed(Boolean successful, Boolean update, ExplorePostSet explorePostSet, ArrayList<FullPost> fullFeaturedPostArrayList);
}
