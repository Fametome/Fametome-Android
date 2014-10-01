package com.fametome.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

public class DisabledListArrayAdapter<T> extends ArrayAdapter<T> {

    public DisabledListArrayAdapter(Context context, int resource, T[] objects) {
        super(context, resource, objects);
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
