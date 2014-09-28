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

    private ParseObject faceObject = null;

    private String id;
    private FTBitmap picture;
    private String text;

    private FaceListener faceListener;

    public Face(){

    }

    public Face(ParseObject faceObject){
        this.faceObject = faceObject;
        loadFace(faceObject);
    }

    protected void loadFace(ParseObject faceObject){
        id = faceObject.getObjectId();
        text = faceObject.getString(ParseConsts.FACE_TEXT);
        ((ParseFile)faceObject.get(ParseConsts.FACE_PICTURE)).getDataInBackground(new GetDataCallback() {
            public void done(byte[] faceData, ParseException e) {
                if (e == null) {
                    picture = new FTBitmap(BitmapFactory.decodeByteArray(faceData, 0, faceData.length));
                    if(faceListener != null) {
                        faceListener.onFaceLoaded();
                    }
                } else {
                    Log.e("Face", "loadPictureFile - error when getting a face picture file : " + e.getMessage());
                }
            }
        });
    }

    public Face(ParseObject faceObject, FTBitmap picture){
        this.faceObject = faceObject;

        id = faceObject.getObjectId();
        text = faceObject.getString(ParseConsts.FACE_TEXT);
        this.picture = picture;

        if(faceListener != null){
            faceListener.onFaceLoaded();
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FTBitmap getPicture() {
        return picture;
    }

    public void setPicture(FTBitmap picture) {
        this.picture = picture;
    }

    public void setFaceListener(FaceListener faceListener){
        this.faceListener = faceListener;
    }

    public ParseObject getFaceObject(){
        return faceObject;
    }
}
