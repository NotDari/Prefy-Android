package com.daribear.prefy.customClasses;

import java.util.ArrayList;

import lombok.Data;

@Data
public class PostListContainer {
    private ArrayList<StandardPost> postList;
    private Integer pageNumber;
}
