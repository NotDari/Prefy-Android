package com.example.prefy.custom_classes

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UsersInfo(val fullname: String? = null ) {
}