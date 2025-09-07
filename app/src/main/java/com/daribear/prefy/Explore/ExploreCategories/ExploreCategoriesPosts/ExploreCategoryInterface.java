package com.daribear.prefy.Explore.ExploreCategories.ExploreCategoriesPosts;

import com.daribear.prefy.Explore.ExplorePostSet;

/**
 * Interface for callback when explore category posts are retrieved
 */
public interface ExploreCategoryInterface {
    /**
     * Called when category posts retrieval is complete
     * @param successful whether the retrieval was successful or not
     * @param explorePostSet the set of posts retrieved
     */
    void Completed(Boolean successful, ExplorePostSet explorePostSet);
}
