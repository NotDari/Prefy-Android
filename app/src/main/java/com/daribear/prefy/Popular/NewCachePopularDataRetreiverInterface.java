package com.daribear.prefy.Popular;

import com.daribear.prefy.Popular.PopularPostSet;
import com.daribear.prefy.customClasses.Posts.FullPost;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Interface which handles the completion of the retrieval of the popular posts from cache
 */
public interface NewCachePopularDataRetreiverInterface {
    /**
     * Called when the popular posts retriever from cache is complete
     * @param successful whether it was successfull or not
     * @param postList list of popular posts
     * @param avoidList list of posts to avoid retrieving from the database
     */
    void completed(Boolean successful, ArrayList<FullPost> postList, ArrayList<Long> avoidList);
}
