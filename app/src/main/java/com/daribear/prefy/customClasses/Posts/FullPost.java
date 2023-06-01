package com.daribear.prefy.customClasses.Posts;

import com.daribear.prefy.Profile.User;
import com.daribear.prefy.customClasses.Posts.StandardPost;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FullPost {
    private StandardPost standardPost;
    private User user;

}
