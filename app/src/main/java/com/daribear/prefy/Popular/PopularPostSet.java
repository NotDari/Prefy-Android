package com.daribear.prefy.Popular;

import com.daribear.prefy.Profile.User;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PopularPostSet {
    private ArrayList<PopularPost> postList;
    private ArrayList<User> userList;



}
