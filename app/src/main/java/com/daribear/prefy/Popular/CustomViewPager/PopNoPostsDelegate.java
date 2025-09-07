package com.daribear.prefy.Popular.CustomViewPager;

/**
 * Delegate interface for handling the state when no additaional posts
 * are available to load or display in the popular posts section
 */
public interface PopNoPostsDelegate {
    /**
     * Called when there are no more popular posts
     */
    void noMorePosts();
}
