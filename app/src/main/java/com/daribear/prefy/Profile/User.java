package com.daribear.prefy.Profile;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements Parcelable{
    private String username;
    private String profileImageURL;
    private Long id;
    private String fullname;
    private Long postsNumber;
    private Long votesNumber;
    private Long prefsNumber;
    private Long rating;
    private String bio;
    private String vk;
    private String instagram;
    private String twitter;
    private Boolean verified;



    protected User(Parcel in) {
        username = in.readString();
        profileImageURL = in.readString();
        id = in.readLong();
        fullname = in.readString();
        postsNumber = in.readLong();
        votesNumber = in.readLong();
        prefsNumber = in.readLong();
        rating = in.readLong();
        bio = in.readString();
        vk = in.readString();
        instagram = in.readString();
        twitter = in.readString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            verified = in.readBoolean();
        } else {
            verified = in.readByte() != 0;
        }


    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(profileImageURL);
        dest.writeLong(id);
        dest.writeString(fullname);
        dest.writeLong(postsNumber);
        dest.writeLong(votesNumber);
        dest.writeLong(prefsNumber);
        dest.writeValue(rating);
        dest.writeString(bio);
        dest.writeString(vk);
        dest.writeString(instagram);
        dest.writeString(twitter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dest.writeBoolean(verified);
        } else {
            dest.writeByte((byte) (verified ? 1 : 0));
        }


    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };


}
