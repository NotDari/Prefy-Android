package com.daribear.prefy.Popular.PopularViewModel;


import com.daribear.prefy.Popular.PopularPostSet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PopularModelPackage implements Cloneable{

    private PopularPostSet popularPostSet;
    private String retrievalType;


    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
