package com.fametome.object;

import com.parse.ParseObject;

/**
 * Created by famille on 01/10/14.
 */
public class ParseFriend extends Friend {

    public ParseFriend(){
        super();
    }

    public ParseFriend(ParseObject friendObject){
        super(friendObject);
    }
}
