package com.daribear.prefy.Search;

import com.daribear.prefy.Profile.User;

import java.util.ArrayList;

public interface SearchUsersStringDelegate {
    void stringCompleted(Boolean successful, Boolean update, String text, ArrayList<User> searchUserArrayList);
}
