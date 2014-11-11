package com.fametome.object;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.fametome.listener.FaceListener;
import com.fametome.listener.FriendListener;
import com.fametome.listener.MessageListener;
import com.fametome.listener.UserListener;
import com.fametome.util.FTBitmap;
import com.fametome.util.FTWifi;
import com.fametome.util.ParseConsts;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class Friend {
    private FriendListener friendListener;

    private ParseUser friendUser;

    protected FTBitmap avatar;
    protected String username;
    protected String id;

    private List<Face> faces;
    private int facesNumber;
    private int friendsNumber;
    private int facesLoadedNumber;
    private boolean isFacesLoaded;

    public Friend(){

    }

    public Friend(Context context, ParseObject relationObject){
        loadRelation(relationObject);
        doFriendQuery(context, null);
    }

    /* WHEN ADD FRIEND FOR THE AUTHOR OF A MESSAGE IN INBOX */
    public Friend(Context context, String id, MessageListener messageListener){
        this.id = id;
        doFriendQuery(context, messageListener);
    }

    protected void loadRelation(ParseObject relationObject){

        Log.d("Friend", "Friend - user id : " + ParseUser.getCurrentUser().getObjectId());
        Log.d("Friend", "Friend - receiver id : " + relationObject.get(ParseConsts.RELATION_RECEIVER));
        Log.d("Friend", "Friend - sender id : " + relationObject.getString(ParseConsts.RELATION_SENDER));

        if(relationObject.get(ParseConsts.RELATION_RECEIVER).equals(ParseUser.getCurrentUser().getObjectId())){
            id = relationObject.getString(ParseConsts.RELATION_SENDER);
        }else{
            id = relationObject.getString(ParseConsts.RELATION_RECEIVER);
        }

        Log.d("Friend", "Friend - friend id : " + id);
        Log.d("Friend", "Friend ------------------------------- ");
    }

    protected void doFriendQuery(Context context, final MessageListener messageListener){
        Log.d("Friend", "doFriendQuery - start of the function");

        ParseQuery<ParseUser> friendQuery = ParseUser.getQuery();
        if(FTWifi.isNetworkAvailable(context)) {
            friendQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        }else{
            friendQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ONLY);
        }
        friendQuery.getInBackground(id, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser friendUser, ParseException e) {
                Friend.this.friendUser = friendUser;

                Log.d("Friend", "doFriendQuery - the friend is loaded");

                if (e == null) {

                    username = friendUser.getString(ParseConsts.USER_USERNAME);

                    Log.d("Friend", "doFriendQuery - the friend username is " + username);

                    if(messageListener != null){
                        Log.d("Friend", "doFriendQuery - loading " + username + " for message");
                    }

                    if (friendUser.get(ParseConsts.USER_AVATAR) != null) {
                        ((ParseFile) friendUser.get(ParseConsts.USER_AVATAR)).getDataInBackground(new GetDataCallback() {
                            public void done(byte[] avatarData, ParseException e) {
                                if (e == null) {
                                    avatar = new FTBitmap(avatarData);
                                } else {
                                    Log.e("Friend", "Friend - the friend hasn't an avatar " + e.getMessage());
                                }
                                if (friendListener != null) {
                                    friendListener.onFriendLoaded();
                                    Log.d("Friend", "doFriendQuery - the friend listener is called");
                                }else{
                                    Log.d("Friend", "doFriendQuery - there isn't listener");
                                }
                                if(messageListener != null){
                                    messageListener.onAuthorLoaded();
                                }
                            }
                        });
                    }else{
                        if (friendListener != null) {
                            friendListener.onFriendLoaded();
                            Log.d("Friend", "doFriendQuery - the friend listener is called");
                        }else{
                            Log.d("Friend", "doFriendQuery - there isn't listener");
                        }
                        if(messageListener != null){
                            messageListener.onAuthorLoaded();
                        }
                    }

                    ParseQuery<ParseObject> senderToReceiverQuery = ParseQuery.getQuery(ParseConsts.RELATION);
                    senderToReceiverQuery.whereEqualTo(ParseConsts.RELATION_SENDER, friendUser.getObjectId());
                    senderToReceiverQuery.whereGreaterThanOrEqualTo(ParseConsts.RELATION_STATUT, ParseConsts.RELATION_STATUT_FRIENDS);

                    ParseQuery<ParseObject> receiverToSenderQuery = ParseQuery.getQuery(ParseConsts.RELATION);
                    receiverToSenderQuery.whereEqualTo(ParseConsts.RELATION_RECEIVER, friendUser.getObjectId());
                    receiverToSenderQuery.whereGreaterThanOrEqualTo(ParseConsts.RELATION_STATUT, ParseConsts.RELATION_STATUT_FRIENDS);

                    List<ParseQuery<ParseObject>> friendsQueries = new ArrayList<ParseQuery<ParseObject>>();
                    friendsQueries.add(senderToReceiverQuery);
                    friendsQueries.add(receiverToSenderQuery);

                    ParseQuery<ParseObject> friendsQuery = ParseQuery.or(friendsQueries);
                    friendsQuery.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
                    friendsQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> friendsList, ParseException e) {

                            if(e == null){
                                friendsNumber = friendsList.size();
                                Log.d("Friend", "doFriendQuery - " + username + " have " + friendsNumber + " friends.");
                            }else{
                                Log.w("Friend", "doFriendQuery - " + username + " don't have any friends.");

                            }
                        }
                    });
                }else{
                    Log.e("Friend", "Friend - error when getting the friend : " + e.getMessage());
                }

            }
        });
    }

    public FTBitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(FTBitmap avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void loadFaces(final Context context, final UserListener.onFacesLoadedListener facesListener){
        faces = new ArrayList<Face>();

        if(FTWifi.isNetworkAvailable(context)) {

            ParseQuery<ParseObject> faceQuery = ParseQuery.getQuery(ParseConsts.FACE);
            faceQuery.whereEqualTo(ParseConsts.FACE_USER, friendUser);
            faceQuery.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
            faceQuery.orderByDescending(ParseConsts.CREATED_AT);
            faceQuery.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> faceList, ParseException e) {
                    if (e == null) {
                        facesNumber = faceList.size();
                        facesLoadedNumber = 0;

                        Log.d("Friend", "loadFaces - " + username + " has " + facesNumber + " faces.");

                        if (facesNumber != 0) {
                            for (ParseObject faceObject : faceList) {
                                final ParseFace face = new ParseFace(context, faceObject);
                                face.setFaceListener(new FaceListener() {
                                    @Override
                                    public void onFaceLoaded() {
                                        Log.d("Friend", "loadFaces - a face is loaded");
                                        facesLoadedNumber++;
                                        Log.d("Friend", "loadFaces - faces listeners are called");
                                        if (facesLoadedNumber == facesNumber) {
                                            isFacesLoaded = true;
                                            facesListener.onFacesLoaded();
                                        }
                                    }
                                });
                                faces.add(face);

                            }
                        } else {
                            Log.i("Friend", "loadFaces - " + username + " has 0 face");
                            isFacesLoaded = true;
                            facesListener.onFacesLoaded();
                        }
                    } else {
                        Log.e("Friend", "loadFaces - error when getting faces : " + e.getMessage());
                        facesNumber = 0;
                        facesLoadedNumber = 0;
                        isFacesLoaded = true;
                        facesListener.onFacesLoaded();
                    }
                }
            });
        }else{
            facesListener.onFacesLoaded();
            facesNumber = 0;
            facesLoadedNumber = 0;
            isFacesLoaded = true;
        }
    }

    public boolean isFacesLoaded(){
        return isFacesLoaded;
    }

    public int getFacesNumber(){
        return facesNumber;
    }

    public Face getFace(int index){
        return faces.get(index);
    }

    public ParseUser getParseUser(){
        return friendUser;
    }

    public int getFriendsNumber() {
        return friendsNumber;
    }

    public void setFriendListener(FriendListener friendListener){
        this.friendListener = friendListener;
    }
}
