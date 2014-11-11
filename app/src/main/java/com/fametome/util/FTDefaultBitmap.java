package com.fametome.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.fametome.R;

public class FTDefaultBitmap {

    private static FTDefaultBitmap instance;

    private static Bitmap picture_default;
    private static Bitmap take_picture_default;
    private static Bitmap avatar_default;

    public static void createInstance(Context context){
        instance = new FTDefaultBitmap();

        picture_default = BitmapFactory.decodeResource(context.getResources(), R.drawable.picture_medium);
        take_picture_default = BitmapFactory.decodeResource(context.getResources(), R.drawable.picture_mini);
        avatar_default = BitmapFactory.decodeResource(context.getResources(), R.drawable.avatar_default);
    }

    public static FTDefaultBitmap getInstance(){
        return instance;
    }

    public Bitmap getDefaultPicture(){
        return picture_default;
    }

    public Bitmap getDefaultTakePicture(){
        return take_picture_default;
    }

    public Bitmap getDefaultAvatar(){
        return avatar_default;
    }
}
