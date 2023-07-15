package com.daribear.prefy.Popular;

import com.daribear.prefy.Popular.PopularPostSet;
import com.daribear.prefy.customClasses.Posts.FullPost;

import java.lang.reflect.Array;
import java.util.ArrayList;

public interface NewCachePopularDataRetreiverInterface {
    void completed(Boolean successful, ArrayList<FullPost> postList, ArrayList<Long> avoidList);
}
