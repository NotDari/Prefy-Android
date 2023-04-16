package com.daribear.prefy.SubmitPost;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.daribear.prefy.R;

public class GetImageChoiceDialog {
    private Dialog choiceDialog;
    private final Context context;

    public GetImageChoiceDialog(Context context) {
        this.context = context;
    }

    public void initDialog(){
        choiceDialog = new Dialog(context, android.R.style.Theme_DeviceDefault_NoActionBar);
        choiceDialog.setContentView(R.layout.post_choice_layout);
        choiceDialog.setCancelable(true);
        Window dialogWindow = choiceDialog.getWindow();
        dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogWindow.setGravity(Gravity.CENTER);
        initGallery(choiceDialog);
        choiceDialog.show();


    }

    private void initGallery(Dialog choiceDialog){
        ConstraintLayout gallerylay = choiceDialog.findViewById(R.id.GetImageChoiceDialogGalleryLayout);
        gallerylay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choiceDialog.dismiss();
            }
        });
    }
}
