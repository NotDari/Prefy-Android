package com.daribear.prefy.DeleteDialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * The dialog shown when deleting a comment or a post.
 * Makes the user confirm they would like to delete the item.
 * Is a singleton to stop multiple dialogs showing at once, if the user presses it multiple times.
 */
public class DeleteDialog {
    private static DeleteDialog instance;
    private Boolean dialogShowing;
    private Context context;
    private AlertDialog alertDialog;
    private DeleteDialogDelegate delegate;

    //Constructor
    public DeleteDialog(Context context, DeleteDialogDelegate delegate, String type) {
        this.context = context;
        this.delegate = delegate;
        createDialog(type);
    }

    /**
     * Creates the confirm deletion dialog with a yes/no option and a cancel button
     * @param type type of item to be deleted, for use in the title
     */
    private void createDialog(String type){
        dialogShowing = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //Create the dialog with the yes/know options
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
        //Cancel the dialog
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
    //Gets an instance of this classes singleton
    public static DeleteDialog getInstance(Context context, DeleteDialogDelegate delegate, String type){
        if (instance == null){
            instance = new DeleteDialog(context, delegate, type);
        } else {
            instance.delegate = delegate;
            instance.createDialog(type);
        }
        return instance;
    }

    /**
     * Show the dialog.
     */
    public void show(){
        if (!dialogShowing){
            dialogShowing = true;
            alertDialog.show();
        }
    }
}
