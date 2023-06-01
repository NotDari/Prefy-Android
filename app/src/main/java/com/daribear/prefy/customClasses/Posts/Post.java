package com.daribear.prefy.customClasses.Posts;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post implements Parcelable {
    private Integer leftVotes, rightVotes;
    private String imageURL;
    private String question;
    private Integer commentsNumber;
    private Double creationDate;
    private Long postId;
    private Integer allVotes;
    private String currentVote;


    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post that = (Post) o;
        return this.postId.equals(that.postId);
    }

    public Post(Parcel in) {
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
        dest.writeInt(leftVotes);
        dest.writeInt(rightVotes);
        dest.writeString(imageURL);
        dest.writeString(question);
        dest.writeInt(commentsNumber);
        dest.writeDouble(creationDate);
        dest.writeLong(postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId);
    }




}
