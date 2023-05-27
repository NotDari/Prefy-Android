package com.daribear.prefy.Utils.GetFollowing;

import java.util.ArrayList;
import java.util.HashMap;

public interface GetFollowingDelegate {
    void completed(Boolean successful, HashMap<Long, Boolean> followList, String type);
}
