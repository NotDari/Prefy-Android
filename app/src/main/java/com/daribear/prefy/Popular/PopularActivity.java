package com.daribear.prefy.Popular;

import lombok.Getter;
import lombok.Setter;

/**
 * Class which represents the activity statistics for a popular post.
 * Contains the totalActivities, and the votes and comments Counts.
 */
@Getter
@Setter
public class PopularActivity {
    Integer totalActivities;
    Integer votesCount;
    Integer commentsCount;

}
