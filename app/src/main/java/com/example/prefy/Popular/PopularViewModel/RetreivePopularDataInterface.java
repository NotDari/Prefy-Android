package com.example.prefy.Popular.PopularViewModel;

import com.example.prefy.Popular.PopularPostSet;

public interface RetreivePopularDataInterface {

    void taskComplete(Boolean successful, PopularPostSet popularPostSet, Boolean update);
}
