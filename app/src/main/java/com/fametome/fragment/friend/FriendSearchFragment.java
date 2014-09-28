package com.fametome.fragment.friend;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fametome.Dialog.FTDialog;
import com.fametome.FTFragment;
import com.fametome.FTPush;
import com.fametome.R;
import com.fametome.activity.member.MainActivity;
import com.fametome.object.Friend;
import com.fametome.object.FriendRequest;
import com.fametome.object.User;
import com.fametome.util.FTDefaultBitmap;
import com.fametome.util.FTWifi;
import com.fametome.util.ParseConsts;
import com.fametome.widget.LoadingButton;
import com.fametome.widget.SquareImageView;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class FriendSearchFragment extends FTFragment {

    private EditText searchText = null;
    private LoadingButton search = null;
    private LinearLayout userLayout = null;
    private SquareImageView avatar = null;
    private TextView username = null;
    private LoadingButton sendRequest = null;

    private ParseUser findedUser = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ((MainActivity)getActivity()).setNavigationDrawerEnabled(false);

        View rootView = inflater.inflate(R.layout.fragment_friend_search, container, false);

        searchText = (EditText)rootView.findViewById(R.id.text);
        search = (LoadingButton)rootView.findViewById(R.id.search);
        userLayout = (LinearLayout)rootView.findViewById(R.id.userLayout);
        avatar = (SquareImageView)rootView.findViewById(R.id.avatar);
        username = (TextView)rootView.findViewById(R.id.username);
        sendRequest = (LoadingButton)rootView.findViewById(R.id.sendRequest);

        search.setOnClickListener(clickSearch);
        sendRequest.setOnClickListener(clickSendRequest);

        return rootView;
    }

    private View.OnClickListener clickSearch = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager)getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);

            final String searchedUsername = searchText.getText().toString().trim();
            Log.d("FriendSearchFragment", "|" + searchedUsername + "|" + searchText.getText() + "|");

            if(searchedUsername.equals(User.getInstance().getUsername())){
                new AlertDialog.Builder(((MainActivity)getActivity()).getContext())
                        .setMessage(getString(R.string.friends_search_himself))
                        .show();
            }else{

                /* Le user a déja envoyé une relation à la personne recherchée ou vice-versa */
                for(Friend friendRequest : User.getInstance().getFriendsRequests()){
                    if(friendRequest.getUsername().equals(searchedUsername)){
                        Log.d("FriendSearchFragment", "there is already a relation in progress whith this friends");

                        userLayout.setVisibility(View.VISIBLE);

                        if(friendRequest.getAvatar() != null) {
                            avatar.setImageBitmap(friendRequest.getAvatar().getBitmap());
                        }else{
                            avatar.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.avatar_default));
                        }
                        username.setText(friendRequest.getUsername());

                        sendRequest.startLoading();
                        sendRequest.setText(getString(R.string.friends_search_request_in_progress));
                        return;
                    }
                }

                /* Le user et la personne recherchée sont déjà amis */
                for(Friend friend : User.getInstance().getFriends()){
                    if(friend.getUsername().equals(searchedUsername)){
                        Log.d("FriendSearchFragment", "the friend is already friends");

                        userLayout.setVisibility(View.VISIBLE);

                        if(friend.getAvatar() != null) {
                            avatar.setImageBitmap(friend.getAvatar().getBitmap());
                        }else{
                            avatar.setImageBitmap(FTDefaultBitmap.getInstance().getDefaultAvatar());
                        }
                        username.setText(friend.getUsername());

                        sendRequest.startLoading();
                        sendRequest.setText(getString(R.string.friends_search_already_friends));
                        return;
                    }
                }

                /* Sinon on fait une query pour récupérer la personne recherchée */
                if(FTWifi.isNetworkAvailable(getActivity().getApplicationContext())) {
                    search.startLoading();

                    ParseQuery<ParseUser> friendQuery = ParseUser.getQuery();
                    friendQuery.whereEqualTo(ParseConsts.USER_USERNAME, searchedUsername);
                    friendQuery.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
                    friendQuery.getFirstInBackground(new GetCallback<ParseUser>() {
                        @Override
                        public void done(final ParseUser findedUser, ParseException e) {
                            if(getActivity() != null) {
                                search.stopLoading();
                                sendRequest.setText(getString(R.string.friends_search_add));
                                sendRequest.stopLoading();

                                if (e == null) {

                                    FriendSearchFragment.this.findedUser = findedUser;

                                    userLayout.setVisibility(View.VISIBLE);
                                    username.setText(findedUser.getUsername());
                                    if (findedUser.get(ParseConsts.USER_AVATAR) != null) {
                                        ((ParseFile) findedUser.get(ParseConsts.USER_AVATAR)).getDataInBackground(new GetDataCallback() {
                                            public void done(byte[] avatarData, ParseException e) {
                                                if (e == null) {
                                                    if (avatar != null) {
                                                        Log.d("FriendSearchFragment", "clickSearch - " + searchedUsername + " has an avatar");
                                                        avatar.setImageBitmap(BitmapFactory.decodeByteArray(avatarData, 0, avatarData.length));
                                                    } else {
                                                        avatar.setImageBitmap(FTDefaultBitmap.getInstance().getDefaultAvatar());
                                                    }
                                                } else {
                                                    Log.e("FriendSearchFragment", "clickSearch - " + searchedUsername + " | error when getting avatar " + e.getMessage());
                                                    avatar.setImageBitmap(FTDefaultBitmap.getInstance().getDefaultAvatar());
                                                }
                                            }
                                        });
                                    } else {
                                        Log.e("FriendSearchFragment", "clickSearch - " + searchedUsername + " | findedUser.get(ParseConsts.USER_AVATAR) is null");
                                        avatar.setImageBitmap(FTDefaultBitmap.getInstance().getDefaultAvatar());
                                    }

                                } else {
                                    FTDialog dialog = new FTDialog(((MainActivity) getActivity()).getContext());
                                    dialog.setMessage(getString(R.string.friends_search_inexistant_user));
                                    dialog.show();
                                }
                            }
                        }
                    });
                }else{
                    FTDialog dialog = new FTDialog(((MainActivity)getActivity()).getContext());
                    dialog.setTitle(getString(R.string.friends_search_without_wifi_title));
                    dialog.setMessage(getString(R.string.friends_search_without_wifi_message));
                    dialog.show();
                }

            }
        }
    };

    private View.OnClickListener clickSendRequest = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sendRequest.startLoading();

            final ParseObject relation = ParseObject.create(ParseConsts.RELATION);
            relation.put(ParseConsts.RELATION_SENDER, ParseUser.getCurrentUser().getObjectId());
            relation.put(ParseConsts.RELATION_RECEIVER, findedUser.getObjectId());
            relation.put(ParseConsts.RELATION_STATUT, ParseConsts.RELATION_STATUT_REQUEST_IN_PROGRESS);
            relation.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    if(getActivity() != null) {

                        if (e == null) {

                            String titlePush = getString(R.string.push_send_request_title, User.getInstance().getUsername());
                            String messagePush = getString(R.string.push_send_request_message, User.getInstance().getUsername());
                            FTPush.sendPushToFriend(findedUser.getObjectId(), titlePush, messagePush);

                            sendRequest.setText(getString(R.string.friends_search_request_sended));
                            User.getInstance().addFriendRequest(new FriendRequest(relation));
                        } else {
                            FTDialog dialog = new FTDialog(((MainActivity) getActivity()).getContext());
                            dialog.setMessage(getString(R.string.friends_search_sending_error));
                            dialog.show();
                        }
                    }
                }
            });
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setIcon(R.drawable.ic_account_shadow);
        actionBar.setTitle(R.string.friends_search_title);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            FriendsFragment friendsFragment = new FriendsFragment();
            ((MainActivity) getActivity()).showPreviousFragment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
