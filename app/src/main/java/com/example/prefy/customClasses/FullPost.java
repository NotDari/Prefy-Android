package com.example.prefy.customClasses;

import com.example.prefy.Profile.User;


import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FullPost {
    private StandardPost standardPost;
    private User user;
}
