package com.daribear.prefy.customClasses.Posts;

import android.os.Parcel;
import android.os.Parcelable;

import com.daribear.prefy.Utils.ParcelableHelper;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A post is a standard data entity that contains the details of the post.
 * Often extended, and an be written to a parcel.
 */
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


    /**
     * Parcelable Creator to enable passing Post objects between Android components.
     */
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

    /**
     * Checks equality between posts based on the unique postId.
     * @param o other object to compare
     * @return true if both posts have the same postId
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post that = (Post) o;
        return this.postId.equals(that.postId);
    }

    /**
     * Constructs a Post object from a Parcel, used in Parcelable.
     * @param in Parcel containing post data
     */
    public Post(Parcel in) {
        leftVotes = in.readInt();
        rightVotes = in.readInt();
        imageURL = in.readString();
        question = in.readString();
        commentsNumber = in.readInt();
        creationDate = in.readDouble();
        postId = in.readLong();
    }


    /**
     * Required method for Parcelable. Usually returns 0.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes the Post's data to a Parcel so it can be passed between Android components.
     * @param dest the Parcel to write data into
     * @param flags additional flags
     */
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

    /**
     * Returns a hash code based on postId.
     * Used to identify unique objects, for equals.
     */
    @Override
    public int hashCode() {
        return Objects.hash(postId);
    }




}
