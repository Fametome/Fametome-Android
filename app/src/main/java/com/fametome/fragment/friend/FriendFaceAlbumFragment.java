package com.fametome.fragment.friend;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.fametome.FTFragment;
import com.fametome.R;
import com.fametome.activity.member.MainActivity;
import com.fametome.adapter.FriendFaceAlbumAdapter;
import com.fametome.object.User;

public class FriendFaceAlbumFragment extends FTFragment {

    GridView faceGrid;

    public FriendFaceAlbumFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setNavigationDrawerEnabled(false);
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_friend_face_album, container, false);

        faceGrid = (GridView)rootView.findViewById(R.id.faceGrid);

        faceGrid.setAdapter(new FriendFaceAlbumAdapter(getActivity().getApplicationContext(), getArguments().getInt("friendIndex")));

        faceGrid.setOnItemClickListener(clickItemFaceAlbum);

        return rootView;
    }

    private AdapterView.OnItemClickListener clickItemFaceAlbum = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Bundle facePagerBundle = new Bundle();
            facePagerBundle.putInt("friendIndex", getArguments().getInt("friendIndex"));
            facePagerBundle.putInt("faceIndex", position);

            final FriendFacePagerFragment facePagerFragment = new FriendFacePagerFragment();
            facePagerFragment.setArguments(facePagerBundle);
            ((MainActivity)getActivity()).showFragment(facePagerFragment);
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setIcon(R.drawable.ic_friend_shadow);
        actionBar.setTitle(getString(R.string.friends_face_album_title, User.getInstance().getFriend(getArguments().getInt("friendIndex")).getUsername()));
        super.onCreateOptionsMenu(menu, inflater);
    }
}
