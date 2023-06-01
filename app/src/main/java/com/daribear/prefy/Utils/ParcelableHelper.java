package com.daribear.prefy.Utils;

import android.os.Build;
import android.os.Parcel;

public class ParcelableHelper {


    public static void writeLongToParcel(Parcel p, Long l) {
        p.writeByte((byte)(l != null ? 1 : 0));
        if (l != null) {
            p.writeLong(l);
        }
    }

    public static Long readLongFromParcel(Parcel p) {
        boolean isPresent = p.readByte() == 1;
        return isPresent ? p.readLong() : null;
    }

    public static void writeStringToParcel(Parcel p, String s) {
        p.writeByte((byte)(s != null ? 1 : 0));
        if (s != null) {
            p.writeString(s);
        }
    }

    public static String readStringFromParcel(Parcel p) {
        boolean isPresent = p.readByte() == 1;
        return isPresent ? p.readString() : null;
    }

    public static void writeBooleanToParcel(Parcel p, Boolean b) {
        p.writeByte((byte)(b != null ? 1 : 0));
        if (b != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                p.writeBoolean(b);
            } else {
                p.writeByte((byte) (b ? 1 : 0));
            }

        }
    }

    public static Boolean readBooleanFromParcel(Parcel p) {
        boolean isPresent = p.readByte() == 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return isPresent ? p.readBoolean() : null;
        } else {
            return isPresent ? (p.readByte() != 0) : null;
        }
    }
}
