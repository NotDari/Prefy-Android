package com.daribear.prefy.customClasses.Posts;


import com.daribear.prefy.customClasses.Posts.StandardPost;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PopularPost extends StandardPost{
    private Double popularDate;
}
