package com.daribear.prefy.Comments;


import android.os.Parcel;
import android.os.Parcelable;

import com.daribear.prefy.Profile.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The data entity class which represents a comment.
 */
@Getter
@Setter
@NoArgsConstructor

public class Comment implements Parcelable {
    private Double creationDate;
    private Long userId;
    private String text;
    private String replyUsername;
    private Long replyID;
    private Long subReplyID;
    private Long commentId;
    private Long postId;
    private User user;

    /**
     * Creates a comment from a parcel
     * @param in parcel to create the comment from
     */
    protected Comment(Parcel in) {
        if (in.readByte() == 0) {
            creationDate = null;
        } else {
            creationDate = in.readDouble();
        }
        userId = in.readLong();
        text = in.readString();
        replyUsername = in.readString();
        replyID = in.readLong();
        commentId = in.readLong();
        postId = in.readLong();
        user = in.readParcelable(User.class.getClassLoader());
    }
    /**
     * Parcelable.Creator implementation required for Android parcelable support.
     */
    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Turns the instance of this class into a parcel
     * @param dest destination parcel to write into
     * @param flags flags for the parcel
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (creationDate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(creationDate);
        }
        dest.writeLong(userId);
        dest.writeString(text);
        dest.writeString(replyUsername);
        dest.writeLong(replyID);
        dest.writeLong(commentId);
        dest.writeLong(postId);
        dest.writeParcelable(user, flags);
    }
}
