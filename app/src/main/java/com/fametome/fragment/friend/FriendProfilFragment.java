package com.fametome.fragment.friend;

import android.app.ActionBar;
import android.media.Image;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fametome.FTFragment;
import com.fametome.R;
import com.fametome.activity.member.MainActivity;
import com.fametome.fragment.outbox.OutboxFragment;
import com.fametome.listener.FaceListener;
import com.fametome.listener.OnImageClickListener;
import com.fametome.listener.UserListener;
import com.fametome.object.Face;
import com.fametome.object.Friend;
import com.fametome.object.User;
import com.fametome.util.FTDefaultBitmap;
import com.fametome.util.ParseConsts;
import com.fametome.widget.ImageLayout;
import com.fametome.widget.ProfilView;
import com.fametome.widget.StatsView;

public class FriendProfilFragment extends FTFragment implements UserListener.onFacesLoadedListener{

    private Friend friend;

    private ProfilView profil;
    private ImageButton sendMessage;
    private ImageLayout facesLayout = null;
    private Button allFace = null;
    private TextView emptyFace = null;
    private StatsView stats = null;

    public FriendProfilFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setNavigationDrawerEnabled(false);
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_friend_profil, container, false);

        profil = (ProfilView)rootView.findViewById(R.id.profil);
        sendMessage = (ImageButton)rootView.findViewById(R.id.sendMessage);
        facesLayout = (ImageLayout)rootView.findViewById(R.id.faces);
        allFace = (Button)rootView.findViewById(R.id.allFace);
        emptyFace = (TextView)rootView.findViewById(R.id.emptyFace);
        stats = (StatsView)rootView.findViewById(R.id.stats);

        if(getArguments() != null){
            friend = User.getInstance().getFriend(getArguments().getInt("friendIndex"));
            if(friend.getAvatar()!= null){
                profil.setAvatar(friend.getAvatar().getBitmap());
            }else{
                profil.setAvatar(FTDefaultBitmap.getInstance().getDefaultAvatar());
            }
            profil.setUsername(friend.getUsername());
            stats.setFriendNumber(friend.getFriendsNumber());
            stats.setMessageSendNumber(friend.getParseUser().getInt(ParseConsts.USER_STATS_MESSAGE_SEND_NUMBER));
        }

        if(friend.isFacesLoaded()){
            displayFaces();
        }else{
            friend.loadFaces(this);
        }

        sendMessage.setOnClickListener(clickSendMessage);
        allFace.setOnClickListener(clickAllFace);

        facesLayout.setImageClickListener(clickImageFace);

        return rootView;
    }

    private View.OnClickListener clickSendMessage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            OutboxFragment outboxFragment = new OutboxFragment();
            outboxFragment.setMessageType(OutboxFragment.TYPE_MONO_DESTINATAIRE);
            outboxFragment.setFriend(friend);
            ((MainActivity)getActivity()).showFragment(outboxFragment);
        }
    };

    private OnImageClickListener clickImageFace = new OnImageClickListener() {
        @Override
        public void onImageClicked(int index) {

            if(index < friend.getFacesNumber()){

                Bundle facePagerBundle = new Bundle();
                facePagerBundle.putInt("friendIndex", getArguments().getInt("friendIndex"));
                facePagerBundle.putInt("faceIndex", index);

                final FriendFacePagerFragment facePagerFragment = new FriendFacePagerFragment();
                facePagerFragment.setArguments(facePagerBundle);
                ((MainActivity)getActivity()).showFragment(facePagerFragment);
            }
        }
    };

    private View.OnClickListener clickAllFace = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Bundle friendFaceAlbumBundle = new Bundle();
            friendFaceAlbumBundle.putInt("friendIndex", getArguments().getInt("friendIndex"));

            FriendFaceAlbumFragment friendFaceAlbumFragment = new FriendFaceAlbumFragment();
            friendFaceAlbumFragment.setArguments(friendFaceAlbumBundle);
            ((MainActivity)getActivity()).showFragment(friendFaceAlbumFragment);
        }
    };

    private void displayFaces() {
        if(friend.getFacesNumber() != 0) {
            facesLayout.getImageView(0).setVisibility(View.VISIBLE);
            facesLayout.getImageView(2).setVisibility(View.VISIBLE);
            emptyFace.setVisibility(View.GONE);
            allFace.setVisibility(View.VISIBLE);
            for (int i = 0; i < Math.min(facesLayout.getImageNumber(), friend.getFacesNumber()); i++) {
                facesLayout.setImage(i, friend.getFace(i).getPicture().getBitmap());
            }
        }else{
            facesLayout.getImageView(0).setVisibility(View.INVISIBLE);
            facesLayout.getImageView(2).setVisibility(View.INVISIBLE);
            emptyFace.setVisibility(View.VISIBLE);
            allFace.setVisibility(View.GONE);
        }
        stats.setFaceNumber(friend.getFacesNumber());
    }

    @Override
    public void onFacesLoaded() {
        displayFaces();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        if(friend == null){
            friend = User.getInstance().getFriend(getArguments().getInt("friendIndex"));
        }

        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setIcon(R.drawable.ic_friend_shadow);
        actionBar.setTitle(getString(R.string.friends_profil_title, friend.getUsername()));

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            ((MainActivity)getActivity()).showPreviousFragment();
        }

        return super.onOptionsItemSelected(item);
    }
}
