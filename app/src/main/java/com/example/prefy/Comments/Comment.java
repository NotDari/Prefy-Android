package com.example.prefy.Comments;


import android.os.Parcel;
import android.os.Parcelable;

import com.example.prefy.Profile.User;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class Comment implements Parcelable {
    private Double creationDate;
    private Long userId;
    private String text;
    private String replyUsername;
    private Long replyID;
    private Long commentId;
    private Long postId;
    private User user;


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
