package com.daribear.prefy.Comments;

import java.util.ArrayList;

public interface CommentRetreiverInterface {

    void complete(Boolean successful, Boolean update, ArrayList<FullRecComment> commentList, Integer pageNumber);
}
