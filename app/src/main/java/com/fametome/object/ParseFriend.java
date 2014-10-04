package com.fametome.object;

import com.fametome.listener.MessageListener;
import com.parse.ParseObject;

public class ParseFriend extends Friend {

    public ParseFriend(){
        super();
    }

    public ParseFriend(ParseObject friendObject){
        super(friendObject);
    }

    public ParseFriend(String id, MessageListener messageListener){
        super(id, messageListener);
    }

    public void setId(String id){
        super.id = id;
    }

    public String getId(){
        return id;
    }
}
