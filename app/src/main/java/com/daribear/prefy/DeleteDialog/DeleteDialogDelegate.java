package com.daribear.prefy.DeleteDialog;

/**
 * A delegate interface for handling delete dialog actions.
 * Implement this interface in classes that need to respond when
 * the delete button in a dialog is clicked.
 */
public interface DeleteDialogDelegate {
    /**
     * Called when the delete button is clicked
     */
    void deleteClicked();
}
