package com.fametome.object;

import com.fametome.util.ParseConsts;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class FriendRequest extends Friend{

    ParseObject relationObject;

    private String state;

    public FriendRequest(){

    }

    public FriendRequest(ParseObject relationObject){
        super(relationObject);
        this.relationObject = relationObject;

        if(relationObject.get(ParseConsts.RELATION_RECEIVER).equals(ParseUser.getCurrentUser().getObjectId())){
            state = ParseConsts.RELATION_SENDER;
        }else{
            state = ParseConsts.RELATION_RECEIVER;
        }

    }

    public void load(ParseObject relationObject){
        loadRelation(relationObject);
        super.doFriendQuery(null);
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
