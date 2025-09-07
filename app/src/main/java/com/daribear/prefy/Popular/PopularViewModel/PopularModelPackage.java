package com.daribear.prefy.Popular.PopularViewModel;


import com.daribear.prefy.Popular.PopularPostSet;
import com.daribear.prefy.customClasses.Posts.FullPost;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A data entity which represents the PopularModel, contains a list of full posts and the retrieval type
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PopularModelPackage implements Cloneable{

    private ArrayList<FullPost> fullPostList;
    private String retrievalType;


    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
