package com.fametome.object;

import android.graphics.BitmapFactory;
import android.util.Log;

import com.fametome.listener.FlashListener;
import com.fametome.util.FTBitmap;
import com.fametome.util.ParseConsts;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

public class ParseFlash extends Flash {

    private ParseObject flashObject;

    public ParseFlash(){

    }

    public ParseFlash(ParseObject flashObject, final FlashListener flashListener){
        this.flashObject = flashObject;

        Log.d("ParseFlash", "ParseFlash - face id : " + flashObject.getString(ParseConsts.FLASH_FACE_ID));
        Log.d("ParseFlash", "ParseFlash - text : " + flashObject.getString(ParseConsts.FLASH_TEXT));

        if(flashObject.getString(ParseConsts.FLASH_FACE_ID) != null){
            setFace(new ParseFace(flashObject, flashListener));

        }else if(flashObject.getString(ParseConsts.FLASH_TEXT) != null){
            setText(flashObject.getString(ParseConsts.FLASH_TEXT));
            flashListener.onFlashLoaded();

        }else if(flashObject.get(ParseConsts.FLASH_PICTURE) != null){

            ((ParseFile)flashObject.get(ParseConsts.FLASH_PICTURE)).getDataInBackground(new GetDataCallback() {
                public void done(byte[] faceData, ParseException e) {
                    if (e == null) {
                        setPicture(new FTBitmap(faceData));
                        flashListener.onFlashLoaded();
                    } else {
                        Log.e("ParseFlash", "ParseFlash - error when getting a flash picture file : " + e.getMessage());
                    }
                }
            });
        }
    }

    public ParseObject getFlashObject(){
        return flashObject;
    }
}
