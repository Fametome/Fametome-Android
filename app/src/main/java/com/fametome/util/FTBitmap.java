package com.fametome.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class FTBitmap {

    public Bitmap bitmap;
    public byte[] datas;

    public FTBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        datas = stream.toByteArray();
    }

    public FTBitmap(byte[] datas) {
        this.datas = datas;
        bitmap = BitmapFactory.decodeByteArray(datas, 0, datas.length);
    }

    public FTBitmap(Bitmap bitmap, byte[] datas) {
        this.bitmap = bitmap;
        this.datas = datas;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public byte[] getDatas() {
        return datas;
    }

    public void setDatas(byte[] datas) {
        this.datas = datas;
    }
}
