package com.daribear.prefy.customClasses.Containers;

import com.daribear.prefy.customClasses.Posts.StandardPost;

import java.util.ArrayList;

import lombok.Data;

/**
 * A data entity representing a list of standard posts and the page number.
 * Useful for pagination
 */
@Data
public class PostListContainer {
    private ArrayList<StandardPost> postList;
    private Integer pageNumber;
}
