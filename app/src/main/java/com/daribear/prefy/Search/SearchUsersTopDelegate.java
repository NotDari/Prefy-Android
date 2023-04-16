package com.daribear.prefy.Search;

import com.daribear.prefy.Profile.User;

import java.util.ArrayList;

public interface SearchUsersTopDelegate {
    void topCompleted(Boolean successful, Boolean update, ArrayList<User> searchUserArrayList);
}
