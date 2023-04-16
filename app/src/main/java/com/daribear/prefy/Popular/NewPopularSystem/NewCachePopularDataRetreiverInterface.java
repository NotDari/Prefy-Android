package com.daribear.prefy.Popular.NewPopularSystem;

import com.daribear.prefy.Popular.PopularPostSet;

import java.util.ArrayList;

public interface NewCachePopularDataRetreiverInterface {
    void completed(Boolean successful, PopularPostSet popularPostSet, ArrayList<Long> avoidList);
}
