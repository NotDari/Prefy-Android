package com.daribear.prefy.SubmitPost;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a basic post object with minimal initial data.
 * This class is used when creating a new post or handling placeholder posts
 * before all data is fully populated.
 */
@Getter
@Setter
public class EmptyPost {
    private String uid;
    private Long allVotes;
    private String imageURL;
    private String question;
    private Long creationDate;
    private String creationDate_category;
    private String creationDate_uid;

    //Constructor
    public EmptyPost(String uid, String imageURL, String question, Long creationDate, String creationDate_category, String creationDate_uid) {
        this.uid = uid;
        this.allVotes = 0L;
        this.imageURL = imageURL;
        this.question = question;
        this.creationDate = creationDate;
        this.creationDate_category = creationDate_category;
        this.creationDate_uid = creationDate_uid;
    }
}
