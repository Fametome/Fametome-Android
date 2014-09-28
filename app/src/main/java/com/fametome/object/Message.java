package com.fametome.object;

import java.util.ArrayList;
import java.util.List;

public class Message {

    public List<ParseFlash> flashs = null;

    public Message() {
        flashs = new ArrayList<ParseFlash>();
    }

    public Message(List<ParseFlash> flashs) {
        this.flashs = flashs;
    }

    public List<ParseFlash> getFlashs(){
        return flashs;
    }

    public void setFlashs(List<ParseFlash> flashs){
        this.flashs = flashs;
    }

    public void addFlash(ParseFlash flash){
        flashs.add(flash);
    }

    public Flash getFlash(int position){
        return flashs.get(position);
    }

    public Flash setFlash(int position, ParseFlash flash){
        return flashs.set(position, flash);
    }

    public void removeFlash(int position){
        flashs.remove(position);
    }

    public int getFlashNumber(){
        return flashs.size();
    }


}
