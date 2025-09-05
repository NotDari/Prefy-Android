package com.daribear.prefy.Profile.ProfilePostsRec.ProfileRetreiver;


import com.daribear.prefy.Profile.User;
import com.daribear.prefy.customClasses.Containers.PostListContainer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A class which contains a whole profile. A user and their list of posts.
 */
@Getter
@Setter
@NoArgsConstructor
public class WholeProfile {
    private PostListContainer postListContainer;
    private User user;
}
