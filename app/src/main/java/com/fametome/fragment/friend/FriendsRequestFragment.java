package com.fametome.fragment.friend;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fametome.Dialog.DialogManager;
import com.fametome.Dialog.FTProgressDialog;
import com.fametome.FTFragment;
import com.fametome.R;
import com.fametome.activity.member.MainActivity;
import com.fametome.adapter.FriendsRequestListAdapter;
import com.fametome.listener.UserListener;
import com.fametome.object.User;
import com.fametome.util.FTWifi;

public class FriendsRequestFragment extends FTFragment implements UserListener.onFriendsLoadedListener{

    private ListView requestList = null;
    private FriendsRequestListAdapter requestListAdapter = null;
    private FTProgressDialog refreshDialog;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        User.getInstance().addFriendsLoadedListener(this);

        refreshDialog = new FTProgressDialog(((MainActivity)getActivity()).getContext());
        refreshDialog.setTitle(getString(R.string.friends_requests_refresh_dialog_title));
        refreshDialog.setMessage(getString(R.string.friends_requests_refresh_dialog_message));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setNavigationDrawerEnabled(false);

        View rootView = inflater.inflate(R.layout.fragment_friends_request, container, false);

        requestList = (ListView)rootView.findViewById(R.id.list_requests);
        final View emptyView = rootView.findViewById(R.id.emptyView);
        emptyView.setOnClickListener(clickEmptyView);

        requestListAdapter = new FriendsRequestListAdapter((MainActivity)getActivity());
        requestList.setAdapter(requestListAdapter);
        requestList.setEmptyView(emptyView);

        return rootView;
    }

    private View.OnClickListener clickEmptyView = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final FriendSearchFragment friendSearchFragment = new FriendSearchFragment();
            ((MainActivity)getActivity()).showFragment(friendSearchFragment);
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.friends_requests, menu);

        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setIcon(R.drawable.ic_account_shadow);
        actionBar.setTitle(R.string.friends_request_title);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.friends_requests_refresh){
            if(FTWifi.isNetworkAvailable(getActivity().getApplicationContext())) {
                refreshDialog.show();
                User.getInstance().refreshFriendsRequests();
            }else{
                DialogManager.showDialog(((MainActivity)getActivity()).getContext(), R.string.friends_request_refresh_without_wifi_title, R.string.friends_request_refresh_without_wifi_message);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFriendsLoaded() {}

    @Override
    public void onFriendsRequestsLoaded() {
        refreshDialog.cancel();
        requestListAdapter.notifyDataSetChanged();
    }
}