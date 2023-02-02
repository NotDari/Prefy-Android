package com.example.prefy.Profile.ProfilePostsRec.ProfileRetreiver;


import com.example.prefy.Profile.User;
import com.example.prefy.customClasses.PostListContainer;
import com.example.prefy.customClasses.StandardPost;

import java.util.ArrayList;

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
