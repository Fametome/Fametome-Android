package com.fametome.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.fametome.object.Initialisation;

public class DatabaseQuery {

    private final Context context;
    private Database database;
    private SQLiteDatabase db;

    public DatabaseQuery(Context context){
        this.context = context;
        database = new Database(context);
    }

    public void open() throws SQLException {
        db = database.getWritableDatabase();
    }

    public void close(){
        database.close();
    }

    /** QUERIES AVEC LA TABLE DU PROCESSUS D'INITIALISATION **/

    /* To create the initialisation at signUp or Login */
    public void createInitialisation(boolean defaultValue){
        open();

        ContentValues values = new ContentValues();
        values.put(Database.INITIALISATION_ACCOUNT, defaultValue);
        values.put(Database.INITIALISATION_ADD_FACE, defaultValue);
        values.put(Database.INITIALISATION_INBOX, defaultValue);
        values.put(Database.INITIALISATION_OUTBOX, defaultValue);
        values.put(Database.INITIALISATION_FRIENDS, defaultValue);
        values.put(Database.INITIALISATION_FRIEND_SEARCH, defaultValue);

        db.insert(Database.TABLE_INITIALISATION, null, values);

        close();
    }

    /* To remove the initialisation at signOut */
    public void removeInitialisation(){
        open();

        db.delete(Database.TABLE_INITIALISATION, null, null);

        close();
    }

    /* To set the value of a column */
    public void setValue(String column, boolean booleanValue){
        open();

        final int value = booleanValue ? 1 : 0;

        ContentValues values = new ContentValues();
        values.put(column, value);
        db.update(Database.TABLE_INITIALISATION, values, null, null);

        close();

    }

    /* To get the initialisation */
    public Initialisation getInitialisation(){
        Initialisation initialisation = new Initialisation();

        open();

        String query = "SELECT * FROM " + Database.TABLE_INITIALISATION;

        Cursor cursor = db.rawQuery(query, null);

        if(cursor != null && cursor.getCount() != 0){
            cursor.moveToFirst();
            initialisation.setAccount(cursor.getInt(cursor.getColumnIndex(Database.INITIALISATION_ACCOUNT)) == 1);
            initialisation.setAddFace(cursor.getInt(cursor.getColumnIndex(Database.INITIALISATION_ADD_FACE)) == 1);
            initialisation.setInbox(cursor.getInt(cursor.getColumnIndex(Database.INITIALISATION_INBOX)) == 1);
            initialisation.setOutbox(cursor.getInt(cursor.getColumnIndex(Database.INITIALISATION_OUTBOX)) == 1);
            initialisation.setFriends(cursor.getInt(cursor.getColumnIndex(Database.INITIALISATION_FRIENDS)) == 1);
            initialisation.setFriendSearch(cursor.getInt(cursor.getColumnIndex(Database.INITIALISATION_FRIEND_SEARCH)) == 1);
        }

        close();

        /* Print on log the current initialisation */
        Log.d("DatabaseQuery", "getInitialisation - isAccount : " + initialisation.isAccount());
        Log.d("DatabaseQuery", "getInitialisation - isAddFace : " + initialisation.isAddFace());
        Log.d("DatabaseQuery", "getInitialisation - isInbox : " + initialisation.isInbox());
        Log.d("DatabaseQuery", "getInitialisation - isOutbox : " + initialisation.isOutbox());
        Log.d("DatabaseQuery", "getInitialisation - isFriends : " + initialisation.isFriends());
        Log.d("DatabaseQuery", "getInitialisation - isFriendSearch : " + initialisation.isFriendSearch());

        return initialisation;
    }
}
