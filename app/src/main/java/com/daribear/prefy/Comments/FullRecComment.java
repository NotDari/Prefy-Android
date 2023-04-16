package com.daribear.prefy.Comments;

import lombok.Data;

@Data
public class FullRecComment {
    private FullComment fullComment;
    private Integer repliesShown;
    private Boolean minimised;
}
