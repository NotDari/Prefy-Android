package com.daribear.prefy.Explore;


import com.daribear.prefy.customClasses.Posts.FullPost;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

/**
 * A data entity representing the explore post set.
 * Contains a list of full posts.
 */
@Getter
@Setter
public class ExplorePostSet {
    private ArrayList<FullPost> postList;
}
