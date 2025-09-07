package com.daribear.prefy.Popular;

import com.daribear.prefy.Profile.User;
import com.daribear.prefy.customClasses.Posts.PopularPost;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;


/**
 * The data entity representing the popularpostset. Contains the list of posts and their users.
 */
@Getter
@Setter
public class PopularPostSet {
    private ArrayList<PopularPost> postList;
    private ArrayList<User> userList;

}
