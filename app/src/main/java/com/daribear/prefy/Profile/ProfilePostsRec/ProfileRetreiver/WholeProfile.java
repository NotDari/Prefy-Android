package com.daribear.prefy.Profile.ProfilePostsRec.ProfileRetreiver;


import com.daribear.prefy.Profile.User;
import com.daribear.prefy.customClasses.PostListContainer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WholeProfile {
    private PostListContainer postListContainer;
    private User user;
}
