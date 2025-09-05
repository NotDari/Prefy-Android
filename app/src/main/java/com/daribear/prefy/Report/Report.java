package com.daribear.prefy.Report;


import lombok.Data;

/**
 * The report data entity which represents the users report of a post or comment.
 */
@Data
public class Report {
    private String type;
    private Long postId;
    private Long userId;
    private Long commentId;
    private String repCategory;
    private Double creationDate;
}
