package com.fametome.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Button;

import com.fametome.R;

public class LoadingButton extends Button {

    private String text = null;
    private String loadingText = null;

    private int backgroundResourceId;
    private int loadingBackgroundResourceId;

    private boolean isLoading = false;

    public LoadingButton(Context context) {
        super(context);
        init();
    }

    public LoadingButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public LoadingButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        TypedArray attributes = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.LoadingButton, 0, 0);

        try {
            backgroundResourceId = attributes.getResourceId(R.styleable.LoadingButton_background, R.drawable.register_button_confirm);
            loadingBackgroundResourceId = attributes.getResourceId(R.styleable.LoadingButton_loadingBackground, R.drawable.register_button_confirm_loading);
        } finally {
            attributes.recycle();
        }
        init();
    }

    private void init(){
        text = super.getText().toString();
        loadingText = getContext().getString(R.string.loading);

        super.setBackgroundResource(backgroundResourceId);
    }

    public boolean isLoading(){
        return isLoading;
    }

    public void startLoading(){
        super.setEnabled(false);
        super.setText(loadingText);
        super.setBackgroundResource(loadingBackgroundResourceId);
        isLoading = true;
    }

    public void stopLoading(){
        super.setEnabled(true);
        super.setText(text);
        super.setBackgroundResource(backgroundResourceId);
        isLoading = false;
    }
}
