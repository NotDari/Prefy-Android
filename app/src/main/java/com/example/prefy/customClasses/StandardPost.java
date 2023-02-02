package com.example.prefy.customClasses;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StandardPost implements Parcelable {
    //TODO Make the leftVotes, RightVotes and allVotes a Long
    private Long userId;
    private Integer leftVotes, rightVotes;
    private String imageURL;
    private String question;
    private Integer commentsNumber;
    private Double creationDate;
    private Long postId;
    private Integer allVotes;
    private String currentVote;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StandardPost that = (StandardPost) o;
        return postId.equals(that.postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId);
    }

    protected StandardPost(Parcel in) {
        userId = in.readLong();
        leftVotes = in.readInt();
        rightVotes = in.readInt();
        imageURL = in.readString();
        question = in.readString();
        commentsNumber = in.readInt();
        creationDate = in.readDouble();
        postId = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(userId);
        dest.writeInt(leftVotes);
        dest.writeInt(rightVotes);
        dest.writeString(imageURL);
        dest.writeString(question);
        dest.writeInt(commentsNumber);
        dest.writeDouble(creationDate);
        dest.writeLong(postId);

    }



    @SuppressWarnings("unused")
    public static final Parcelable.Creator<StandardPost> CREATOR = new Parcelable.Creator<StandardPost>() {
        @Override
        public StandardPost createFromParcel(Parcel in) {
            return new StandardPost(in);
        }

        @Override
        public StandardPost[] newArray(int size) {
            return new StandardPost[size];
        }
    };
}
