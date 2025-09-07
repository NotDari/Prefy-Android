package com.daribear.prefy.Comments;

import lombok.Data;

/**
 * Wrapper class for a FullComment used in RecyclerView
 * Tracks how many replies are currently shown and whether it is minimised
 */
@Data
public class FullRecComment {
    private FullComment fullComment;
    private Integer repliesShown;
    private Boolean minimised;
}
