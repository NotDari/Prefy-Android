package com.daribear.prefy.Search;

import com.daribear.prefy.Profile.User;

import java.util.ArrayList;

/**
 * The delegate which is responsible for alerting of the completion of the topusers retriever thread.
 */
public interface SearchUsersTopDelegate {
    /**
     * Alerts whether the retrieval was successful and provides the data if it was.
     *
     * @param successful if the retrieval was successful
     * @param update if it was an update or not
     * @param searchUserArrayList the datalist
     */
    void topCompleted(Boolean successful, Boolean update, ArrayList<User> searchUserArrayList);
}
