package com.fametome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.fametome.R;
import com.fametome.object.Friend;
import com.fametome.object.User;
import com.fametome.widget.ProfilView;

public class FriendFaceAlbumAdapter extends BaseAdapter {

    LayoutInflater inflater = null;
    Friend friend;

    public FriendFaceAlbumAdapter(Context context, int friendIndex) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.friend = User.getInstance().getFriend(friendIndex);
    }

    @Override
    public int getCount() {
        return friend.getFacesNumber();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = inflater.inflate(R.layout.item_account_face_album, parent, false);

        ProfilView face = (ProfilView)rootView.findViewById(R.id.face);
        face.setAvatar(friend.getFace(position).getPicture().getBitmap());
        face.setUsername(friend.getFace(position).getText());

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
