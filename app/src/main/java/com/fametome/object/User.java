package com.fametome.object;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.fametome.listener.FaceListener;
import com.fametome.listener.FriendListener;
import com.fametome.listener.MessageListener;
import com.fametome.listener.UserListener;
import com.fametome.util.FTWifi;
import com.fametome.util.ParseConsts;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class User {
    private static User instance = null;

    private Context context;

    private String username;
    private Bitmap avatar;
    private int messagesSendNumber;

    private List<ParseFace> faces;
    private List<ParseFriend> friends;
    private List<FriendRequest> friendsRequests;
    private List<ParseMessage> messages;

    private int facesNumber;
    private int facesLoadedNumber;
    private int friendsNumber;
    private int friendsLoadedNumber;
    private int friendsRequestsNumber;
    private int friendsRequestsLoadedNumber;
    private int messagesNumber;
    private int messagesLoadedNumber;

    private boolean isUserLoaded;
    private boolean isFacesLoaded;
    private boolean isFriendsLoaded;
    private boolean isFriendsRequestsLoaded;
    private boolean isMessagesLoaded;

    private List<UserListener.onUserLoadedListener> userLoadedListeners;
    private List<UserListener.onFacesLoadedListener> facesLoadedListeners;
    private List<UserListener.onFriendsLoadedListener> friendsLoadedListeners;
    private List<UserListener.onMessagesLoadedListener> messagesLoadedListeners;

    private User(Context context){
        this.context = context;

        userLoadedListeners = new ArrayList<UserListener.onUserLoadedListener>();
        facesLoadedListeners = new ArrayList<UserListener.onFacesLoadedListener>();
        friendsLoadedListeners = new ArrayList<UserListener.onFriendsLoadedListener>();
        messagesLoadedListeners = new ArrayList<UserListener.onMessagesLoadedListener>();

        /* Recupération des infos basiques de l'utilisateur */

        username = ParseUser.getCurrentUser().getUsername();
        messagesSendNumber = ParseUser.getCurrentUser().getInt(ParseConsts.USER_STATS_MESSAGE_SEND_NUMBER);
        if(ParseUser.getCurrentUser().get(ParseConsts.USER_AVATAR) != null) {
            ((ParseFile) ParseUser.getCurrentUser().get(ParseConsts.USER_AVATAR)).getDataInBackground(new GetDataCallback() {
                public void done(byte[] data, ParseException e) {
                    isUserLoaded = true;
                    if (e == null) {
                        avatar = BitmapFactory.decodeByteArray(data, 0, data.length);
                        for(int i = 0; i < userLoadedListeners.size(); i++){
                            userLoadedListeners.get(i).onUserLoaded();
                        }
                    } else {
                        Log.e("User", "User - error when getting avatar : " + e.getMessage());
                        for(int i = 0; i < userLoadedListeners.size(); i++){
                            userLoadedListeners.get(i).onUserLoaded();
                        }
                    }
                }
            });
        }else{
            isUserLoaded = true;
            for(int i = 0; i < userLoadedListeners.size(); i++){
                userLoadedListeners.get(i).onUserLoaded();
            }
        }

        /* Récupération des faces de l'utilisateur */
        faces = new ArrayList<ParseFace>();

        ParseQuery<ParseObject> faceQuery = ParseQuery.getQuery(ParseConsts.FACE);
        faceQuery.whereEqualTo(ParseConsts.FACE_USER, ParseUser.getCurrentUser());
        faceQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        faceQuery.orderByDescending(ParseConsts.CREATED_AT);
        faceQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> facesObjects, ParseException e) {
                if (e == null) {
                    facesNumber = facesObjects.size();
                    facesLoadedNumber = 0;
                    if(facesNumber != 0) {
                        for (int i = 0; i < facesNumber; i++) {
                            faces.add(new ParseFace());
                            final ParseFace face = new ParseFace(facesObjects.get(i));
                            face.setFaceListener(onFaceLoaded);
                            faces.set(i, face);

                        }
                    }else{
                        Log.i("User", "User - " + username + " has 0 faces");
                        for(int i = 0; i < facesLoadedListeners.size(); i++){
                            facesLoadedListeners.get(i).onFacesLoaded();
                        }
                        facesLoadedNumber = 0;
                        isFacesLoaded = true;
                    }
                } else {
                    Log.e("User", "User - error when getting faces : " + e.getMessage());
                }
            }
        });

        /* Récupération des friends de l'utilisateur */
        refreshFriends();

        /* Récupération des friends requests de l'utilisateur */
        refreshFriendsRequests();

        /** RÉCUPÉRATION DES MESSAGES DE L'UTILISATEUR **/
        refreshMessages();

    }

    public static User createInstance(Context context){
        if(instance == null){
            instance = new User(context);
        }
        return instance;
    }

    public static User getInstance(){
        if(instance == null){
            instance = new User(null);
        }
        return instance;
    }

    public static void deleteInstance(){
        instance = null;
    }

    /* LISTENER SUR LE CHARGEMENT DES FACES */
    private FaceListener onFaceLoaded = new FaceListener() {
        @Override
        public void onFaceLoaded() {
            Log.d("User", "onFaceLoaded - a face is loaded");
            facesLoadedNumber++;
            for(int i = 0; i < facesLoadedListeners.size(); i++){
                facesLoadedListeners.get(i).onFacesLoaded();
                Log.d("User", "onFaceLoaded - faces listeners are called");
            }
            if(facesLoadedNumber == facesNumber) {
                isFacesLoaded = true;
            }
        }
    };

    /* LISTENER SUR LE CHARGEMENT DES FRIENDS */
    private FriendListener onFriendLoaded = new FriendListener() {
        @Override
        public void onFriendLoaded() {
            Log.d("User", "onFriendLoaded - a friend is loaded");
            friendsLoadedNumber++;
            for(int i = 0; i < friendsLoadedListeners.size(); i++){
                friendsLoadedListeners.get(i).onFriendsLoaded();
                Log.d("User", "onFriendLoaded - friends listeners are called");
            }
            if(friendsLoadedNumber == friendsNumber) {
                isFriendsLoaded = true;
            }
        }
    };

    /* LISTENER SUR LE CHARGEMENT DES FRIENDS REQUESTS */
    private FriendListener onFriendRequestLoaded = new FriendListener() {
        @Override
        public void onFriendLoaded() {
            Log.d("User", "onFriendLoaded - a friend request is loaded");
            friendsRequestsLoadedNumber++;
            if(friendsRequestsLoadedNumber == friendsRequestsNumber){
                for(int i = 0; i < friendsLoadedListeners.size(); i++){
                    friendsLoadedListeners.get(i).onFriendsRequestsLoaded();
                    Log.d("User", "onFriendLoaded - friends listeners are called for requests");
                }
                if(friendsRequestsLoadedNumber == friendsRequestsNumber) {
                    isFriendsRequestsLoaded = true;
                }
            }
        }
    };

    /* LISTENER SUR LE CHARGEMENT DES MESSAGES */
    private MessageListener onMessageLoaded = new MessageListener() {
        @Override
        public void onAuthorLoaded() {
            Log.d("User", "onMessageLoaded - a message is loaded");
            messagesLoadedNumber++;

            if(messagesLoadedNumber == messagesNumber) {
                isMessagesLoaded = true;
                for(int i = 0; i < messagesLoadedListeners.size(); i++){
                    messagesLoadedListeners.get(i).onMessagesLoaded();
                    Log.d("User", "onMessageLoaded - messages listeners are called");
                }
            }

        }

        @Override
        public void onFlashsLoaded() {

        }
    };

    /** FONCTIONS SUR LES INFOS BASIQUES DU USER **/

    public boolean isUserLoaded(){
        return isUserLoaded;
    }

    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }

    public Bitmap getAvatar(){
        return avatar;
    }

    public void setAvatar(Bitmap avatar){
        Log.d("User", "setAvatar - starting the function");
        this.avatar = avatar;
        for(int i = 0; i < userLoadedListeners.size(); i++){
            userLoadedListeners.get(i).onUserLoaded();
            Log.d("User", "setAvatar - basic user listeners are called for avatar");
        }
    }

    public int getMessagesSendNumber(){
        return messagesSendNumber;
    }

    public List<ParseMessage> getMessages(){
        return messages;
    }


    /** FONCTIONS SUR LES FACES **/

    public boolean isFacesLoaded(){
        return isFacesLoaded;
    }

    public int getFacesNumber(){
        return facesNumber;
    }

    public List<ParseFace> getFaces(){
        return faces;
    }

    public ParseFace getFace(int index){
        return faces.get(index);
    }

    public void addFace(ParseFace faceToAdd){
        faceToAdd.setFaceListener(onFaceLoaded);
        facesNumber++;
        facesLoadedNumber++;
        faces.add(0, faceToAdd);
        for(int i = 0; i < facesLoadedListeners.size(); i++){
            facesLoadedListeners.get(i).onFacesLoaded();
        }
    }

    public void removeFace(int index){
        faces.remove(index);
        facesNumber--;
        facesLoadedNumber--;
        for(int i = 0; i < facesLoadedListeners.size(); i++){
            facesLoadedListeners.get(i).onFacesLoaded();
        }
    }

    public ParseFace getFaceWithText(String text){
        for(int i = 0; i < getFacesNumber(); i++){
            if(getFace(i).getText().equals(text)){
                return getFace(i);
            }
        }
        return null;
    };



    /** FONCTIONS SUR LES FRIENDS **/

    public void refreshFriends(){
        friends = new ArrayList<ParseFriend>();

        ParseQuery<ParseObject> senderToReceiverQuery = ParseQuery.getQuery(ParseConsts.RELATION);
        senderToReceiverQuery.whereEqualTo(ParseConsts.RELATION_SENDER, ParseUser.getCurrentUser().getObjectId());
        senderToReceiverQuery.whereGreaterThanOrEqualTo(ParseConsts.RELATION_STATUT, ParseConsts.RELATION_STATUT_FRIENDS);

        ParseQuery<ParseObject> receiverToSenderQuery = ParseQuery.getQuery(ParseConsts.RELATION);
        receiverToSenderQuery.whereEqualTo(ParseConsts.RELATION_RECEIVER, ParseUser.getCurrentUser().getObjectId());
        receiverToSenderQuery.whereGreaterThanOrEqualTo(ParseConsts.RELATION_STATUT, ParseConsts.RELATION_STATUT_FRIENDS);

        List<ParseQuery<ParseObject>> friendsQueries = new ArrayList<ParseQuery<ParseObject>>();
        friendsQueries.add(senderToReceiverQuery);
        friendsQueries.add(receiverToSenderQuery);

        ParseQuery<ParseObject> friendsQuery = ParseQuery.or(friendsQueries);
        friendsQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        friendsQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> friendsList, ParseException e) {

                friendsNumber = 0;
                friendsLoadedNumber = 0;

                if(e == null) {
                    Log.w("User", "User - " + friendsList.size() + "  friends.");

                    friendsNumber = friendsList.size();
                    friendsLoadedNumber = 0;
                    if(friendsNumber != 0) {
                        for (ParseObject friendObject : friendsList) {
                            final ParseFriend friend = new ParseFriend(friendObject);
                            friend.setFriendListener(onFriendLoaded);
                            friends.add(friend);

                        }
                    }else{
                        Log.i("User", "User - " + username + " has 0 faces");
                        friendsLoadedNumber = 0;
                        isFriendsLoaded = true;
                        for(int i = 0; i < friendsLoadedListeners.size(); i++){
                            friendsLoadedListeners.get(i).onFriendsLoaded();
                        }
                    }
                    Log.d("User", "User - " + username + " have " + friendsNumber + " friends.");
                }else{
                    Log.w("User", "User - " + username + " don't have any friends.");
                }
            }
        });
    }

    public boolean isFriendsLoaded(){
        return isFriendsLoaded;
    }

    public int getFriendsNumber(){
        return friendsNumber;
    }

    public List<ParseFriend> getFriends(){
        return friends;
    }

    public Friend getFriend(int index){
        return index < friendsNumber ? friends.get(index) : null;
    }

    public void addFriend(ParseFriend friendToAdd){
        friendsNumber++;
        friendsLoadedNumber++;
        friends.add(0, friendToAdd);
        for(int i = 0; i < friendsLoadedListeners.size(); i++){
            friendsLoadedListeners.get(i).onFriendsLoaded();
        }
    }



    /** FONCTIONS SUR LES FRIENDS REQUESTS **/

    public void refreshFriendsRequests(){

        friendsRequests = new ArrayList<FriendRequest>();

        ParseQuery<ParseObject> requestSenderToReceiverQuery = ParseQuery.getQuery(ParseConsts.RELATION);
        requestSenderToReceiverQuery.whereEqualTo(ParseConsts.RELATION_SENDER, ParseUser.getCurrentUser().getObjectId());
        requestSenderToReceiverQuery.whereEqualTo(ParseConsts.RELATION_STATUT, ParseConsts.RELATION_STATUT_REQUEST_IN_PROGRESS);

        ParseQuery<ParseObject> requestReceiverToSenderQuery = ParseQuery.getQuery(ParseConsts.RELATION);
        requestReceiverToSenderQuery.whereEqualTo(ParseConsts.RELATION_RECEIVER, ParseUser.getCurrentUser().getObjectId());
        requestReceiverToSenderQuery.whereEqualTo(ParseConsts.RELATION_STATUT, ParseConsts.RELATION_STATUT_REQUEST_IN_PROGRESS);

        List<ParseQuery<ParseObject>> friendsRequestQueries = new ArrayList<ParseQuery<ParseObject>>();
        friendsRequestQueries.add(requestSenderToReceiverQuery);
        friendsRequestQueries.add(requestReceiverToSenderQuery);

        ParseQuery<ParseObject> friendsRequestQuery = ParseQuery.or(friendsRequestQueries);
        friendsRequestQuery.orderByDescending(ParseConsts.CREATED_AT);
        friendsRequestQuery.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
        friendsRequestQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> friendsRequestsList, ParseException e) {

                if(e == null) {
                    Log.w("User", "refreshFriendsRequests - " + friendsRequestsList.size() + "  friends requests.");

                    friendsRequestsNumber = friendsRequestsList.size();
                    friendsRequestsLoadedNumber = 0;
                    if(friendsRequestsNumber != 0) {
                        for (int i = 0; i < friendsRequestsNumber; i++) {
                            final FriendRequest friendRequest = new FriendRequest();
                            friendRequest.setFriendListener(onFriendRequestLoaded);
                            friendRequest.load(friendsRequestsList.get(i));
                            friendsRequests.add(friendRequest);

                        }
                    }else{
                        Log.i("User", "refreshFriendsRequests - " + username + " has 0 friends requests");
                        for(int i = 0; i < friendsLoadedListeners.size(); i++){
                            friendsLoadedListeners.get(i).onFriendsRequestsLoaded();
                        }
                        friendsRequestsLoadedNumber = 0;
                        isFriendsRequestsLoaded = true;
                    }
                    Log.d("User", "refreshFriendsRequests - " + username + " have " + friendsRequestsNumber + " requests.");
                }else{
                    Log.w("User", "refreshFriendsRequests - " + username + " don't have any requests.");
                }
            }
        });
    }

    public int getFriendsRequestsNumber(){
        return friendsRequestsNumber;
    }

    public FriendRequest getFriendRequest(int index){
        return friendsRequests.get(index);
    }

    public void addFriendRequest(FriendRequest friendRequestToAdd){
        friendsRequestsNumber++;
        friendsRequestsLoadedNumber++;
        friendsRequests.add(0, friendRequestToAdd);
        for(int i = 0; i < friendsLoadedListeners.size(); i++){
            friendsLoadedListeners.get(i).onFriendsRequestsLoaded();
        }
    }

    public List<FriendRequest> getFriendsRequests(){
        return friendsRequests;
    }

    public void removeFriendRequest(int index){
        friendsRequests.remove(index);
        friendsRequestsNumber--;
        friendsRequestsLoadedNumber--;
        for(int i = 0; i < friendsLoadedListeners.size(); i++){
            friendsLoadedListeners.get(i).onFriendsRequestsLoaded();
        }
    }

    /** FONCTIONS SUR LES MESSAGES **/

    public void refreshMessages(){
        messages = new ArrayList<ParseMessage>();

        if(FTWifi.isNetworkAvailable(context)) {
            ParseQuery<ParseObject> messageQuery = ParseQuery.getQuery(ParseConsts.MESSAGE);
            messageQuery.whereEqualTo(ParseConsts.MESSAGE_RECIPIENT_ID_ARRAY, ParseUser.getCurrentUser().getObjectId());
            messageQuery.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ONLY);
            messageQuery.orderByDescending(ParseConsts.CREATED_AT);
            messageQuery.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> messageList, ParseException e) {
                    if (e == null) {

                        messagesNumber = messageList.size();
                        messagesLoadedNumber = 0;
                        isMessagesLoaded = false;

                        if(messagesNumber != 0){
                            for (int i = 0; i < messagesNumber; i++) {
                                final ParseMessage message = new ParseMessage();
                                message.load(messageList.get(i), onMessageLoaded);
                                messages.add(message);

                            }
                        }else{
                            for (int i = 0; i < messagesLoadedListeners.size(); i++) {
                                messagesLoadedListeners.get(i).onMessagesLoaded();
                                Log.d("User", "refreshMessages - messages listeners are called");
                            }
                        }

                    } else {
                        Log.e("User", "refreshMessages - error when getting messages : " + e.getMessage());
                        for (int i = 0; i < messagesLoadedListeners.size(); i++) {
                            messagesLoadedListeners.get(i).onMessagesLoaded();
                            Log.d("User", "refreshMessages - messages listeners are called for error");
                        }
                    }
                }
            });
        }else{
            Log.w("User", "refreshMessages - no network");
            for (int i = 0; i < messagesLoadedListeners.size(); i++) {
                messagesLoadedListeners.get(i).onMessagesLoaded();
                Log.d("User", "refreshMessages - messages listeners are called");
            }
        }
    }

    public int getMessagesNumber(){
        return messagesNumber;
    }

    public int getMessagesLoadedNumber(){
        return messagesLoadedNumber;
    }

    public ParseMessage getMessage(int index){
        return messages.get(index);
    }

    /* AJOUT DES LISTENERS */

    public void addUserLoadedListener(UserListener.onUserLoadedListener userLoadedListener){
        userLoadedListeners.add(userLoadedListener);
    }

    public void addFacesLoadedListener(UserListener.onFacesLoadedListener facesLoadedListener){
        facesLoadedListeners.add(facesLoadedListener);
    }

    public void addFriendsLoadedListener(UserListener.onFriendsLoadedListener friendsLoadedListener){
        friendsLoadedListeners.add(friendsLoadedListener);
    }

    public void addMessagesLoadedListener(UserListener.onMessagesLoadedListener messagesLoadedListener){
        messagesLoadedListeners.add(messagesLoadedListener);
    }

}