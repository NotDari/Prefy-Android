package com.daribear.prefy.Settings.DetailsEdit.DetailChoice;

import android.content.Context;
import android.view.View;

import com.daribear.prefy.R;
import com.daribear.prefy.Utils.Utils;

public class PersonalDetailsChoice {
    private String originalUsername , originalName, originalAbout;
    private View view;


    public PersonalDetailsChoice(View view) {
        this.view = view;
    }

    private void init(){
        getDetails();
    }


    private void getDetails(){
        Context appContext = view.getContext().getApplicationContext();
        Utils utils = new Utils(appContext);
        this.originalUsername = utils.loadString(appContext.getString(R.string.save_username_pref), "");
        this.originalName = utils.loadString(appContext.getString(R.string.save_fullname_pref), "");
        this.originalAbout = utils.loadString(appContext.getString(R.string.save_bio_pref), "");
    }

    private void buildViews(){

    }
}
