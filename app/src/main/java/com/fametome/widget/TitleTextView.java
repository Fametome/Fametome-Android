package com.fametome.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;

public class TitleTextView extends TextView{
    public TitleTextView(Context context) {
        super(context);
        init();
    }

    public TitleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TitleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        String firstLetter = super.getText().subSequence(0, 1).toString();
        String otherLetter = super.getText().subSequence(1, super.getText().length()).toString();

        super.setText(Html.fromHtml("<font color=\"#FF9911\">" + firstLetter + "</font>" + otherLetter));
        super.setTypeface(null, Typeface.BOLD);
    }
}
