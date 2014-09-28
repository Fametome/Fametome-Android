package com.fametome.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fametome.R;

public class StatsView extends LinearLayout {

    TextView friendNumberView;
    TextView messageSendNumberView;
    TextView faceNumberView;

    int friendNumber;
    int messageSendNumber;
    int faceNumber;

    public StatsView(Context context) {
        super(context);
        init();
    }

    public StatsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StatsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        Log.i("StatsView", "init - init");

        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.view_stats, this);

        friendNumberView = (TextView)rootView.findViewById(R.id.friendNumber);
        messageSendNumberView = (TextView)rootView.findViewById(R.id.messageSendNumber);
        faceNumberView = (TextView)rootView.findViewById(R.id.faceNumber);
    }

    public void setFriendNumber(int friendNumber) {
        this.friendNumber = friendNumber;
        friendNumberView.setText(String.valueOf(friendNumber));
    }

    public void setMessageSendNumber(int messageSendNumber) {
        this.messageSendNumber = messageSendNumber;
        messageSendNumberView.setText(String.valueOf(messageSendNumber));
    }

    public void setFaceNumber(int faceSendNumber) {
        this.faceNumber = faceSendNumber;
        faceNumberView.setText(String.valueOf(faceSendNumber));
    }

}
