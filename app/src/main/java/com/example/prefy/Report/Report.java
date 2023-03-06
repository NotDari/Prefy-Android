package com.example.prefy.Report;


import lombok.Data;

@Data
public class Report {
    private String type;
    private Long postId;
    private Long userId;
    private Long commentId;
    private String repCategory;
    private Double creationDate;
}
