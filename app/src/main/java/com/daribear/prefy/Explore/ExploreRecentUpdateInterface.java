package com.daribear.prefy.Explore;

/**
 * Interface which notifies when the recent posts update process is completed, so receives a callback.
 */
public interface ExploreRecentUpdateInterface {
    /**
     * Called when the retrieval is complete,
     * @param successful whether it was successful
     * @param additionalPosts the additional posts
     */
    void completed(Boolean successful, ExplorePostSet additionalPosts);
}
