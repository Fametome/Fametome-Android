package com.fametome.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fametome.R;

public class FTProgressDialog extends FTDialog{

    private ProgressBar progressView = null;

    public FTProgressDialog(Context context) {
        builder = new AlertDialog.Builder(context);

        final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.dialog_progress, null);
        titleView = (TextView)rootView.findViewById(R.id.title);
        progressView = (ProgressBar)rootView.findViewById(R.id.progress);
        messageView = (TextView)rootView.findViewById(R.id.message);

        builder.setView(rootView);
    }

    @Override
    public void show() {
        if(titleView.getText().toString().isEmpty()){
            titleView.setVisibility(View.GONE);
        }else{
            titleView.setVisibility(View.VISIBLE);
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void setMessage(String message) {
        super.setMessage(message);
    }
}
