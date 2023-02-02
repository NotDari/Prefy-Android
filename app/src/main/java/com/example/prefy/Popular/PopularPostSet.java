package com.example.prefy.Popular;

import com.example.prefy.Profile.User;
import com.example.prefy.customClasses.StandardPost;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PopularPostSet {
    private ArrayList<StandardPost> postList;
    private ArrayList<User> userList;

}
