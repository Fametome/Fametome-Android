package com.fametome.object;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.fametome.listener.FaceListener;
import com.fametome.listener.FlashListener;
import com.fametome.util.FTBitmap;
import com.fametome.util.ParseConsts;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class ParseFace extends Face {

    private String id;

    private ParseObject faceObject = null;

    private FaceListener faceListener = null;

    public ParseFace(){

    }

    public ParseFace(ParseObject faceObject){
        ParseQuery<ParseObject> faceQuery = ParseQuery.getQuery(ParseConsts.FACE);
        faceQuery.setLimit(1);
        faceQuery.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
        faceQuery.getInBackground(faceObject.getObjectId(), new GetCallback<ParseObject>() {
            public void done(ParseObject faceObject, ParseException e) {
                if (e == null) {

                    loadFace(faceObject, null);

                } else {
                    Log.e("ParseFace", "ParseFace - error when getting face : " + e.getMessage());
                }
            }
        });
    }

    public ParseFace(ParseObject flashObject, final FlashListener flashListener){
        ParseQuery<ParseObject> faceQuery = ParseQuery.getQuery(ParseConsts.FACE);
        faceQuery.setLimit(1);
        faceQuery.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
        faceQuery.getInBackground(flashObject.getString(ParseConsts.FLASH_FACE_ID), new GetCallback<ParseObject>() {
            public void done(ParseObject faceObject, ParseException e) {
                if (e == null) {

                    loadFace(faceObject, flashListener);


                } else {
                    Log.e("ParseFace", "ParseFace - error when getting faceObject : " + e.getMessage());
                }
            }
        });
    }

    public ParseFace(ParseObject faceObject, FTBitmap picture, String text){
        this.faceObject = faceObject;
        this.id = faceObject.getObjectId();
        this.picture = picture;
        this.text = text;
    }

    private void loadFace(ParseObject faceObject, final FlashListener flashListener){
        id = faceObject.getObjectId();
        text = faceObject.getString(ParseConsts.FACE_TEXT);
        ((ParseFile)faceObject.get(ParseConsts.FACE_PICTURE)).getDataInBackground(new GetDataCallback() {
            public void done(byte[] faceData, ParseException e) {
                if (e == null) {
                    picture = new FTBitmap(BitmapFactory.decodeByteArray(faceData, 0, faceData.length));
                    if(faceListener != null) {
                        faceListener.onFaceLoaded();
                    }
                    if(flashListener != null){
                        flashListener.onFlashLoaded();
                    }
                } else {
                    Log.e("Face", "loadPictureFile - error when getting a face picture file : " + e.getMessage());
                }
            }
        });
    }


    public void setFaceListener(FaceListener faceListener){
        this.faceListener = faceListener;
    }

    public ParseObject getFaceObject(){
        return faceObject;
    }

    public String getId() {
        return id;
    }
}
