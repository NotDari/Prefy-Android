package com.example.prefy.Popular;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PopularActivity {
    Integer totalActivities;
    Integer votesCount;
    Integer commentsCount;

    public void subtractVotes(){
        if (totalActivities != null & votesCount != null) {
            if (totalActivities - votesCount > 0) {
                totalActivities -= votesCount;
                votesCount = 0;
            } else {
                totalActivities = 0;
                votesCount = 0;
                commentsCount = 0;
            }
        }
    }
    public void subtractComments(){
        if (totalActivities != null & commentsCount != null) {
            if (totalActivities - commentsCount > 0) {
                totalActivities -= commentsCount;
                commentsCount = 0;
            } else {
                totalActivities = 0;
                votesCount = 0;
                commentsCount = 0;
            }
        }
    }
}
