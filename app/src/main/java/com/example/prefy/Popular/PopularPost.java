package com.example.prefy.Popular;


import android.os.Parcel;
import android.os.Parcelable;

import com.example.prefy.Profile.User;
import com.example.prefy.customClasses.StandardPost;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PopularPost extends StandardPost{
    private Double popularDate;
}
