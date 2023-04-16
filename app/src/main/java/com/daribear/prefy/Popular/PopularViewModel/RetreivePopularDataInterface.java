package com.daribear.prefy.Popular.PopularViewModel;

import com.daribear.prefy.Popular.PopularPostSet;

public interface RetreivePopularDataInterface {

    void taskComplete(Boolean successful, PopularPostSet popularPostSet, String type);
}
