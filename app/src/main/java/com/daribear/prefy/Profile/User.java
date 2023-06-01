package com.daribear.prefy.Profile;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;


import com.daribear.prefy.Utils.ParcelableHelper;

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

    private Long followerNumber;

    private Long followingNumber;
    private String bio;
    private String vk;
    private String instagram;
    private String twitter;
    private Boolean verified;

    private Boolean following;




    protected User(Parcel in) {
        username = ParcelableHelper.readStringFromParcel(in);
        profileImageURL = ParcelableHelper.readStringFromParcel(in);
        id = ParcelableHelper.readLongFromParcel(in);
        fullname = ParcelableHelper.readStringFromParcel(in);
        postsNumber = ParcelableHelper.readLongFromParcel(in);
        votesNumber = ParcelableHelper.readLongFromParcel(in);
        prefsNumber = ParcelableHelper.readLongFromParcel(in);
        followerNumber = ParcelableHelper.readLongFromParcel(in);
        followingNumber = ParcelableHelper.readLongFromParcel(in);
        bio = ParcelableHelper.readStringFromParcel(in);
        vk = ParcelableHelper.readStringFromParcel(in);
        instagram = ParcelableHelper.readStringFromParcel(in);
        twitter = ParcelableHelper.readStringFromParcel(in);
        verified = ParcelableHelper.readBooleanFromParcel(in);
        following = ParcelableHelper.readBooleanFromParcel(in);


    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelableHelper.writeStringToParcel(dest,username);
        ParcelableHelper.writeStringToParcel(dest,profileImageURL);
        ParcelableHelper.writeLongToParcel(dest, id);
        ParcelableHelper.writeStringToParcel(dest,fullname);
        ParcelableHelper.writeLongToParcel(dest, postsNumber);
        ParcelableHelper.writeLongToParcel(dest, votesNumber);
        ParcelableHelper.writeLongToParcel(dest, prefsNumber);
        ParcelableHelper.writeLongToParcel(dest,followerNumber);
        ParcelableHelper.writeLongToParcel(dest, followingNumber);
        ParcelableHelper.writeStringToParcel(dest,bio);
        ParcelableHelper.writeStringToParcel(dest,vk);
        ParcelableHelper.writeStringToParcel(dest,instagram);
        ParcelableHelper.writeStringToParcel(dest,twitter);
        ParcelableHelper.writeBooleanToParcel(dest, verified);
        ParcelableHelper.writeBooleanToParcel(dest, following);
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
