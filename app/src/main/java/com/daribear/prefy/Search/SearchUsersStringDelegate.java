package com.daribear.prefy.Search;

import com.daribear.prefy.Profile.User;

import java.util.ArrayList;

/**
 * The delegate which is responsible for alerting of the completion of the get users by search retriever thread.
 */
public interface SearchUsersStringDelegate {
    /**
     * Alerts whether the retrieval was successful and provides the data if it was.
     *
     * @param successful if the retrieval was successful
     * @param update if it was an update or not
     * @param text text of the search
     * @param searchUserArrayList the datalist
     */
    void stringCompleted(Boolean successful, Boolean update, String text, ArrayList<User> searchUserArrayList);
}
