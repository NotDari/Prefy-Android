package com.example.prefy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.prefy.R
import com.example.prefy.Utils.SharedPrefs
/**
class Profile_fragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleSharedPrefs()
    }

    private fun handleSharedPrefs(){
        val sharedprefs = SharedPrefs(requireActivity())
        ProfilePageUsername.text = sharedprefs.getStringSharedPref(getString(R.string.save_username_pref))
        AccountNameText.text = sharedprefs.getStringSharedPref(getString(R.string.save_fullname_pref))
        val imageUrl:String = sharedprefs.getStringSharedPref(getString(R.string.save_profileP_pref)).toString()
        var profileImage = profileImageView
        if (imageUrl != "none"){
            Glide.with(this)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .into(profileImage)
        } else {
            profileImage.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.user_photo))
        }
        //Toast.makeText(requireActivity(), , 1000).show()

    }

}
        */