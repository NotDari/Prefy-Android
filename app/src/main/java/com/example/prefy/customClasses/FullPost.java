package com.example.prefy.customClasses;

import com.example.prefy.Profile.User;


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
