package com.daribear.prefy.Popular.PopularViewModel;

import com.daribear.prefy.Popular.PopularPostSet;
import com.daribear.prefy.customClasses.Posts.FullPost;

import java.lang.reflect.Array;
import java.util.ArrayList;

public interface RetreivePopularDataInterface {

    void taskComplete(Boolean successful, ArrayList<FullPost> fullPostList, String type);
}
