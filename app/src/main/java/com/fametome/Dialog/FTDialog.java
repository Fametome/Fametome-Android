package com.fametome.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class FTDialog{

    private AlertDialog dialog;
    private AlertDialog.Builder builder;

    public FTDialog(Context context) {
        builder = new AlertDialog.Builder(context);
    }

    public void setTitle(String title){
        builder.setTitle(title);
    }

    public void setTitle(int resId){
        builder.setTitle(resId);
    }

    public void setMessage(String message){
        builder.setMessage(message);
    }

    public void setMessage(int resId){
        builder.setMessage(resId);
    }

    public void setPositiveButton(int textResId, final DialogInterface.OnClickListener listener) {
        builder.setPositiveButton(textResId, listener);
    }

    public void setNegativeButton(int textResId, final DialogInterface.OnClickListener listener) {
        builder.setNegativeButton(textResId, listener);
    }

    public void show() {
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

}
