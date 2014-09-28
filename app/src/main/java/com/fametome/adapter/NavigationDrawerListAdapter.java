package com.fametome.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fametome.R;
import com.fametome.object.User;
import com.fametome.util.FTDefaultBitmap;
import com.fametome.widget.RoundedCornerImageView;

public class NavigationDrawerListAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;

    private Bitmap inboxIcon;
    private Bitmap outboxIcon;
    private Bitmap friendsIcon;

    int selectedItem;

    public NavigationDrawerListAdapter(Context context){
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inboxIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.dialog_mini);
        outboxIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.map_mini);
        friendsIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.book_mini);
    }

    @Override
    public int getCount() {
        return 4;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        TextView text = null;

        if(position == 0){
            convertView = inflater.inflate(R.layout.item_navigation_drawer_top, parent, false);

            RoundedCornerImageView avatar = (RoundedCornerImageView)convertView.findViewById(R.id.avatar);
            text = (TextView)convertView.findViewById(R.id.text);

            text.setText(User.getInstance().getUsername());

            if(User.getInstance().getAvatar() != null){
                avatar.setImageBitmap(User.getInstance().getAvatar());
            }else{
                avatar.setImageBitmap(FTDefaultBitmap.getInstance().getDefaultAvatar());
            }

        }else {
            convertView = inflater.inflate(R.layout.item_navigation_drawer, parent, false);

            ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
            text = (TextView)convertView.findViewById(R.id.text);

            if (position == 1) {
                text.setText(context.getString(R.string.inbox_title));
                icon.setImageBitmap(inboxIcon);
            } else if (position == 2) {
                text.setText(context.getString(R.string.outbox_title));
                icon.setImageBitmap(outboxIcon);
            } else {
                text.setText(context.getString(R.string.friends_title));
                icon.setImageBitmap(friendsIcon);
            }
        }

        if(selectedItem == position){
            text.setTypeface(null, Typeface.BOLD);
            text.setTextColor(Color.BLACK);
        }

        return convertView;
    }

    public void setSelectedItem(int position){
        selectedItem = position;
        notifyDataSetChanged();
    }
}
