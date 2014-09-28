package com.fametome.fragment.outbox;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;

import com.fametome.Dialog.FTDialog;
import com.fametome.FTFragment;
import com.fametome.R;
import com.fametome.activity.member.MainActivity;
import com.fametome.adapter.OutboxFlashsListAdapter;
import com.fametome.fragment.NavigationDrawerFragment;
import com.fametome.object.User;

import java.util.ArrayList;
import java.util.List;

public class OutboxFragment extends FTFragment {

    private OutboxFlashsListAdapter listFlashsAdapter = null;

    private ListView listFlashs = null;
    private Button addFlash = null;

    private byte type;
    public static final byte TYPE_PLURI_DESTINATAIRE = 0;
    public static final byte TYPE_MONO_DESTINATAIRE = 1;

    public OutboxFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ((MainActivity) getActivity()).setNavigationDrawerEnabled(true);
        ((MainActivity)getActivity()).setSelectedItem(NavigationDrawerFragment.FRAGMENT_OUTBOX);

        View rootView = inflater.inflate(R.layout.fragment_outbox, container, false);

        listFlashs = (ListView)rootView.findViewById(R.id.list_flashs);
        addFlash = (Button)rootView.findViewById(R.id.addFlash);
        addFlash.setOnClickListener(clickAddFlash);

        listFlashsAdapter = new OutboxFlashsListAdapter((MainActivity)getActivity());
        listFlashs.setAdapter(listFlashsAdapter);

        return rootView;
    }

    private View.OnClickListener clickAddFlash = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            listFlashsAdapter.addItem();
            if(listFlashsAdapter.getFlashsNumber() >= 7){
                addFlash.setVisibility(View.GONE);
            }
        }
    };

    public void setMessageType(byte type){
        this.type = type;
    }

    @Override
    public void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager)getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(listFlashs.getWindowToken(), 0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.outbox, menu);

        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setIcon(R.drawable.ic_outbox_shadow);

        if(type == TYPE_PLURI_DESTINATAIRE) {
            actionBar.setTitle(R.string.outbox_title);
        }else if(type == TYPE_MONO_DESTINATAIRE){
            actionBar.setTitle(getString(R.string.outbox_mono_destinataire_title, getFriend().getUsername()));
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        InputMethodManager imm = (InputMethodManager)getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(listFlashs.getWindowToken(), 0);

        if (item.getItemId() == R.id.outbox_send) {

            if(User.getInstance().getFriendsNumber() == 0) {
                FTDialog dialog = new FTDialog(((MainActivity) getActivity()).getContext());
                dialog.setTitle(getString(R.string.outbox_send_without_friends_title));
                dialog.setMessage(getString(R.string.outbox_send_without_friends_message));
                dialog.show();
            }else{
                if(listFlashsAdapter.getMessage() != null) {
                    if(type == TYPE_PLURI_DESTINATAIRE) {
                        final OutboxChooseRecipientsFragment chooseRecipientsFragment = new OutboxChooseRecipientsFragment();
                        chooseRecipientsFragment.setMessage(listFlashsAdapter.getMessage());
                        ((MainActivity) getActivity()).showFragment(chooseRecipientsFragment);

                    }else if(type == TYPE_MONO_DESTINATAIRE){
                        final List<String> friendId = new ArrayList<String>();
                        friendId.add(getFriend().getParseUser().getObjectId());
                        OutboxChooseRecipientsFragment.sendMessage(((MainActivity) getActivity()).getContext(), listFlashsAdapter.getMessage(), TYPE_MONO_DESTINATAIRE, friendId);

                    }

                }else{
                    FTDialog dialog = new FTDialog(((MainActivity) getActivity()).getContext());
                    dialog.setTitle(getString(R.string.outbox_send_with_empty_message_title));
                    dialog.setMessage(getString(R.string.outbox_send_with_empty_message_message));
                    dialog.show();
                }
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
