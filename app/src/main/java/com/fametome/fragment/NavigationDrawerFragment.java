package com.fametome.fragment;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fametome.R;
import com.fametome.activity.member.MainActivity;
import com.fametome.adapter.NavigationDrawerListAdapter;
import com.fametome.listener.NavigationDrawerListener;
import com.fametome.listener.UserListener;
import com.fametome.object.User;

public class NavigationDrawerFragment extends Fragment implements UserListener.onUserLoadedListener {

    private NavigationDrawerListener navigationDrawerListener;

    private NavigationDrawerListAdapter adapter;

    private ActionBarDrawerToggle drawerToggle;

    public static final int FRAGMENT_ACCOUNT = 0;
    public static final int FRAGMENT_INBOX = 1;
    public static final int FRAGMENT_OUTBOX = 2;
    public static final int FRAGMENT_FRIEND = 3;

    private DrawerLayout drawerLayout;
    private ListView menuList;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        User.getInstance().addUserLoadedListener(this);
    }

    public NavigationDrawerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        menuList = (ListView)rootView.findViewById(R.id.menuList);

        menuList.setOnItemClickListener(clickItemMenuList);

        adapter = new NavigationDrawerListAdapter(((MainActivity)getActivity()).getContext());
        menuList.setAdapter(adapter);

        return rootView;
    }

    private AdapterView.OnItemClickListener clickItemMenuList = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    };

    public void releaseDrawer(){
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public void closeAndLockDrawer(){
        drawerToggle.setDrawerIndicatorEnabled(false);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }


    public void setUp(DrawerLayout drawerLayout) {
        this.drawerLayout = drawerLayout;

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, R.drawable.ic_drawer_new, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActivity().invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }
        };

        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                drawerToggle.syncState();
            }
        });

        drawerLayout.setDrawerListener(drawerToggle);
    }

    public void selectItem(int position) {

        adapter.setSelectedItem(position);

        if (drawerLayout != null) {
            drawerLayout.closeDrawers();
        }
        if (navigationDrawerListener != null) {
            navigationDrawerListener.onNavigationDrawerItemSelected(position);
        }
    }

    public void toggle(){
        if(drawerLayout.isDrawerOpen(Gravity.LEFT)){
            drawerLayout.closeDrawer(Gravity.START);
        }else {
            drawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    public void setSelectedItem(int itemPos){
        adapter.setSelectedItem(itemPos);
    }

    @Override
    public void onAttach(Activity activity) {
        Log.i("NavigationDrawerFragment", "onAttach - called");
        super.onAttach(activity);
        navigationDrawerListener = (NavigationDrawerListener) activity;
    }

    @Override
    public void onDetach() {
        Log.i("NavigationDrawerFragment", "onDetach - called");
        super.onDetach();
        navigationDrawerListener = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUserLoaded() {
        adapter.notifyDataSetChanged();
    }
}
