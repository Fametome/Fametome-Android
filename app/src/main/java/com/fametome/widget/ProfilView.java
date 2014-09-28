package com.fametome.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fametome.R;
import com.fametome.util.FTDefaultBitmap;

public class ProfilView extends LinearLayout {

    private SquareImageView avatarView = null;
    private TextView usernameView = null;

    private String username;
    private Bitmap avatar;

    public ProfilView(Context context) {
        super(context);
        init();
    }

    public ProfilView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProfilView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.view_profil, this);

        avatarView = (SquareImageView)rootView.findViewById(R.id.avatar);
        usernameView = (TextView)rootView.findViewById(R.id.username);
    }

    public void setUsername(String username){
        this.username = username;
        usernameView.setText(username);
    }

    public void setAvatar(Bitmap avatar){
        this.avatar = avatar;
        if(avatar != null) {
            avatarView.setImageBitmap(avatar);
        }else{
            avatarView.setImageBitmap(FTDefaultBitmap.getInstance().getDefaultAvatar());
        }
    }

    public String getUsername(){
        return username;
    }

}
