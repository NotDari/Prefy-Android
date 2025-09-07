package com.daribear.prefy.Explore;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Data entity representing a category class to store a pair of explore category choices
 * Each choice chosen has a name and a drawable resource id to show the category image
 */
@Getter
@Setter
@AllArgsConstructor
public class CategoryClass {
    private String firstChoiceName, secondChoiceName;
    private Integer firstChoiceDrawable, secondChoiceDrawable;
}
