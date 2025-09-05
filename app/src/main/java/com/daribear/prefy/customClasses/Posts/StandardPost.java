package com.daribear.prefy.customClasses.Posts;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a standard post, which extends the base post but also contains the usr id.
 */
@Getter
@Setter
@NoArgsConstructor
public class StandardPost extends Post{
    private Long userId;



    public int describeContents() {
        return 0;
    }

    /**
     * Write the post to a parcel
     *
     * @param out parcel to be written to
     * @param flags flags to add to parcel
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeLong(userId);
    }

    /**
     * Receives a Standard post from the parcel
     * @param in parcel in
     */
    protected StandardPost(Parcel in) {
        super(in);
        userId = in.readLong();
    }


    /**
     * Parcelable.Creator implementation required for Android parcelable support.
     */
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
