package com.example.prefy.Search;

import com.example.prefy.Profile.User;

import java.util.ArrayList;

public interface SearchUsersStringDelegate {
    void stringCompleted(Boolean successful, Boolean update, String text, ArrayList<User> searchUserArrayList);
}
