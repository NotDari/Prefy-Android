package com.daribear.prefy.Explore;

import com.daribear.prefy.customClasses.Posts.FullPost;

import lombok.Getter;
import lombok.Setter;


/**
 * Class which contains the full featured post. Contains the featured post(post key) and the post itself.
 */
@Getter
@Setter
public class FullFeaturedPost {
    private FeaturedPost featuredPost;
    private FullPost fullPost;
}
