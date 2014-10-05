package com.fametome.adapter;

import android.app.Fragment;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fametome.R;
import com.fametome.fragment.inbox.InboxFragment;
import com.fametome.object.User;
import com.fametome.util.FTDefaultBitmap;
import com.fametome.widget.SquareImageView;

public class InboxMessagesListAdapter extends BaseAdapter {

    private Context context;

    private InboxFragment fragment;

    private LayoutInflater inflater;

    public InboxMessagesListAdapter(Context context, InboxFragment fragment){
        this.context = context;
        this.fragment = fragment;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return User.getInstance().getMessagesNumber();
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
        convertView = inflater.inflate(R.layout.item_inbox_messages_list, parent, false);

        ImageView avatarView = (ImageView)convertView.findViewById(R.id.avatar);
        TextView usernameView = (TextView)convertView.findViewById(R.id.username);

        if(User.getInstance().getMessages().size() != 0) {
            if (User.getInstance().getMessagesLoadedNumber() != 0) {
                if (User.getInstance().getMessage(position).getAuthor() != null) {
                    if (User.getInstance().getMessage(position).getAuthor().getAvatar() != null) {
                        avatarView.setImageBitmap(User.getInstance().getMessage(position).getAuthor().getAvatar().getBitmap());
                    } else {
                        avatarView.setImageBitmap(FTDefaultBitmap.getInstance().getDefaultAvatar());
                    }
                    usernameView.setText(context.getString(R.string.inbox_item_description, User.getInstance().getMessage(position).getAuthor().getUsername()));
                } else {
                    avatarView.setImageBitmap(FTDefaultBitmap.getInstance().getDefaultAvatar());
                    usernameView.setText(context.getString(R.string.inbox_item_description, User.getInstance().getMessage(position).getAuthor().getUsername()));
                }
            } else {
                Log.w("InboxMessagesListAdapter", "getView - there is not loaded messages");
            }
        }else{
            Log.w("InboxMessagesListAdapter", "getView - there is not messages");
        }

        return convertView;
    }
}
