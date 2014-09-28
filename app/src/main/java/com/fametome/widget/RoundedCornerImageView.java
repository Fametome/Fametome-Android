package com.fametome.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class RoundedCornerImageView extends View {

    private Bitmap imageBitmap = null;

    private Paint paint = null;

    public RoundedCornerImageView(Context context) {
        super(context);
        init();
    }

    public RoundedCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundedCornerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public void onDraw(Canvas canvas){
        canvas.drawBitmap(cutBitmap(canvas), null, new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), paint);
    }

    private Bitmap cutBitmap(Canvas canvas){
        Bitmap output = Bitmap.createBitmap(imageBitmap.getWidth(), imageBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas specialCanvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, imageBitmap.getWidth(),
                imageBitmap.getHeight());

        paint.setAntiAlias(true);
        specialCanvas.drawARGB(0, 0, 0, 0);
        specialCanvas.drawRoundRect(new RectF(rect), 25, 25, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        specialCanvas.drawBitmap(imageBitmap, null, rect, paint);
        return output;
    }

    public void setImageBitmap(Bitmap imageBitmap){
        this.imageBitmap = imageBitmap;
    }
}
