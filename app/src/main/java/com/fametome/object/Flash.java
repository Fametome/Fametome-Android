package com.fametome.object;

import android.graphics.Bitmap;
import android.util.Log;

import com.fametome.util.FTBitmap;

public class Flash {

    private Face face;
    private String text;
    private FTBitmap picture;

    private byte type;
    public static final byte NO_TYPE = -1;
    public static final byte TYPE_TEXT = 0;
    public static final byte TYPE_PICTURE = 1;
    public static final byte TYPE_FACE = 2;

    public Flash(){
        type = NO_TYPE;
    }

    public Flash(String text) {
        this.text = text;
        type = TYPE_TEXT;
    }

    public Flash(FTBitmap picture){
        this.picture = picture;
        type = TYPE_PICTURE;
    }

    public String getText() {
        if(type == TYPE_FACE){
            return face.getText();
        }
        return text;
    }

    public void setText(String text) {
        this.text = text;
        face = null;
        picture = null;

        if(text.trim().isEmpty()){
            Log.d("Flash", "setText - the setted text is empty, then the type is changing to NO_TYPE");
            type = NO_TYPE;
        }else {
            Log.d("Flash", "setText - the setted text is " + text + ", the type is TYPE_TEXT");
            type = TYPE_TEXT;
        }
    }

    public FTBitmap getPicture() {
        if(type == TYPE_FACE){
            return face.getPicture();
        }
        return picture;
    }

    public void setPicture(FTBitmap picture) {
        this.picture = picture;
        face = null;
        text = "";
        type = TYPE_PICTURE;
    }

    public Face getFace(){
        return face;
    }

    public void setFace(Face face){
        this.face = face;
        picture = null;
        text = "";
        type = TYPE_FACE;
    }

    public byte getType(){
        return type;
    }
}
