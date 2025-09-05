package com.daribear.prefy.Utils.Permissions;

/**
 * Interface for handling the result of a permission request from the user.
 */
public interface PermissionReceived {
    /**
     * Called when a permission request is complete.
     * @param Granted whether the permission was granted
     */
    void granted(Boolean Granted);
}
