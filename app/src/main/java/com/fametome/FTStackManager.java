package com.fametome;

import android.util.Log;

import java.util.ArrayList;

public class FTStackManager {

    ArrayList<FTFragment> fragments;

    public FTStackManager(){
        fragments = new ArrayList<FTFragment>();
    }

    public void addFragment(FTFragment fragment){
        Log.d("FTStackManager", "addFragment - a fragment is added");
        fragments.add(fragment);
    }

    public void replaceLastFragment(FTFragment fragment){
        fragments.set((fragments.size() - 1), fragment);
    }

    public FTFragment getPreviousFragment(){
        Log.d("FTStackManager", "getAndRemoveLastFragment - a fragment is removing");
        fragments.remove(fragments.size() - 1);
        return fragments.get(fragments.size() - 1);
    }

    public void clearAllFragments(){
        Log.d("FTStackManager", "clearAllFragments - all fragments are removing");
        fragments.clear();
    }

    public boolean isPreviousFragment(){
        return fragments.size() > 1;
    }
}
