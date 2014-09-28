package com.fametome.listener;

import android.graphics.Bitmap;

import com.fametome.util.FTBitmap;

public abstract interface CameraListener {
    public abstract void onPictureTaken(FTBitmap picture);

}
