package com.fametome.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fametome.FTPush;
import com.fametome.R;
import com.fametome.activity.member.MainActivity;
import com.fametome.fragment.friend.FriendsFragment;
import com.fametome.object.FriendRequest;
import com.fametome.object.User;
import com.fametome.util.ParseConsts;
import com.fametome.widget.LoadingButton;
import com.fametome.widget.SquareImageView;
import com.parse.DeleteCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class FriendsRequestListAdapter extends BaseAdapter {

    private MainActivity mainActivity;
    private LayoutInflater inflater = null;

    public FriendsRequestListAdapter(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        inflater = (LayoutInflater)mainActivity.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return User.getInstance().getFriendsRequestsNumber();
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

        View rootView;

        Log.d("FriendsRequestListAdapter", "getView - n°" + position);


        if(User.getInstance().getFriendRequest(position).getState() != ParseConsts.RELATION_RECEIVER) {

            rootView = inflater.inflate(R.layout.item_friends_request_received, parent, false);

            SquareImageView avatar = (SquareImageView) rootView.findViewById(R.id.avatar);
            TextView username = (TextView) rootView.findViewById(R.id.username);

            if (User.getInstance().getFriendRequest(position).getAvatar() != null) {
                avatar.setImageBitmap(User.getInstance().getFriendRequest(position).getAvatar().getBitmap());
            }
            username.setText(User.getInstance().getFriendRequest(position).getUsername());

            final LoadingButton accept = (LoadingButton) rootView.findViewById(R.id.accept);
            final LoadingButton decline = (LoadingButton) rootView.findViewById(R.id.decline);

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    decline.setVisibility(View.GONE);
                    accept.startLoading();
                    accept.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    clickAccept(position);
                }
            });

            decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    accept.setVisibility(View.GONE);
                    accept.setEnabled(false);
                    decline.startLoading();
                    decline.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    clickDecline(position);
                }
            });

        }else{
            rootView = inflater.inflate(R.layout.item_friends_request_sended, parent, false);

            TextView text = (TextView)rootView.findViewById(R.id.text);

            text.setText(Html.fromHtml(mainActivity.getResources().getString(R.string.friends_request_description_sended, User.getInstance().getFriendRequest(position).getUsername())));
        }


        return rootView;
    }

    private void clickAccept(final int index) {
        ParseObject friendRelationObject = User.getInstance().getFriendRequest(index).getRelationObject();
        friendRelationObject.increment(ParseConsts.RELATION_STATUT);
        friendRelationObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                notifyDataSetChanged();
                if(e == null){

                    String titlePush = mainActivity.getString(R.string.push_accept_request_title, User.getInstance().getUsername());
                    String messagePush = mainActivity.getString(R.string.push_accept_request_message, User.getInstance().getUsername());
                    FTPush.sendPushToFriend(User.getInstance().getFriendRequest(index).getParseUser().getObjectId(), titlePush, messagePush);

                    User.getInstance().addFriend(User.getInstance().getFriendRequest(index));
                    User.getInstance().removeFriendRequest(index);

                    if(User.getInstance().getFriendsRequestsNumber() == 0){
                        mainActivity.showPreviousFragment();
                    }

                }else{
                    Log.e("FriendsRequestListAdapter", "the accept behavior of friend request cannot be handled : " + e.getMessage());
                }
            }
        });
    }

    private void clickDecline(final int index) {
        ParseObject friendRelationObject = User.getInstance().getFriendRequest(index).getRelationObject();
        friendRelationObject.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                notifyDataSetChanged();
                if(e == null){

                    User.getInstance().removeFriendRequest(index);

                    if(User.getInstance().getFriendsRequestsNumber() == 0){
                        final FriendsFragment friendsFragment = new FriendsFragment();
                        mainActivity.showFragment(friendsFragment);
                    }


                }else{
                    Log.e("FriendsRequestListAdapter", "the delete behavior of friend request cannot be handled : " + e.getMessage());
                }
            }
        });
    }
}
