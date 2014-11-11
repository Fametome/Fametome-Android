package com.fametome.fragment.account;

import android.app.ActionBar;
import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.fametome.Dialog.DialogManager;
import com.fametome.Dialog.FTDialog;
import com.fametome.FTFragment;
import com.fametome.FTPush;
import com.fametome.R;
import com.fametome.activity.member.MainActivity;
import com.fametome.listener.CameraListener;
import com.fametome.object.Face;
import com.fametome.object.Initialisation;
import com.fametome.object.ParseFace;
import com.fametome.object.User;
import com.fametome.util.FTBitmap;
import com.fametome.util.FTDefaultBitmap;
import com.fametome.util.FTWifi;
import com.fametome.util.ParseConsts;
import com.fametome.widget.LoadingButton;
import com.fametome.widget.ProfilView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class AccountAddFaceFragment extends FTFragment implements CameraListener{

    ProfilView takePicture;

    EditText text = null;
    LoadingButton addFace = null;

    private FTBitmap facePicture;

    public AccountAddFaceFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setNavigationDrawerEnabled(false);
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_add_face, container, false);

        takePicture = (ProfilView)rootView.findViewById(R.id.take_picture);
        text = (EditText)rootView.findViewById(R.id.text);
        addFace = (LoadingButton)rootView.findViewById(R.id.addFace);

        if(!Initialisation.getInstance().isAddFace()){
            DialogManager.showInitialisationAddFaceDialog(((MainActivity) getActivity()).getContext());
            Initialisation.getInstance().setAddFace(true);
        }

        takePicture.setAvatar(FTDefaultBitmap.getInstance().getDefaultPicture());
        takePicture.setUsername(getActivity().getApplicationContext().getString(R.string.picture_take));

        takePicture.setOnClickListener(clickTakePicture);
        addFace.setOnClickListener(clickAddFace);

        return rootView;
    }

    private View.OnClickListener clickTakePicture = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((MainActivity)getActivity()).showCamera(AccountAddFaceFragment.this);
        }
    };

    private View.OnClickListener clickAddFace = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(FTWifi.isNetworkAvailable(getActivity().getApplicationContext())) {

                final String titlePush = getString(R.string.push_add_face_title, User.getInstance().getUsername());
                final String messagePush = getString(R.string.push_add_face_message, User.getInstance().getUsername());



                if (facePicture != null && text.getText().toString().length() > 0) {

                    final String faceText = text.getText().toString();

                    addFace.startLoading();
                    takePicture.setEnabled(false);
                    takePicture.setUsername(faceText);
                    text.setVisibility(View.GONE);

                    final ParseFile pictureFile = new ParseFile(User.getInstance().getUsername() + "_face", facePicture.getDatas());
                    pictureFile.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {


                            if (e == null) {
                                final ParseObject faceObject = new ParseObject(ParseConsts.FACE);
                                faceObject.put(ParseConsts.FACE_USER, ParseUser.getCurrentUser());
                                faceObject.put(ParseConsts.FACE_TEXT, text.getText().toString());
                                faceObject.put(ParseConsts.FACE_PICTURE, pictureFile);
                                faceObject.put(ParseConsts.FACE_PUBLIC, true);

                                faceObject.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {

                                            FTPush.sendPushToAllFriends(titlePush, messagePush);

                                            User.getInstance().addFace(new ParseFace(faceObject, facePicture, faceText));
                                            if(getActivity() != null) {
                                                ((MainActivity) getActivity()).showPreviousFragment();
                                            }

                                        } else {
                                            addFace.stopLoading();
                                            takePicture.setEnabled(true);
                                            takePicture.setUsername(getString(R.string.picture_take));
                                            text.setVisibility(View.VISIBLE);
                                            Log.e("AddFaceFragment", "clickAddFace - error when adding face : " + e.getMessage());
                                        }
                                    }
                                });
                            } else {
                                addFace.stopLoading();
                                takePicture.setEnabled(true);
                                takePicture.setUsername(getString(R.string.picture_take));
                                text.setVisibility(View.VISIBLE);
                                Log.e("AddFaceFragment", "clickAddFace - error when adding face image : " + e.getMessage());
                            }
                        }
                    });

                } else {
                    Log.w("AddFAceFragment", "clickAddFace - the picture is null or there is no text");
                }
            }else{
                FTDialog dialog = new FTDialog(((MainActivity)getActivity()).getContext());
                dialog.setTitle(getString(R.string.account_add_face_without_wifi_title));
                dialog.setMessage(getString(R.string.account_add_face_without_wifi_message));
                dialog.show();
            }
        }
    };

    @Override
    public void onPictureTaken(FTBitmap facePicture) {
        takePicture.setAvatar(facePicture.getBitmap());
        this.facePicture = facePicture;
        ((MainActivity)getActivity()).leaveCamera();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setIcon(R.drawable.ic_account_shadow);
        actionBar.setTitle(R.string.account_add_face_title);

        super.onCreateOptionsMenu(menu, inflater);
    }
}