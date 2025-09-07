package com.daribear.prefy.Utils.PlayIntegrity;

/**
 * Interface used as a callback for the playIntegrity results
 */
public interface IntegrityDelegate {
    /**
     * The play integrity request is complete, regardless of whether it was successful.
     */
    void complete(IntegrityResponse integrityResponse);
}
