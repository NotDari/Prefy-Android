package com.daribear.prefy.DeleteDialog;

/**
 * A delegate interface to notify when an item has been deleted.
 * Implement this interface in classes that need to perform an action
 * after a deletion event occurs.
 */
public interface DeleteDelegate{
    /**
     * Called when the delete button is clicked
     */
    void itemDeleted();
}
