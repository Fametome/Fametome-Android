package com.fametome.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fametome.R;

public class FTDialog{

    protected AlertDialog dialog;
    protected AlertDialog.Builder builder;

    protected View rootView = null;
    protected TextView titleView = null;
    protected TextView messageView = null;

    public FTDialog(){

    }

    public FTDialog(Context context) {
        builder = new AlertDialog.Builder(context);
        final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.dialog, null);
        titleView = (TextView)rootView.findViewById(R.id.title);
        messageView = (TextView)rootView.findViewById(R.id.message);

        builder.setView(rootView);
    }

    public void setTitle(String title){
        titleView.setText(title);
        dialog = builder.create();
    }

    public void setTitle(int resId){
        titleView.setText(resId);
        dialog = builder.create();
    }

    public void setMessage(String message){
        messageView.setText(message);
        dialog = builder.create();
    }

    public void setMessage(int resId){
        messageView.setText(resId);
        dialog = builder.create();
    }

    public void setPositiveButton(int textResId, final DialogInterface.OnClickListener listener) {
        builder.setPositiveButton(textResId, listener);
        dialog = builder.create();
    }

    public void setNegativeButton(int textResId, final DialogInterface.OnClickListener listener) {
        builder.setNegativeButton(textResId, listener);
        dialog = builder.create();
    }

    public void show() {
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void cancel(){
        if(dialog != null) {
            dialog.cancel();
        }else{
            Log.d("FTDialog", "cancel - the dialog is null");
        }
    }

}
