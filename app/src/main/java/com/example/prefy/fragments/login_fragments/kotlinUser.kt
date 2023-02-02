package com.example.prefy.fragments.login_fragments

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class kotlinUser(val username: String? = null, val profileImageURL: String? = null ) {
}