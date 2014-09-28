package com.fametome.fragment.inbox;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fametome.FTFragment;
import com.fametome.R;
import com.fametome.activity.member.MainActivity;
import com.fametome.adapter.InboxShowFlashPagerAdapter;
import com.fametome.listener.AutoScrollListener;
import com.fametome.object.ParseMessage;
import com.fametome.object.User;
import com.fametome.widget.AutoScrollViewPager;

public class InboxShowFlashFragment extends FTFragment implements AutoScrollListener{

    private ParseMessage message;

    private AutoScrollViewPager pager;
    private InboxShowFlashPagerAdapter pagerAdapter;

    public InboxShowFlashFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setNavigationDrawerEnabled(false);
        setHasOptionsMenu(true);

        final View decorView = getActivity().getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LOW_PROFILE;
        decorView.setSystemUiVisibility(uiOptions);

        getActivity().getActionBar().hide();

        View rootView = inflater.inflate(R.layout.fragment_inbox_show_flash, container, false);

        pager = (AutoScrollViewPager)rootView.findViewById(R.id.flashPager);

        pager.setAutoScrollListener(this);

        if(getArguments() != null){
            message = User.getInstance().getMessage(getArguments().getInt("messageIndex"));
            pagerAdapter = new InboxShowFlashPagerAdapter(getActivity().getApplicationContext(), message);
        }else{
            Log.e("InboxShowFlashFragment", "onCreateView - getArguments is null !!!");
        }

        pager.setAdapter(pagerAdapter);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        Log.d("InboxShowFlashFragment", "onDestroyView - view is destroyed");
        super.onDestroyView();
        pager.cancelAutoScroll();
        getActivity().getWindow().getDecorView().setSystemUiVisibility(0);
        getActivity().getActionBar().show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setIcon(R.drawable.ic_outbox_shadow);
        actionBar.setTitle("Show flash fragment");
        actionBar.hide();

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onScrollFinished() {
        Log.d("InboxShowFlashFragment", "onScrollFinished - scroll finished");
        InboxResponseFragment responseFragment = new InboxResponseFragment();
        responseFragment.setParseMessage(message);
        ((MainActivity)getActivity()).replaceLastFragment(responseFragment);

        getActivity().getWindow().getDecorView().setSystemUiVisibility(0);
        getActivity().getActionBar().show();
    }
}
