package com.fametome.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fametome.R;
import com.fametome.listener.UserListener;
import com.fametome.object.Flash;
import com.fametome.object.User;
import com.fametome.util.FTBitmap;
import com.fametome.util.FTDefaultBitmap;
import com.fametome.widget.ProfilView;


public class FriendsGridAdapter extends BaseAdapter{
    LayoutInflater inflater = null;

    public FriendsGridAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return User.getInstance().getFriendsNumber();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = inflater.inflate(R.layout.item_friend_grid, parent, false);

        ProfilView profil = (ProfilView)rootView.findViewById(R.id.profil);

        if(User.getInstance().getFriend(position).getAvatar() != null) {
            profil.setAvatar(User.getInstance().getFriend(position).getAvatar().getBitmap());
        }else{
            profil.setAvatar(FTDefaultBitmap.getInstance().getDefaultAvatar());
        }
        profil.setUsername(User.getInstance().getFriend(position).getUsername());

        return rootView;
    }


}
