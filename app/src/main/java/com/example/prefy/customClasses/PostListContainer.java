package com.example.prefy.customClasses;

import java.lang.reflect.Array;
import java.util.ArrayList;

import lombok.Data;

@Data
public class PostListContainer {
    private ArrayList<StandardPost> postList;
    private Integer pageNumber;
}
