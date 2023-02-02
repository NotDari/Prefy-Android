package com.example.prefy.Popular;


import android.os.Parcel;
import android.os.Parcelable;

import com.example.prefy.Profile.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PopularPost implements Parcelable {
    private String postId;
    private Double creationDate;
    private String postKey;

    protected PopularPost(Parcel in) {
        postId = in.readString();
        creationDate = in.readDouble();
        postKey = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(postId);
        dest.writeDouble(creationDate);
        dest.writeString(postKey);


    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PopularPost> CREATOR = new Parcelable.Creator<PopularPost>() {
        @Override
        public PopularPost createFromParcel(Parcel in) {
            return new PopularPost(in);
        }

        @Override
        public PopularPost[] newArray(int size) {
            return new PopularPost[size];
        }
    };
}
