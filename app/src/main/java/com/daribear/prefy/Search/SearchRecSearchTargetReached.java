package com.daribear.prefy.Search;

/**
 * Interface to notify when a specific search target in the RecyclerView is reached.
 *
 * Typically used for search results pagination or loading more data when the user
 * scrolls to the top of the search results list.
 */
public interface SearchRecSearchTargetReached {
    /**
     * Called when the target in the search RecyclerView is reached.
     *
     * @param lastUsername The username of the last item reached, useful for pagination or tracking.
     */
    void topReached(String lastUsername);
}
