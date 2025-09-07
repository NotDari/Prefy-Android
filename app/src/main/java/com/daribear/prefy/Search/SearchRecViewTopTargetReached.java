package com.daribear.prefy.Search;

/**
 * Interface to notify the delegate when the top or bottom of the recyclerview has been reached.
 */
public interface SearchRecViewTopTargetReached {
    /**
     * Called when the top/bottom of the RecyclerView is reached.
     */
    void endReached();
}
