package com.daribear.prefy.Popular;

/**
 * Interface called when a vote has occured on a popular post.
 */
public interface PopularPostVote{
    /**
     * Calls when the user has voted on a popular post.
     * @param cooldown whether there is a cooldown
     * @param removeVote whether to remove the vote
     */
    void voted(Boolean cooldown, Boolean removeVote);
}
