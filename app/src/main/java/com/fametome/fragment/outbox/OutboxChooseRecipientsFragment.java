package com.fametome.fragment.outbox;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.fametome.Dialog.DialogManager;
import com.fametome.Dialog.FTDialog;
import com.fametome.Dialog.FTProgressDialog;
import com.fametome.FTFragment;
import com.fametome.FTPush;
import com.fametome.R;
import com.fametome.activity.member.MainActivity;
import com.fametome.adapter.FriendsGridAdapter;
import com.fametome.fragment.NavigationDrawerFragment;
import com.fametome.object.Flash;
import com.fametome.object.Message;
import com.fametome.object.ParseMessage;
import com.fametome.object.User;
import com.fametome.util.FTInteger;
import com.fametome.util.FTWifi;
import com.fametome.util.ParseConsts;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OutboxChooseRecipientsFragment extends FTFragment {

    private GridView friendsGrid;

    private ArrayList<Integer> friendsSelectedPos;

    public OutboxChooseRecipientsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setNavigationDrawerEnabled(false);
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_outbox_choose_recipient, container, false);
        friendsGrid = (GridView)rootView.findViewById(R.id.friendsGrid);


        friendsSelectedPos = new ArrayList<Integer>();
        friendsGrid.setAdapter(new FriendsGridAdapter(getActivity().getApplicationContext()));

        friendsGrid.setOnItemClickListener(clickItemFriendsGrid);

        return rootView;
    }

    private AdapterView.OnItemClickListener clickItemFriendsGrid = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            for(int i = 0; i < friendsSelectedPos.size(); i++){
                if(friendsSelectedPos.get(i) == position){
                    Log.d("OutboxChooseRecipientFragment", "clickItemFriendsGrid - " + User.getInstance().getFriend(position).getUsername() + " going to be deselected");
                    friendsSelectedPos.remove(i);
                    view.setBackgroundColor(getResources().getColor(R.color.invisible));
                    return;
                }
            }

            Log.d("OutboxChooseRecipientFragment", "clickItemFriendsGrid - " + User.getInstance().getFriend(position).getUsername() + " going to be selected");
            friendsSelectedPos.add(position);
            view.setBackgroundColor(getResources().getColor(R.color.theme_orange));
        }
    };

    public static void sendMessage(final Context context, final ParseMessage message, final byte messageType, final List<String> recipientsIds){
        if(FTWifi.isNetworkAvailable(context)) {

            ParseObject messageObject = new ParseObject(ParseConsts.MESSAGE);
            messageObject.put(ParseConsts.MESSAGE_AUTHOR_ID, message.getAuthor().getId());
            messageObject.addAllUnique(ParseConsts.MESSAGE_RECIPIENT_ID_ARRAY, recipientsIds);
            messageObject.put(ParseConsts.MESSAGE_ANIMATION, 0);
            messageObject.put(ParseConsts.MESSAGE_SEEN, false);
            messageObject.put(ParseConsts.MESSAGE_TIME, 0);

            final FTProgressDialog progressDialog = new FTProgressDialog(context);
            progressDialog.setTitle(R.string.outbox_send_message_progress_title);
            progressDialog.setMessage(R.string.outbox_send_message_progress_message);

            if(messageType != OutboxFragment.TYPE_DEMO_MESSAGE) {
                progressDialog.show();
            }

            final int flashsNumber = message.getFlashNumber();
            final FTInteger flashsSendedNumber = new FTInteger(0);
            for (int i = 0; i < flashsNumber; i++) {

                final Flash currentFlash = message.getFlash(i);

                if (currentFlash.getType() != Flash.NO_TYPE) {

                    final ParseObject flash = currentFlash.toParseObject();
                    flash.put(ParseConsts.FLASH_MESSAGE, messageObject);
                    flash.put(ParseConsts.FLASH_INDEX, i);

                    if (currentFlash.getType() == Flash.TYPE_FACE) {
                        Log.i("OutboxChooseRecipientsFragment", "onOptionsItemSelected - the flash n°" + i + " is a face ! : " + currentFlash.getFace().getText());

                        flash.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    ParseUser.getCurrentUser().increment(ParseConsts.USER_STATS_FLASH_SEND_NUMBER);

                                } else {
                                    Log.e("OutboxChooseRecipientsFragment", "onOptionsItemSelected - error when sending flash : " + e.getMessage());
                                }
                                flashsSendedNumber.increment();
                                if(flashsSendedNumber.isEqualsTo(flashsNumber)){
                                    progressDialog.cancel();
                                    messageSended(context, message.getAuthor().getUsername(), recipientsIds);
                                    return;
                                }
                            }
                        });

                        if(messageType != OutboxFragment.TYPE_DEMO_MESSAGE) {
                            ParseUser.getCurrentUser().increment(ParseConsts.USER_STATS_FACE_SEND_NUMBER);
                        }
                    } else if (currentFlash.getType() == Flash.TYPE_TEXT) {
                        Log.i("OutboxChooseRecipientsFragment", "onOptionsItemSelected - the flash n°" + i + " is a text ! : " + currentFlash.getText());

                        flash.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    if(messageType != OutboxFragment.TYPE_DEMO_MESSAGE) {
                                        ParseUser.getCurrentUser().increment(ParseConsts.USER_STATS_FLASH_SEND_NUMBER);
                                    }
                                } else {
                                    Log.e("OutboxChooseRecipientsFragment", "onOptionsItemSelected - error when sending flash : " + e.getMessage());
                                }
                                flashsSendedNumber.increment();
                                if(flashsSendedNumber.isEqualsTo(flashsNumber)){
                                    progressDialog.cancel();
                                    messageSended(context, message.getAuthor().getUsername(), recipientsIds);
                                }
                            }
                        });

                        if(messageType != OutboxFragment.TYPE_DEMO_MESSAGE) {
                            ParseUser.getCurrentUser().increment(ParseConsts.USER_STATS_TEXT_SEND_NUMBER);
                        }

                    } else if (currentFlash.getType() == Flash.TYPE_PICTURE) {
                        Log.i("OutboxChooseRecipientsFragment", "onOptionsItemSelected - the flash n°" + i + " is a picture !");

                        final ParseFile pictureFile = new ParseFile("flash_picture", currentFlash.getPicture().getDatas());

                        pictureFile.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    if (messageType != OutboxFragment.TYPE_DEMO_MESSAGE){
                                        ParseUser.getCurrentUser().increment(ParseConsts.USER_STATS_PICTURE_SEND_NUMBER);
                                    }
                                    flash.put(ParseConsts.FLASH_PICTURE, pictureFile);

                                    flash.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            Log.i("OutboxChooseRecipientsFragment", "onOptionsItemSelected - a picture is saving");
                                            if (e == null) {
                                                if(messageType != OutboxFragment.TYPE_DEMO_MESSAGE) {
                                                    ParseUser.getCurrentUser().increment(ParseConsts.USER_STATS_FLASH_SEND_NUMBER);
                                                }
                                            } else {
                                                Log.e("OutboxChooseRecipientsFragment", "onOptionsItemSelected - error when sending flash : " + e.getMessage());
                                            }
                                            flashsSendedNumber.increment();
                                            if(flashsSendedNumber.isEqualsTo(flashsNumber)){
                                                progressDialog.cancel();
                                                messageSended(context, message.getAuthor().getUsername(), recipientsIds);
                                            }
                                        }
                                    });
                                } else {
                                    Log.e("OutboxChooseRecipientsFragment", "onOptionsItemSelected - error when saving picture file : " + e.getMessage());
                                    flashsSendedNumber.increment();
                                    if(flashsSendedNumber.isEqualsTo(flashsNumber)){
                                        progressDialog.cancel();
                                        messageSended(context, message.getAuthor().getUsername(), recipientsIds);
                                    }
                                }
                            }
                        });

                    }

                } else {
                    Log.w("OutboxChooseRecipientsFragment", "onOptionsItemSelected - the flash n°" + i + " is empty");
                    flashsSendedNumber.increment();
                    if(flashsSendedNumber.isEqualsTo(flashsNumber)){
                        progressDialog.cancel();
                        messageSended(context, message.getAuthor().getUsername(), recipientsIds);
                    }
                }
            }

            if(messageType != OutboxFragment.TYPE_DEMO_MESSAGE) {
                ParseUser.getCurrentUser().increment(ParseConsts.USER_STATS_MESSAGE_SEND_NUMBER);
                ParseUser.getCurrentUser().saveInBackground();

                ((MainActivity) context).selectItem(NavigationDrawerFragment.FRAGMENT_INBOX);
            }

        }else{
            /* - Pas de wifi - */
            DialogManager.showDialog(context, R.string.outbox_send_without_network_title, R.string.outbox_send_without_network_message);

        }
    }

    private static void messageSended(Context context, String senderUsername, List<String> recipientsIds){
        Log.d("OutboxChooseRecipientFragment", "messageSended - sending the push");
        String titlePush = context.getString(R.string.push_send_message_title, senderUsername);
        String messagePush = context.getString(R.string.push_send_message_message, senderUsername);
        FTPush.sendPushToMultipleFriends(recipientsIds, titlePush, messagePush);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.outbox_choose_recipient, menu);

        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setIcon(R.drawable.ic_outbox_shadow);
        actionBar.setTitle(R.string.outbox_choose_recipients_title);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            final OutboxFragment outboxFragment = new OutboxFragment();
            outboxFragment.setObject(getMessage());
            ((MainActivity)getActivity()).showPreviousFragment(outboxFragment);

        }else if(item.getItemId() == R.id.outbox_choose_recipient_send){

            if(friendsSelectedPos.size() > 0) {

                List<String> friendsSelectedId = new ArrayList<String>();
                for(Integer posInUserFriendList : friendsSelectedPos){
                    friendsSelectedId.add(User.getInstance().getFriend(posInUserFriendList).getParseUser().getObjectId());
                }

                sendMessage(((MainActivity)getActivity()).getContext(), getMessage(), OutboxFragment.TYPE_PLURI_DESTINATAIRE, friendsSelectedId);

            }else{
                /* - Aucun ami sélectionné - */
                DialogManager.showDialog(((MainActivity)getActivity()).getContext(), R.string.outbox_send_choose_recipients_empty_title, R.string.outbox_send_choose_recipients_empty_message);
            }

        }

        return super.onOptionsItemSelected(item);
    }
}