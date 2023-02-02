package com.example.prefy.Popular.PopularViewModel;

import com.example.prefy.Popular.PopularPostSet;

public interface CachePopularDataRetreiverInterface {
    void completed(Boolean successful, PopularPostSet popularPostSet);
}
