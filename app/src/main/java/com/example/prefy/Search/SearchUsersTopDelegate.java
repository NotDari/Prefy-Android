package com.example.prefy.Search;

import com.example.prefy.Profile.User;

import java.util.ArrayList;

public interface SearchUsersTopDelegate {
    void topCompleted(Boolean successful, Boolean update, ArrayList<User> searchUserArrayList);
}
