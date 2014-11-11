package com.fametome.fragment.account;

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
import com.fametome.adapter.AccountFaceAlbumAdapter;
import com.fametome.listener.UserListener;
import com.fametome.object.User;

public class AccountFaceAlbumFragment extends FTFragment implements UserListener.onFacesLoadedListener {

    GridView faceGrid;
    private AccountFaceAlbumAdapter gridAdapter;

    public AccountFaceAlbumFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        User.getInstance().addFacesLoadedListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setNavigationDrawerEnabled(false);
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_account_face_album, container, false);

        faceGrid = (GridView)rootView.findViewById(R.id.faceGrid);

        gridAdapter = new AccountFaceAlbumAdapter(getActivity().getApplicationContext());

        faceGrid.setAdapter(gridAdapter);

        faceGrid.setOnItemClickListener(clickItemFaceAlbum);

        return rootView;
    }

    private AdapterView.OnItemClickListener clickItemFaceAlbum = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Bundle facesArgs = new Bundle();
            facesArgs.putInt("index", position);

            AccountFacePagerFragment facesFragment = new AccountFacePagerFragment();
            facesFragment.setArguments(facesArgs);

            ((MainActivity)getActivity()).showFragment(facesFragment);
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.account_face_album, menu);

        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setIcon(R.drawable.ic_account_shadow);
        actionBar.setTitle(R.string.account_face_album_title);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.account_face_album_add){

            final AccountAddFaceFragment addFaceFragment = new AccountAddFaceFragment();
            ((MainActivity)getActivity()).showFragment(addFaceFragment);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFacesLoaded() {
        gridAdapter.notifyDataSetChanged();
    }
}
