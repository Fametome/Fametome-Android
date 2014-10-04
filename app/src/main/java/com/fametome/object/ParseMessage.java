package com.fametome.object;

import android.util.Log;

import com.fametome.listener.FlashListener;
import com.fametome.listener.MessageListener;
import com.fametome.object.Friend;
import com.fametome.object.Message;
import com.fametome.util.ParseConsts;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ParseMessage extends Message {

    private MessageListener messageListener;

    private ParseObject messageObject;
    private ParseFriend author;

    private boolean isFlashsLoaded = false;
    private int flashsLoadedNumber = 0;
    private int flashsNumber = 0;

    public ParseMessage(){

    }

    public ParseMessage(ParseObject messageObject, MessageListener messageListener){
        loadMessage(messageObject, messageListener);
    }

    public void loadMessage(ParseObject messageObject, MessageListener messageListener){
        this.messageListener = messageListener;
        this.messageObject = messageObject;
        this.author = new ParseFriend(messageObject.getString(ParseConsts.MESSAGE_AUTHOR_ID), messageListener);
        loadFlashs();
    }

    private FlashListener flashListener = new FlashListener(){
        @Override
        public void onFlashLoaded() {
            flashsLoadedNumber++;
            if(flashsLoadedNumber == flashsNumber){
                isFlashsLoaded = true;
                messageListener.onFlashsLoaded();
            }
        }
    };

    public void loadFlashs(){
        ParseQuery<ParseObject> flashsQuery = ParseQuery.getQuery(ParseConsts.FLASH);
        flashsQuery.whereEqualTo(ParseConsts.FLASH_MESSAGE, messageObject);
        flashsQuery.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
        flashsQuery.orderByAscending(ParseConsts.FLASH_INDEX);
        flashsQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> flashsList, ParseException e) {

                if (e == null) {

                    flashsNumber = flashsList.size();
                    flashsLoadedNumber = 0;

                    for(int i = 0; i < flashsList.size(); i++) {
                        final ParseFlash flash = new ParseFlash(flashsList.get(i), flashListener);
                        addFlash(flash);
                    }

                } else {
                    Log.e("ParseMessage", "loadFlashs - error when getting flashs : " + e.getMessage());
                }
            }
        });
    }

    public ParseObject getMessageObject(){
        return messageObject;
    }

    public void setAuthor(ParseFriend author){
        this.author = author;
    }

    public ParseFriend getAuthor(){
        return author;
    }

    public boolean isFlashsLoaded(){
        return isFlashsLoaded;
    }
}
