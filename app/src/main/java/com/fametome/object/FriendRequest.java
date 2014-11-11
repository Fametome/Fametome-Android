package com.fametome.object;

import android.content.Context;

import com.fametome.util.ParseConsts;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class FriendRequest extends ParseFriend{

    ParseObject relationObject;

    private String state;

    public FriendRequest(){

    }

    public FriendRequest(Context context, ParseObject relationObject){
        super(context, relationObject);
        this.relationObject = relationObject;

        if(relationObject.get(ParseConsts.RELATION_RECEIVER).equals(ParseUser.getCurrentUser().getObjectId())){
            state = ParseConsts.RELATION_SENDER;
        }else{
            state = ParseConsts.RELATION_RECEIVER;
        }

    }

    public void load(Context context, ParseObject relationObject){
        loadRelation(relationObject);
        super.doFriendQuery(context, null);
        this.relationObject = relationObject;

        if(relationObject.get(ParseConsts.RELATION_RECEIVER).equals(ParseUser.getCurrentUser().getObjectId())){
            state = ParseConsts.RELATION_SENDER;
        }else{
            state = ParseConsts.RELATION_RECEIVER;
        }

    }

    public ParseObject getRelationObject(){
        return relationObject;
    }

    public String getState(){
        return state;
    }
}
