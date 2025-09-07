package com.daribear.prefy.Popular.PopularViewModel;

import com.daribear.prefy.Popular.PopularActivity;

/**
 * Interface for receiving the popular activites from the database.
 */
public interface PopularActivityRetrieverInterface {
    /**
     * Callback for when the popular activity retriever is complete
     * @param successful whether it succesfully retrieved the data
     * @param popularActivity the popularActivity retrieved
     */
    void taskCompleted(Boolean successful, PopularActivity popularActivity);
}
