package com.fametome.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fametome.R;
import com.fametome.listener.OnImageClickListener;
import com.fametome.util.FTDefaultBitmap;

public class ImageLayout extends LinearLayout{

    private Context context;

    OnImageClickListener imageClickListener = null;

    SquareImageView[] images = null;

    private int imageNumber;
    private int dividerSize;

    public ImageLayout(Context context) {
        super(context);
        init();
    }

    public ImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public ImageLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(attrs);
    }

    public void init(AttributeSet attrs){
        TypedArray attributes = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ImageLayout, 0, 0);

        try {
            imageNumber = attributes.getInteger(R.styleable.ImageLayout_imageNumber, 3);
            dividerSize = attributes.getInteger(R.styleable.ImageLayout_dividerWeight, 2);
        } finally {
            attributes.recycle();
        }
        init();
    }

    public void init(){

        context = getContext();

        int wrapContent = LayoutParams.WRAP_CONTENT;
        int fillParent = LayoutParams.MATCH_PARENT;
        float dp = getContext().getResources().getDisplayMetrics().density;

        int imageWeight = (100 / imageNumber) - (((imageNumber - 1) * dividerSize) / imageNumber);

        LayoutParams imageParams = null;
        LayoutParams dividerParams = null;

        if(getOrientation() == LinearLayout.HORIZONTAL){
            imageParams = new LayoutParams(0, wrapContent);
            dividerParams = new LayoutParams(0, wrapContent);
        }else{
            imageParams = new LayoutParams(wrapContent, 0);
            dividerParams = new LayoutParams(wrapContent, 0);
        }

        imageParams.weight = imageWeight;
        dividerParams.weight = dividerSize;

        setWeightSum(100);

        images = new SquareImageView[imageNumber];

        for(int i = 0; i < imageNumber; i++){
            images[i] = new SquareImageView(context);
            images[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
            images[i].setImageBitmap(FTDefaultBitmap.getInstance().getDefaultPicture());
            images[i].setOnClickListener(new LocalOnImageClickListener(i));

            addView(images[i], imageParams);
            if(i < imageNumber - 1){
                View divider = new View(context);
                divider.setAlpha(0.0f);
                addView(divider, dividerParams);
            }
        }
    }

    public int getImageNumber(){
        return imageNumber;
    }

    public void setImage(int index, Bitmap bitmap){
        if(index < imageNumber){
            images[index].setImageBitmap(bitmap);
        }
    }

    public ImageView getImageView(int index){
        return images[index];
    }

    public void setImageClickListener(OnImageClickListener imageClickListener){
        this.imageClickListener = imageClickListener;
    }

    private class LocalOnImageClickListener implements OnClickListener {

        int index;

        LocalOnImageClickListener(int index){
            this.index = index;
        }

        public void onClick(View v){
            if(imageClickListener != null) {
                imageClickListener.onImageClicked(index);
            }
        }
    }
}
