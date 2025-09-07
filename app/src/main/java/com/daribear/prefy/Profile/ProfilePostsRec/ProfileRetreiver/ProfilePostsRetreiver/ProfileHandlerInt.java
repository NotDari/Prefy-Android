package com.daribear.prefy.Profile.ProfilePostsRec.ProfileRetreiver.ProfilePostsRetreiver;

import com.daribear.prefy.Profile.ProfilePostsRec.ProfileRetreiver.WholeProfile;

/**
 * Interface which is used for the profileExecutor. Alerts when the retriever is complete.
 */
public interface ProfileHandlerInt {
    /**
     * Called when the retriever is complete, whether it was successful or not
     * @param successful whether it was successful
     * @param wholeProfile the data retrieved.
     */
    void taskDone(Boolean successful, WholeProfile wholeProfile);
}
