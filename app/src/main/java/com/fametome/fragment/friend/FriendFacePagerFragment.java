package com.fametome.fragment.friend;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.fametome.FTFragment;
import com.fametome.R;
import com.fametome.activity.member.MainActivity;
import com.fametome.adapter.FriendFacePagerAdapter;
import com.fametome.object.User;

public class FriendFacePagerFragment extends FTFragment {

    private ViewPager facePager = null;
    private FriendFacePagerAdapter facePagerAdapter = null;

    public FriendFacePagerFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ((MainActivity)getActivity()).setNavigationDrawerEnabled(false);

        View rootView = inflater.inflate(R.layout.fragment_account_face_pager, container, false);

        facePager = (ViewPager) rootView.findViewById(R.id.facePager);

        facePagerAdapter = new FriendFacePagerAdapter(getActivity().getApplicationContext(), getArguments().getInt("friendIndex"));

        facePager.setAdapter(facePagerAdapter);

        if(getArguments() != null){
            facePager.setCurrentItem(getArguments().getInt("faceIndex"));
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setIcon(R.drawable.ic_friend_shadow);
        actionBar.setTitle(getString(R.string.friends_face_pager_title, User.getInstance().getFriend(getArguments().getInt("friendIndex")).getUsername()));

        super.onCreateOptionsMenu(menu, inflater);
    }

}
