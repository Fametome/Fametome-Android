package com.fametome.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.fametome.R;

import org.w3c.dom.Attr;

public class SquareImageView extends ImageView {

    public SquareImageView(Context context) {
        super(context);
        init();
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec);

        setMeasuredDimension(width, width);
    }
}