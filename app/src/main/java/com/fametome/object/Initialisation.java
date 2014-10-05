package com.fametome.object;

import android.content.Context;
import android.util.Log;

import com.fametome.database.Database;
import com.fametome.database.DatabaseQuery;

public class Initialisation {
    private static Initialisation instance;

    private DatabaseQuery databaseQuery;

    private boolean account = false;
    private boolean addFace = false;
    private boolean inbox = false;
    private boolean outbox = false;
    private boolean friends = false;
    private boolean friendSearch = false;

    public Initialisation(){

    }

    public Initialisation(Context context){
        databaseQuery = new DatabaseQuery(context);

        Log.d("Initialisation", "Initialisation - retrieving the initialisation state from the database");
        final Initialisation databaseInitialisation = databaseQuery.getInitialisation();
        account = databaseInitialisation.isAccount();
        addFace = databaseInitialisation.isAddFace();
        inbox = databaseInitialisation.isInbox();
        outbox = databaseInitialisation.isOutbox();
        friends = databaseInitialisation.isFriends();
        friendSearch = databaseInitialisation.isFriendSearch();
    }

    public static Initialisation createInstance(Context context){
        instance = new Initialisation(context);
        return instance;
    }

    public static Initialisation getInstance(){
        if(instance == null){
            instance = new Initialisation();
        }
        return instance;
    }

    public boolean isAccount() {
        return account;
    }
    public void setAccount(boolean account) {
        this.account = account;
        if(databaseQuery != null){
            databaseQuery.setValue(Database.INITIALISATION_ACCOUNT, account);
        }
    }

    public boolean isAddFace() {
        return addFace;
    }
    public void setAddFace(boolean addFace) {
        this.addFace = addFace;
        if(databaseQuery != null){
            databaseQuery.setValue(Database.INITIALISATION_ADD_FACE, addFace);
        }
    }

    public boolean isInbox() {
        return inbox;
    }
    public void setInbox(boolean inbox) {
        this.inbox = inbox;
        if(databaseQuery != null){
            databaseQuery.setValue(Database.INITIALISATION_INBOX, inbox);
        }
    }

    public boolean isOutbox() {
        return outbox;
    }
    public void setOutbox(boolean outbox) {
        this.outbox = outbox;
        if(databaseQuery != null){
            databaseQuery.setValue(Database.INITIALISATION_OUTBOX, outbox);
        }
    }

    public boolean isFriends() {
        return friends;
    }
    public void setFriends(boolean friends) {
        this.friends = friends;
        if(databaseQuery != null){
            databaseQuery.setValue(Database.INITIALISATION_FRIENDS, friends);
        }
    }

    public boolean isFriendSearch() {
        return friendSearch;
    }
    public void setFriendSearch(boolean friendSearch) {
        this.friendSearch = friendSearch;
        if(databaseQuery != null){
            databaseQuery.setValue(Database.INITIALISATION_FRIEND_SEARCH, friendSearch);
        }
    }
}
