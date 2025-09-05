package com.daribear.prefy.customClasses.Posts;


import com.daribear.prefy.customClasses.Posts.StandardPost;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A popular post extends a standard post, but also contains the date it was made a popular post.
 * Used to retrieve more popular posts from database, as it uses the last popular post date.
 */
@Getter
@Setter
@NoArgsConstructor
public class PopularPost extends StandardPost{
    private Double popularDate;
}
