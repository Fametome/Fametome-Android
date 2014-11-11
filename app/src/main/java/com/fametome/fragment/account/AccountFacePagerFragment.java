package com.fametome.fragment.account;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.fametome.Dialog.FTDialog;
import com.fametome.FTFragment;
import com.fametome.R;
import com.fametome.activity.member.MainActivity;
import com.fametome.adapter.AccountFacePagerAdapter;
import com.fametome.object.User;
import com.fametome.util.FTWifi;
import com.parse.DeleteCallback;
import com.parse.ParseException;

public class AccountFacePagerFragment extends FTFragment {

    ViewPager facePager = null;
    AccountFacePagerAdapter facePagerAdapter = null;

    public AccountFacePagerFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ((MainActivity)getActivity()).setNavigationDrawerEnabled(false);

        View rootView = inflater.inflate(R.layout.fragment_account_face_pager, container, false);

        facePager = (ViewPager) rootView.findViewById(R.id.facePager);

        facePagerAdapter = new AccountFacePagerAdapter(getActivity().getApplicationContext());

        facePager.setAdapter(facePagerAdapter);

        if(getArguments() != null){
            facePager.setCurrentItem(getArguments().getInt("index"));
        }

        return rootView;
    }

    private DialogInterface.OnClickListener clickButtonDialog = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            dialog.cancel();

            if(which == DialogInterface.BUTTON_POSITIVE){
                User.getInstance().getFace(facePager.getCurrentItem()).getFaceObject().deleteInBackground(new DeleteCallback() {
                    final int index = facePager.getCurrentItem();

                    @Override
                    public void done(ParseException e) {
                        User.getInstance().removeFace(index);
                        if(User.getInstance().getFacesNumber() > 0) {
                            if(facePager.getCurrentItem() == index) {
                                facePagerAdapter = new AccountFacePagerAdapter(getActivity().getApplicationContext());
                                facePager.setAdapter(facePagerAdapter);
                                facePager.setCurrentItem(Math.min(index, User.getInstance().getFacesNumber()));
                            }
                            Log.i("AccountFacePagerFragment", "clickButtonDialog / wahou! it's working!");
                        }else{
                            ((MainActivity)getActivity()).showPreviousFragment();
                        }
                    }
                });
            }

        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        /* La corbeille ne s'affiche pas si l'utilisateur n'est pas connecté à internet */
        if(FTWifi.isNetworkAvailable(((MainActivity) getActivity()).getContext())) {
            inflater.inflate(R.menu.account_face_pager, menu);
        }

        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setIcon(R.drawable.ic_account_shadow);
        actionBar.setTitle(R.string.account_face_pager_title);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.account_face_pager_delete) {
            facePager.getCurrentItem();

            FTDialog dialog = new FTDialog(((MainActivity)getActivity()).getContext());
            dialog.setMessage(getString(R.string.account_face_pager_delete_message) + " \" " + User.getInstance().getFace(facePager.getCurrentItem()).getText() +" \"");
            dialog.setPositiveButton(R.string.confirm, clickButtonDialog);
            dialog.setNegativeButton(R.string.cancel, clickButtonDialog);
            dialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
