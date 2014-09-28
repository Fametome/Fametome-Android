package com.fametome.util;

public class FTInteger{

    int value;

    public FTInteger(int value) {
        this.value = value;
    }

    public void increment(){
        value++;
    }

    public int getValue(){
        return value;
    }

    public boolean isEqualsTo(int secondValue){
        return value == secondValue;
    }
}
