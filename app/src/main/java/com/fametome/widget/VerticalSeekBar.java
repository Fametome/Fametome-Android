package com.fametome.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.fametome.R;
import com.fametome.listener.SeekbarListener;

public class VerticalSeekBar extends View {

    SeekbarListener listener;

    private Paint activateLinePaint;
    private Paint normalLinePaint;
    private Paint handlerPaint;

    private float handlerPos = 0;

    private static final int HANDLER_RADIUS = 15;

    private int max;
    private int progress;

    public VerticalSeekBar(Context context) {
        super(context);
        init();
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs){

        TypedArray attributes = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.VerticalSeekBar, 0, 0);

        try {
            max = attributes.getInteger(R.styleable.VerticalSeekBar_max, 100);
            progress = attributes.getInteger(R.styleable.VerticalSeekBar_progress, 0);
        } finally {
            attributes.recycle();
        }

        init();
    }

    private void init(){
        setRotation(180);

        normalLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        normalLinePaint.setStrokeWidth(2);
        normalLinePaint.setColor(Color.rgb(50, 50, 50));

        activateLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        activateLinePaint.setStrokeWidth(2);
        activateLinePaint.setColor(Color.rgb(200, 200, 0));

        handlerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        handlerPaint.setColor(Color.rgb(255, 125, 0));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(handlerPos < HANDLER_RADIUS){
            handlerPos = HANDLER_RADIUS;
        }
        if(handlerPos > getHeight() - HANDLER_RADIUS){
            handlerPos = getHeight() - HANDLER_RADIUS;
        }

        canvas.drawLine(canvas.getWidth() / 2, 0, canvas.getWidth() / 2, handlerPos, activateLinePaint);
        canvas.drawLine(canvas.getWidth() / 2, handlerPos, canvas.getWidth() / 2, canvas.getHeight(), normalLinePaint);
        canvas.drawCircle(canvas.getWidth() / 2, handlerPos, HANDLER_RADIUS, handlerPaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(HANDLER_RADIUS * 3, height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE){
            handlerPos = event.getY();
            invalidate();

            if(listener != null) {
                listener.onProgressChanged((int) Math.max(0, handlerPos / 4));
            }
        }

        return true;
    }

    public void setProgress(int progress){
        this.progress = progress;
        handlerPos = progress;
        invalidate();
    }

    public void setSeekbarListener(SeekbarListener listener){
        this.listener = listener;
    }

}
