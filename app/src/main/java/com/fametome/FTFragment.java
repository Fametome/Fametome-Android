package com.fametome;

import android.app.Fragment;

import com.fametome.object.Flash;
import com.fametome.object.Friend;
import com.fametome.object.Message;
import com.fametome.object.ParseMessage;

import java.util.List;

public class FTFragment extends Fragment {

    private ParseMessage parseMessage;

    private Message message;

    private Friend friend;

    public void setParseMessage(ParseMessage parseMessage){
        this.parseMessage = parseMessage;
    }

    public ParseMessage getParseMessage(){
        return parseMessage;
    }

    public void setMessage(Message message){
        this.message = message;
    }

    public Message getMessage(){
        return message;
    }

    public void setFriend(Friend friend){
        this.friend = friend;
    }

    public Friend getFriend(){
        return friend;
    }
}
