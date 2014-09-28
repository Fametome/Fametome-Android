package com.fametome.object;

import android.util.Log;

import com.fametome.util.ParseConsts;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class ParseFace extends Face {

    public ParseFace(ParseObject flashObject){
        ParseQuery<ParseObject> flashsQuery = ParseQuery.getQuery(ParseConsts.FACE);
        flashsQuery.setLimit(1);
        flashsQuery.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
        flashsQuery.getInBackground(flashObject.getString(ParseConsts.FLASH_FACE_ID), new GetCallback<ParseObject>() {
            public void done(ParseObject faceObject, ParseException e) {
                if (e == null) {

                    loadFace(faceObject);

                } else {
                    Log.e("ParseFace", "ParseFace - error when getting face : " + e.getMessage());
                }
            }
        });
    }
}
