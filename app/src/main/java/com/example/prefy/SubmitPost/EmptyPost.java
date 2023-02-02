package com.example.prefy.SubmitPost;

import lombok.Getter;
import lombok.Setter;

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
