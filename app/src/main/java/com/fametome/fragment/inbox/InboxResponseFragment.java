package com.fametome.fragment.inbox;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fametome.FTFragment;
import com.fametome.R;
import com.fametome.activity.member.MainActivity;
import com.fametome.fragment.NavigationDrawerFragment;
import com.fametome.fragment.outbox.OutboxFragment;
import com.fametome.object.Flash;
import com.fametome.object.ParseFlash;
import com.fametome.object.User;
import com.fametome.util.FTDefaultBitmap;
import com.fametome.util.ParseConsts;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class InboxResponseFragment extends FTFragment {

    private ImageView avatar;
    private TextView text;
    private Button answer;

    public InboxResponseFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_inbox_response, container, false);

        avatar = (ImageView)rootView.findViewById(R.id.avatar);
        text = (TextView)rootView.findViewById(R.id.text);
        answer = (Button)rootView.findViewById(R.id.answer);

        handleMessageDestruction();

        if(getParseMessage().getAuthor().getAvatar() != null) {
            avatar.setImageBitmap(getParseMessage().getAuthor().getAvatar().getBitmap());
        }else{
            avatar.setImageBitmap(FTDefaultBitmap.getInstance().getDefaultAvatar());
        }
        text.setText(getString(R.string.inbox_response_message, getParseMessage().getAuthor().getUsername()));

        answer.setOnClickListener(clickAnswer);

        User.getInstance().refreshMessages();

        return rootView;
    }

    private void handleMessageDestruction(){
        final ParseObject messageObject = getParseMessage().getMessageObject();

        List<String> recipientsIds = (List<String>)messageObject.get(ParseConsts.MESSAGE_RECIPIENT_ID_ARRAY);

        Log.d("InboxResponseFragment", "handleMessageDestruction - the first recipient id is " + recipientsIds.get(0));

        if(recipientsIds.size() > 1){
            recipientsIds.remove(ParseUser.getCurrentUser().getObjectId());
            messageObject.put(ParseConsts.MESSAGE_RECIPIENT_ID_ARRAY, recipientsIds);
            messageObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e != null){
                        Log.e("InboxResponseFragment", "handleMessageDestruction - error when removing the recipient from recipients array");
                    }
                }
            });
        }else{
            messageObject.deleteInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null) {

                        for(ParseFlash flash : getParseMessage().getFlashs()){
                            flash.getFlashObject().deleteInBackground();
                        }

                    }else{
                        Log.e("InboxResponseFragment", "handleMessageDestruction - error when removing the recipients array");
                    }
                }
            });

        }

        User.getInstance().refreshMessages();

    }

    private View.OnClickListener clickAnswer = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            OutboxFragment outboxFragment = new OutboxFragment();
            outboxFragment.setMessageType(OutboxFragment.TYPE_MONO_DESTINATAIRE);
            outboxFragment.setFriend(getParseMessage().getAuthor());
            ((MainActivity)getActivity()).showFragment(outboxFragment);
            ((MainActivity)getActivity()).setSelectedItem(NavigationDrawerFragment.FRAGMENT_OUTBOX);
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setIcon(R.drawable.ic_inbox_shadow_new);
        actionBar.setTitle(R.string.inbox_response_title);
        actionBar.show();

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            ((MainActivity)getActivity()).selectItem(NavigationDrawerFragment.FRAGMENT_INBOX);
        }

        return super.onOptionsItemSelected(item);
    }

}
