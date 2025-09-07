package com.daribear.prefy.Explore;

import com.daribear.prefy.customClasses.Posts.FullPost;

import java.util.ArrayList;

/**
 * Interface to notify when the recent posts update process if complete
 */
public interface ExploreWholeInterface  {
    void completed(Boolean successful, Boolean update, ExplorePostSet explorePostSet, ArrayList<FullPost> fullFeaturedPostArrayList);
}
