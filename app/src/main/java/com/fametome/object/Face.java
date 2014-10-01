package com.fametome.object;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.fametome.listener.FaceListener;
import com.fametome.util.FTBitmap;
import com.fametome.util.ParseConsts;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

public class Face {


    protected FTBitmap picture;
    protected String text;

    public Face(){

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public FTBitmap getPicture() {
        return picture;
    }

    public void setPicture(FTBitmap picture) {
        this.picture = picture;
    }

}
