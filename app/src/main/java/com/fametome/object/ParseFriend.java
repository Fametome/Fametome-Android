package com.fametome.object;

import android.content.Context;

import com.fametome.listener.MessageListener;
import com.parse.ParseObject;

public class ParseFriend extends Friend {

    public ParseFriend(){
        super();
    }

    public ParseFriend(Context context, ParseObject friendObject){
        super(context, friendObject);
    }

    public ParseFriend(Context context, String id, MessageListener messageListener){
        super(context, id, messageListener);
    }

    public void setId(String id){
        super.id = id;
    }

    public String getId(){
        return id;
    }
}
