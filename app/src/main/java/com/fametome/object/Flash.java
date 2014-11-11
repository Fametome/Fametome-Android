package com.fametome.object;

import android.graphics.Bitmap;
import android.util.Log;

import com.fametome.util.FTBitmap;
import com.fametome.util.FTDefaultBitmap;
import com.fametome.util.ParseConsts;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import org.json.JSONObject;

public class Flash {

    private ParseFace face;
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

    public Flash(ParseFace face){
        this.face = face;
        type = TYPE_FACE;
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

    public ParseFace getFace(){
        return face;
    }

    public void setFace(ParseFace face){
        this.face = face;

        picture = null;
        text = "";
        type = TYPE_FACE;
    }

    public byte getType(){
        return type;
    }

    public ParseObject toParseObject(){

        final ParseObject flashObject = new ParseObject(ParseConsts.FLASH);

        if (type == Flash.TYPE_FACE) {

            flashObject.put(ParseConsts.FLASH_FACE_ID, getFace().getId());
            flashObject.put(ParseConsts.FLASH_TEXT, JSONObject.NULL);
            flashObject.put(ParseConsts.FLASH_PICTURE, JSONObject.NULL);

        } else if (type == Flash.TYPE_TEXT) {

            flashObject.put(ParseConsts.FLASH_FACE_ID, JSONObject.NULL);
            flashObject.put(ParseConsts.FLASH_TEXT, getText());
            flashObject.put(ParseConsts.FLASH_PICTURE, JSONObject.NULL);

        } else if (type == Flash.TYPE_PICTURE) {

            final ParseFile pictureFile = new ParseFile("flash_picture", getPicture().getDatas());

            flashObject.put(ParseConsts.FLASH_FACE_ID, JSONObject.NULL);
            flashObject.put(ParseConsts.FLASH_TEXT, JSONObject.NULL);
            flashObject.put(ParseConsts.FLASH_PICTURE, pictureFile);

        }
        return flashObject;
    }
}
