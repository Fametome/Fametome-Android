package com.fametome.fragment.friend;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fametome.Dialog.DialogManager;
import com.fametome.FTFragment;
import com.fametome.R;
import com.fametome.activity.member.MainActivity;
import com.fametome.adapter.FriendsGridAdapter;
import com.fametome.listener.UserListener;
import com.fametome.object.Initialisation;
import com.fametome.object.User;

import java.util.ArrayList;

public class FriendsFragment extends FTFragment implements UserListener.onFriendsLoadedListener{

    private GridView friendsGrid = null;
    private FriendsGridAdapter friendsGridAdapter = null;

    public FriendsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        User.getInstance().addFriendsLoadedListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setNavigationDrawerEnabled(true);
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        friendsGrid = (GridView)rootView.findViewById(R.id.friendsGrid);
        final LinearLayout emptyView = (LinearLayout)rootView.findViewById(R.id.emptyView);

        if(!Initialisation.getInstance().isFriends()){
            DialogManager.showInitialisationFriendsDialog(((MainActivity) getActivity()).getContext());
            Initialisation.getInstance().setFriends(true);
        }

        friendsGridAdapter = new FriendsGridAdapter(getActivity().getApplicationContext());
        friendsGrid.setAdapter(new FriendsGridAdapter(getActivity().getApplicationContext()));
        friendsGrid.setEmptyView(emptyView);

        friendsGrid.setOnItemClickListener(clickItemFriendsGrid);
        emptyView.setOnClickListener(clickSearch);

        return rootView;
    }

    private AdapterView.OnItemClickListener clickItemFriendsGrid = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Bundle args = new Bundle();
            args.putInt("friendIndex", position);

            final FriendProfilFragment friendProfilFragment = new FriendProfilFragment();
            friendProfilFragment.setArguments(args);

            ((MainActivity) getActivity()).showFragment(friendProfilFragment);
        }
    };

    private View.OnClickListener clickSearch = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final FriendSearchFragment friendSearchFragment = new FriendSearchFragment();
            ((MainActivity)getActivity()).showFragment(friendSearchFragment);
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        inflater.inflate(R.menu.friends, menu);

        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setIcon(R.drawable.ic_friend_shadow);
        actionBar.setTitle(R.string.friends_title);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.friends_search){
            final FriendSearchFragment friendSearchFragment = new FriendSearchFragment();
            ((MainActivity)getActivity()).showFragment(friendSearchFragment);
        }else if(item.getItemId() == R.id.friends_request){
            final FriendsRequestFragment friendsRequestFragment = new FriendsRequestFragment();
            ((MainActivity)getActivity()).showFragment(friendsRequestFragment);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFriendsLoaded() {
        Log.d("FriendsFragment", "onFriendsLoaded - friends are loaded");
        friendsGridAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFriendsRequestsLoaded() {}

}
