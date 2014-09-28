package com.fametome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.fametome.R;
import com.fametome.object.User;
import com.fametome.widget.ProfilView;

public class AccountFaceAlbumAdapter extends BaseAdapter {

    LayoutInflater inflater = null;

    public AccountFaceAlbumAdapter(Context context) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return User.getInstance().getFacesNumber();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = inflater.inflate(R.layout.item_account_face_album, parent, false);

        ProfilView face = (ProfilView)rootView.findViewById(R.id.face);
        face.setAvatar(User.getInstance().getFace(position).getPicture().getBitmap());
        face.setUsername(User.getInstance().getFace(position).getText());

        return rootView;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
