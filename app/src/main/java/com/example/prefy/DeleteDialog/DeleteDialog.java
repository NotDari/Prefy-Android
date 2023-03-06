package com.example.prefy.DeleteDialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class DeleteDialog {
    private static DeleteDialog instance;
    private Boolean dialogShowing;
    private Context context;
    private AlertDialog alertDialog;
    private DeleteDialogDelegate delegate;

    public DeleteDialog(Context context, DeleteDialogDelegate delegate, String type) {
        this.context = context;
        this.delegate = delegate;
        createDialog(type);
    }

    private void createDialog(String type){
        dialogShowing = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to delete this " + type + "?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        delegate.deleteClicked();
                        dialog.dismiss();
                        dialogShowing = false;
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialogShowing = false;
                dialog.dismiss();
            }
        });
        //Creating dialog box
        alertDialog = builder.create();
        //Setting the title manually
        alertDialog.setTitle("Delete " + type + "?");
    }
    public static DeleteDialog getInstance(Context context, DeleteDialogDelegate delegate, String type){
        if (instance == null){
            instance = new DeleteDialog(context, delegate, type);
        }
        return instance;
    }

    public void show(){
        if (!dialogShowing){
            dialogShowing = true;
            alertDialog.show();
        }
    }
}
