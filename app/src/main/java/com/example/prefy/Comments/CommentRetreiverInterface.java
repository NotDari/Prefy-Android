package com.example.prefy.Comments;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public interface CommentRetreiverInterface {

    void complete(Boolean successful, Boolean update, ArrayList<FullRecComment> commentList, Integer pageNumber);
}
