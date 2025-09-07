package com.daribear.prefy.Popular.PopularViewModel;

import com.daribear.prefy.Popular.PopularPostSet;
import com.daribear.prefy.customClasses.Posts.FullPost;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * The interface which is used to signal whether the popular data has been retrieved.
 */
public interface RetreivePopularDataInterface {

    /**
     * Called when the retrieval has completed regardless of whether it was successful.
     * @param successful whether it was successful
     * @param fullPostList the list of posts retrieved
     * @param type the type retrieved
     */
    void taskComplete(Boolean successful, ArrayList<FullPost> fullPostList, String type);
}
