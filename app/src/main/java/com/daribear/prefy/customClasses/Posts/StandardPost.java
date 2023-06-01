package com.daribear.prefy.customClasses.Posts;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StandardPost extends Post{
    private Long userId;



    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeLong(userId);
    }

    protected StandardPost(Parcel in) {
        super(in);
        userId = in.readLong();
    }


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
