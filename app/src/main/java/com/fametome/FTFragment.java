package com.fametome;

import android.app.Fragment;

import com.fametome.object.Flash;
import com.fametome.object.Friend;
import com.fametome.object.Message;
import com.fametome.object.ParseFriend;
import com.fametome.object.ParseMessage;

import java.util.List;

public class FTFragment extends Fragment {

    private Object object;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public ParseMessage getMessage(){
        if(object instanceof ParseMessage){
            return (ParseMessage)object;
        }
        return null;
    }

    public ParseFriend getFriend(){
        if(object instanceof ParseFriend){
            return (ParseFriend)object;
        }
        return null;
    }
}
