package com.daribear.prefy.Utils;

import android.os.Build;
import android.os.Parcel;

/**
 * Helper class used to write data to parcels.
 * Necessary as primitive types could be null
 */
public class ParcelableHelper {

    /**
     * Writes a long to the parcel, handling nulls.
     * @param p parcel to write to
     * @param l long to write to the parcel
     */
    public static void writeLongToParcel(Parcel p, Long l) {
        p.writeByte((byte)(l != null ? 1 : 0));
        if (l != null) {
            p.writeLong(l);
        }
    }

    /**
     * Reads a long from the parcel, checking if its null.
     * @param p parcel to read from
     * @return the Long (which could be null)
     */
    public static Long readLongFromParcel(Parcel p) {
        boolean isPresent = p.readByte() == 1;
        return isPresent ? p.readLong() : null;
    }

    /**
     * Writes a String to the parcel, handling nulls.
     * @param p parcel to write to
     * @param s the string to write
     */
    public static void writeStringToParcel(Parcel p, String s) {
        p.writeByte((byte)(s != null ? 1 : 0));
        if (s != null) {
            p.writeString(s);
        }
    }

    /**
     * Reads a string from the parcel, checking if its null.
     * @param p parcel to read from
     * @return the String (which could be null)
     */
    public static String readStringFromParcel(Parcel p) {
        boolean isPresent = p.readByte() == 1;
        return isPresent ? p.readString() : null;
    }

    /**
     * Writes a Boolean to the parcel, handling nulls.
     * @param p parcel to write to
     * @param b the boolean to write
     */
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

    /**
     * Reads a Boolean from the parcel, checking if its null.
     * @param p  parcel to read from
     * @return the Boolean (which could be null)
     */
    public static Boolean readBooleanFromParcel(Parcel p) {
        boolean isPresent = p.readByte() == 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return isPresent ? p.readBoolean() : null;
        } else {
            return isPresent ? (p.readByte() != 0) : null;
        }
    }

    /**
     * Writes a Double to the parcel, handling nulls.
     * @param p parcel to write to
     * @param d the double to write
     */
    public static void writeDoubleToParcel(Parcel p, Double d){
        p.writeByte((byte)(d != null ? 1 : 0));
        if (d != null) {
            p.writeDouble(d);
        }
    }

    /**
     * Reads a Double from the parcel, checking if its null.
     * @param p parcel to read from
     * @return the Double (which could be null)
     */
    public static Double readDoublefromParcel(Parcel p) {
        boolean isPresent = p.readByte() == 1;
        return isPresent ? p.readDouble() : null;
    }
}
