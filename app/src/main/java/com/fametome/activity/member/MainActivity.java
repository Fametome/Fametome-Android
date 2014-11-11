package com.fametome.activity.member;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import com.fametome.Dialog.CameraDialog;
import com.fametome.FTFragment;
import com.fametome.FTStackManager;
import com.fametome.fragment.account.AccountFragment;
import com.fametome.fragment.friend.FriendsFragment;
import com.fametome.fragment.inbox.InboxFragment;
import com.fametome.fragment.NavigationDrawerFragment;
import com.fametome.R;
import com.fametome.fragment.outbox.OutboxFragment;
import com.fametome.listener.CameraListener;
import com.fametome.listener.NavigationDrawerListener;
import com.fametome.object.Initialisation;
import com.fametome.object.User;
import com.fametome.util.FTDefaultBitmap;

public class MainActivity extends Activity implements NavigationDrawerListener {

    private NavigationDrawerFragment navigationDrawerFragment = null;

    private FTStackManager stackManager = null;

    private CameraDialog cameraDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FTDefaultBitmap.createInstance(this);
        User.createInstance(this);
        Initialisation.createInstance(this);

        setContentView(R.layout.activity_main);

        stackManager = new FTStackManager();

        navigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);

        cameraDialog = new CameraDialog(this);

        if(getIntent().getExtras() != null) {
            if (getIntent().getExtras().get("fragmentPosInMenu") == (Integer)NavigationDrawerFragment.FRAGMENT_ACCOUNT) {
                Log.d("MainActivity", "onCreate - getIntent().getExtras().getInt(\"fragmentPosInMenu\") == 0");
                navigationDrawerFragment.selectItem(NavigationDrawerFragment.FRAGMENT_ACCOUNT);
            } else {
                Log.d("MainActivity", "onCreate - getIntent().getExtras().getInt(\"fragmentPosInMenu\") != 0");
                navigationDrawerFragment.selectItem(NavigationDrawerFragment.FRAGMENT_INBOX);
            }
        }else{
            Log.d("MainActivity", "onCreate - intent is null");
            navigationDrawerFragment.selectItem(NavigationDrawerFragment.FRAGMENT_INBOX);
        }

        navigationDrawerFragment.setUp((DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        stackManager.clearAllFragments();

        switch (position) {
            case 0:
                final AccountFragment accountFragment = new AccountFragment();
                stackManager.addFragment(accountFragment);
                getFragmentManager().beginTransaction().replace(R.id.container, accountFragment).commit();
                break;

            case 1:
                final InboxFragment inboxFragment = new InboxFragment();
                stackManager.addFragment(inboxFragment);
                getFragmentManager().beginTransaction().replace(R.id.container, inboxFragment).commit();
                break;

            case 2:
                final OutboxFragment outboxFragment = new OutboxFragment();
                stackManager.addFragment(outboxFragment);
                outboxFragment.setMessageType(OutboxFragment.TYPE_PLURI_DESTINATAIRE);
                showFragment(outboxFragment);
                break;

            case 3:
                final FriendsFragment friendsFragment = new FriendsFragment();
                stackManager.addFragment(friendsFragment);
                getFragmentManager().beginTransaction().replace(R.id.container, friendsFragment).commit();
                break;
        }
    }

    public void showFragment(FTFragment fragment){
        stackManager.addFragment(fragment);
        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    public void showFragmentAtRoot(FTFragment fragment){
        stackManager.clearAllFragments();
        stackManager.addFragment(fragment);
        showFragment(fragment);
    }

    public void replaceLastFragment(FTFragment fragment){
        stackManager.replaceLastFragment(fragment);
        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    public void selectItem(int itemPos){
        navigationDrawerFragment.selectItem(itemPos);
    }

    public void setSelectedItem(int itemPos){
        navigationDrawerFragment.setSelectedItem(itemPos);
    }

    public void showPreviousFragment(){
        if(stackManager.isPreviousFragment()) {
            final FTFragment fragment = stackManager.getPreviousFragment();
            getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }else{
            navigationDrawerFragment.toggle();
        }
    }

    public void showPreviousFragment(FTFragment fragment){
        if(stackManager.isPreviousFragment()) {
            stackManager.removeLastFragment();
        }

        stackManager.replaceLastFragment(fragment);
        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    public void setNavigationDrawerEnabled(boolean enabled){
        if(enabled){
            navigationDrawerFragment.releaseDrawer();
        }else{
            navigationDrawerFragment.closeAndLockDrawer();
        }
    }

    public void showCamera(CameraListener cameraListener){
        cameraDialog.setCameraListener(cameraListener);
        cameraDialog.show();
    }

    public void leaveCamera(){
        cameraDialog.cancel();
    }

    public Context getContext(){
        return MainActivity.this;
    }

    @Override
    public void onBackPressed(){
        showPreviousFragment();
    }

    @Override
    public void onResume(){
        super.onResume();
        cameraDialog.reconnectCamera();
    }

    @Override
    public void onPause(){
        super.onPause();
        cameraDialog.unlockCamera();
        cameraDialog.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final ActionBar actionBar = getActionBar();
        actionBar.setIcon(R.drawable.ic_outbox_shadow);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            showPreviousFragment();
        }

        return super.onOptionsItemSelected(item);
    }

}