package com.daribear.prefy.Comments;

import java.util.ArrayList;

/**
 * Interface used to notify when comments have been retrieved from the server or local storage
 */
public interface CommentRetreiverInterface {
    /**
     * Called when the comment retrieval task is complete
     * @param successful whether the retrieval was successful
     * @param update whether this was an update to existing comments
     * @param commentList list of retrieved comments
     * @param pageNumber the page number of comments retrieved (for pagination)
     */
    void complete(Boolean successful, Boolean update, ArrayList<FullRecComment> commentList, Integer pageNumber);
}
