package com.daribear.prefy.customClasses.Containers;

import com.daribear.prefy.customClasses.Posts.StandardPost;

import java.util.ArrayList;

import lombok.Data;

@Data
public class PostListContainer {
    private ArrayList<StandardPost> postList;
    private Integer pageNumber;
}
