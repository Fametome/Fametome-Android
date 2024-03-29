package com.fametome.fragment.inbox;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fametome.Dialog.DialogManager;
import com.fametome.Dialog.FTProgressDialog;
import com.fametome.FTFragment;
import com.fametome.R;
import com.fametome.activity.member.MainActivity;
import com.fametome.adapter.InboxMessagesListAdapter;
import com.fametome.listener.UserListener;
import com.fametome.object.Initialisation;
import com.fametome.object.User;
import com.fametome.util.FTWifi;

public class InboxFragment extends FTFragment implements UserListener.onMessagesLoadedListener{

    private Context context;

    private InboxMessagesListAdapter listMessagesAdapter = null;

    private View emptyView = null;
    private View networklessView = null;
    private ListView listMessages = null;

    private FTProgressDialog refreshDialog = null;

    public InboxFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        User.getInstance().addMessagesLoadedListener(this);

        Log.d("InboxFragment", "onCreate - fragment is created");
        refreshDialog = new FTProgressDialog(((MainActivity)getActivity()).getContext());
        refreshDialog.setTitle(R.string.inbox_refresh_dialog_title);
        refreshDialog.setMessage(getString(R.string.inbox_refresh_dialog_message));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setNavigationDrawerEnabled(true);
        setHasOptionsMenu(true);

        context = getActivity().getApplicationContext();

        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);

        emptyView = rootView.findViewById(R.id.emptyView);
        networklessView = rootView.findViewById(R.id.networklessView);
        listMessages = (ListView)rootView.findViewById(R.id.messagesList);

        if(!Initialisation.getInstance().isInbox()){
            DialogManager.showInitialisationInboxDialog(((MainActivity) getActivity()).getContext());
            Initialisation.getInstance().setInbox(true);
        }

        listMessages.setEmptyView(emptyView);
        listMessagesAdapter = new InboxMessagesListAdapter(((MainActivity) getActivity()).getContext(), this);
        listMessages.setAdapter(listMessagesAdapter);

        networklessView.setOnClickListener(clickRefresh);
        emptyView.setOnClickListener(clickRefresh);

        if(FTWifi.isNetworkAvailable(getActivity().getApplicationContext())){
            networklessView.setVisibility(View.GONE);
        }else{
            networklessView.setVisibility(View.VISIBLE);
        }

        return rootView;
    }

    private View.OnClickListener clickRefresh = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            refreshMessages();
        }
    };

    private void refreshMessages(){
        refreshDialog.show();
        User.getInstance().refreshMessages();
        if(FTWifi.isNetworkAvailable(context)){
            networklessView.setVisibility(View.GONE);
        }else{
            networklessView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.inbox, menu);

        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setIcon(R.drawable.ic_inbox_shadow_new);
        actionBar.setTitle(R.string.inbox_title);
        actionBar.show();

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.inbox_refresh){
            refreshMessages();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMessagesLoaded() {
        Log.i("InboxFragment", "onMessagesLoaded - messages are loaded");
        if(listMessagesAdapter != null) {
            listMessagesAdapter.notifyDataSetChanged();
        }

        if(User.getInstance().isMessagesLoaded()) {
            refreshDialog.cancel();
        }

        if(FTWifi.isNetworkAvailable(context)){
            networklessView.setVisibility(View.GONE);
        }else{
            networklessView.setVisibility(View.VISIBLE);
        }
    }
}
