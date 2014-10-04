package com.fametome.fragment.account;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.fametome.Dialog.FTDialog;
import com.fametome.Dialog.FTProgressDialog;
import com.fametome.FTFragment;
import com.fametome.R;
import com.fametome.activity.member.MainActivity;
import com.fametome.fragment.NavigationDrawerFragment;
import com.fametome.fragment.SettingsFragment;
import com.fametome.fragment.friend.FriendProfilFragment;
import com.fametome.fragment.friend.FriendSearchFragment;
import com.fametome.fragment.friend.FriendsRequestFragment;
import com.fametome.listener.CameraListener;
import com.fametome.listener.OnImageClickListener;
import com.fametome.listener.UserListener;
import com.fametome.object.User;
import com.fametome.util.FTBitmap;
import com.fametome.util.FTDefaultBitmap;
import com.fametome.util.FTWifi;
import com.fametome.widget.ImageLayout;
import com.fametome.widget.ProfilView;
import com.fametome.widget.StatsView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class AccountFragment extends FTFragment implements UserListener.onUserLoadedListener, UserListener.onFacesLoadedListener, UserListener.onFriendsLoadedListener{

    ProfilView profil;
    ImageLayout facesLayout = null;
    ImageLayout friendsLayout = null;
    Button allFace = null;
    Button addFace = null;
    Button addFaceWhenEmpty = null;
    Button allFriend = null;
    Button addFriendWhenEmpty = null;
    StatsView stats = null;

    public AccountFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        User.getInstance().addUserLoadedListener(this);
        User.getInstance().addFacesLoadedListener(this);
        User.getInstance().addFriendsLoadedListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setNavigationDrawerEnabled(true);

        View rootView = inflater.inflate(R.layout.fragment_account, container, false);

        profil = (ProfilView)rootView.findViewById(R.id.profil);
        facesLayout = (ImageLayout)rootView.findViewById(R.id.faces);
        friendsLayout = (ImageLayout)rootView.findViewById(R.id.friends);
        allFace = (Button)rootView.findViewById(R.id.allFace);
        addFace = (Button)rootView.findViewById(R.id.addFace);
        addFaceWhenEmpty = (Button)rootView.findViewById(R.id.addFaceWhenEmpty);
        allFriend = (Button)rootView.findViewById(R.id.allFriend);
        addFriendWhenEmpty = (Button)rootView.findViewById(R.id.addFriendWhenEmpty);
        stats = (StatsView)rootView.findViewById(R.id.stats);

        if(User.getInstance().isUserLoaded()){
            Log.d("AccountFragment", "onCreate - user is loaded");

            displayUser();

            profil.setAvatar(User.getInstance().getAvatar());
            profil.setUsername(User.getInstance().getUsername());
            stats.setFriendNumber(User.getInstance().getFriendsNumber());
            stats.setMessageSendNumber(User.getInstance().getMessagesSendNumber());
            stats.setFaceNumber(User.getInstance().getFacesNumber());
        }else {
            Log.d("AccountFragment", "onCreate - user going to be loaded");

        }

        if(User.getInstance().isFacesLoaded()) {
            Log.d("AccountFragment", "onCreate - faces are loaded");
            displayFaces();
        }else{
            Log.d("AccountFragment", "onCreate - faces going to be loaded");
        }

        if(User.getInstance().isFacesLoaded()) {
            Log.d("AccountFragment", "onCreate - friends are loaded");
            displayFriends();
        }else{
            Log.d("AccountFragment", "onCreate - friends going to be loaded");
        }

        profil.setOnClickListener(clickProfil);
        facesLayout.setImageClickListener(faceClickListener);
        allFace.setOnClickListener(clickAllFace);
        addFace.setOnClickListener(clickAddFace);
        addFaceWhenEmpty.setOnClickListener(clickAddFace);
        friendsLayout.setImageClickListener(friendClickListener);
        allFriend.setOnClickListener(clickAllFriend);
        addFriendWhenEmpty.setOnClickListener(clickAddFriend);

        return rootView;
    }

    private View.OnClickListener clickProfil = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(FTWifi.isNetworkAvailable(getActivity().getApplicationContext())) {
                ((MainActivity) getActivity()).showCamera(pictureListenerAvatar);
            }else{
                FTDialog dialog = new FTDialog(((MainActivity)getActivity()).getContext());
                dialog.setTitle(R.string.account_change_avatar_without_wifi_title);
                dialog.setMessage(R.string.account_change_avatar_without_wifi_message);
                dialog.show();
            }
        }
    };

    private CameraListener pictureListenerAvatar = new CameraListener() {
        @Override
            public void onPictureTaken(final FTBitmap picture) {
            ((MainActivity)getActivity()).leaveCamera();

            final FTProgressDialog avatarLoadingDialog = new FTProgressDialog(((MainActivity)getActivity()).getContext());
            avatarLoadingDialog.setTitle(R.string.account_change_avatar_loading_title);
            avatarLoadingDialog.setMessage(getString(R.string.account_change_avatar_loading_message));
            avatarLoadingDialog.show();

            final ParseFile file = new ParseFile(User.getInstance().getUsername() + "_avatar.png", picture.getDatas());
            file.saveInBackground();

            ParseUser.getCurrentUser().put("avatar", file);

            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    avatarLoadingDialog.cancel();
                    if(e == null) {
                        User.getInstance().setAvatar(picture.getBitmap());
                    }else{
                        Log.i("AccountFragment", "ParseExeption when saving user for avatar : " + e.toString());
                    }
                }
            });
        }
    };

    private OnImageClickListener faceClickListener = new OnImageClickListener() {
        @Override
        public void onImageClicked(int index) {
            if(index < User.getInstance().getFacesNumber()) {

                final Bundle facesArgs = new Bundle();
                facesArgs.putInt("index", index);

                final AccountFacePagerFragment facesFragment = new AccountFacePagerFragment();
                facesFragment.setArguments(facesArgs);
                ((MainActivity) getActivity()).showFragment(facesFragment);

            }else{

                final AccountAddFaceFragment addFaceFragment = new AccountAddFaceFragment();
                ((MainActivity)getActivity()).showFragment(addFaceFragment);
            }
        }
    };

    private View.OnClickListener clickAllFace = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final AccountFaceAlbumFragment accountFaceAlbumFragment = new AccountFaceAlbumFragment();
            ((MainActivity)getActivity()).showFragment(accountFaceAlbumFragment);
        }
    };

    private View.OnClickListener clickAddFace = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final AccountAddFaceFragment addFaceFragment = new AccountAddFaceFragment();
            ((MainActivity)getActivity()).showFragment(addFaceFragment);
        }
    };

    private OnImageClickListener friendClickListener = new OnImageClickListener() {
        @Override
        public void onImageClicked(int index) {
            if(index < User.getInstance().getFriendsNumber()) {
                Bundle args = new Bundle();
                args.putInt("friendIndex", index);

                final FriendProfilFragment friendProfilFragment = new FriendProfilFragment();
                friendProfilFragment.setArguments(args);

                ((MainActivity) getActivity()).showFragment(friendProfilFragment);
            }else {
                if (User.getInstance().getFriendsRequestsNumber() == 0) {
                    final FriendSearchFragment friendSearchFragment = new FriendSearchFragment();
                    ((MainActivity) getActivity()).showFragment(friendSearchFragment);
                } else {
                    final FriendsRequestFragment friendsRequestFragment = new FriendsRequestFragment();
                    ((MainActivity) getActivity()).showFragment(friendsRequestFragment);
                }
            }
        }
    };

    private View.OnClickListener clickAllFriend = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            ((MainActivity)getActivity()).selectItem(NavigationDrawerFragment.FRAGMENT_FRIEND);
        }
    };


    private View.OnClickListener clickAddFriend = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            // TODO : Revoir le comportement pour affecter l'élément selected du navigation drawer;
            if(User.getInstance().getFriendsRequestsNumber() == 0) {
                final FriendSearchFragment friendSearchFragment = new FriendSearchFragment();
                ((MainActivity) getActivity()).showFragment(friendSearchFragment);
            }else{
                final FriendsRequestFragment friendsRequestFragment = new FriendsRequestFragment();
                ((MainActivity) getActivity()).showFragment(friendsRequestFragment);
            }
        }
    };

    private void displayUser(){
        profil.setAvatar(User.getInstance().getAvatar());
        profil.setUsername(User.getInstance().getUsername());

        stats.setFriendNumber(User.getInstance().getFriendsNumber());
        stats.setFaceNumber(User.getInstance().getFacesNumber());
    }

    private void displayFaces(){
        if(User.getInstance().getFacesNumber() == 0) {
            Log.d("AccountFragment", "setEmptyFaceLayout - there is no face, an appropriate layout will be setted");
            addFaceWhenEmpty.setVisibility(View.VISIBLE);
            facesLayout.getImageView(0).setVisibility(View.INVISIBLE);
            facesLayout.getImageView(2).setVisibility(View.INVISIBLE);
            addFace.setVisibility(View.GONE);
            allFace.setVisibility(View.GONE);

        }else {
            Log.d("AccountFragment", "setEmptyFaceLayout - there are faces, an appropriate layout will be setted");
            addFaceWhenEmpty.setVisibility(View.GONE);
            facesLayout.getImageView(0).setVisibility(View.VISIBLE);
            facesLayout.getImageView(2).setVisibility(View.VISIBLE);
            addFace.setVisibility(View.VISIBLE);
            allFace.setVisibility(View.VISIBLE);

            for (int i = 0; i < 3; i++) {
                if(i < User.getInstance().getFacesNumber()) {
                    Log.d("AccountFragment", "displayFaces - i : " + i + "  || face text : " + User.getInstance().getFace(i).getText());
                    if (User.getInstance().getFace(i).getPicture() != null) {
                        facesLayout.setImage(i, User.getInstance().getFace(i).getPicture().getBitmap());
                    }else{
                        Log.d("AccountFragment", "displayFaces - the face picture is null");
                        facesLayout.setImage(i, FTDefaultBitmap.getInstance().getDefaultPicture());
                    }
                }else{
                    facesLayout.setImage(i, FTDefaultBitmap.getInstance().getDefaultPicture());
                }
            }
        }
    }

    private void displayFriends(){
        if(User.getInstance().getFriendsNumber() == 0) {
            Log.d("AccountFragment", "displayFriends - there is no friends, an appropriate layout will be setted");
            addFriendWhenEmpty.setVisibility(View.VISIBLE);
            friendsLayout.getImageView(0).setVisibility(View.INVISIBLE);
            friendsLayout.getImageView(2).setVisibility(View.INVISIBLE);
            allFriend.setVisibility(View.GONE);

        }else {
            Log.d("AccountFragment", "displayFriends - there are friends, an appropriate layout will be setted");
            addFriendWhenEmpty.setVisibility(View.GONE);
            friendsLayout.getImageView(0).setVisibility(View.VISIBLE);
            friendsLayout.getImageView(2).setVisibility(View.VISIBLE);
            allFriend.setVisibility(View.VISIBLE);

            for (int i = 0; i < friendsLayout.getImageNumber(); i++) {
                if(i < User.getInstance().getFriendsNumber()) {
                    Log.d("AccountFragment", "displayFriends - i : " + i + "  || friend username : " + User.getInstance().getFriend(i).getUsername());
                    if (User.getInstance().getFriend(i).getAvatar() != null) {
                        friendsLayout.setImage(i, User.getInstance().getFriend(i).getAvatar().getBitmap());
                    }else {
                        friendsLayout.setImage(i, FTDefaultBitmap.getInstance().getDefaultAvatar());
                        Log.d("AccountFragment", "displayFriends - the face picture is null");
                    }
                }else{
                    friendsLayout.setImage(i, FTDefaultBitmap.getInstance().getDefaultPicture());
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.account, menu);

        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setIcon(R.drawable.ic_account_shadow);
        actionBar.setTitle(R.string.account_title);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.account_settings) {
            SettingsFragment settingsFragment = new SettingsFragment();
            ((MainActivity) getActivity()).showFragment(settingsFragment);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUserLoaded() {
        Log.d("AccountFragment", "onUserLoaded - his name is " + User.getInstance().getUsername());
        displayUser();
    }

    @Override
    public void onFacesLoaded() {
        Log.d("AccountFragment", "onFacesLoaded - there is " +  User.getInstance().getFacesNumber() + " faces");
        if(facesLayout != null) {
            displayFaces();
        }
    }

    @Override
    public void onFriendsLoaded() {
        Log.d("AccountFragment", "onFriendsLoaded - there is " +  User.getInstance().getFacesNumber() + " friends");
        if(friendsLayout != null) {
            displayFriends();
        }else{
            Log.d("AccountFragment", "onFriendsLoaded - friends are loaded but the friendsLayout is null");
        }
    }

    @Override
    public void onFriendsRequestsLoaded() {}
}