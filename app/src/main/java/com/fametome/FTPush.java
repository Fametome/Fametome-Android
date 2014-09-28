package com.fametome;

import android.util.Log;

import com.fametome.object.User;
import com.fametome.util.ParseConsts;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class FTPush {

    private static final long NOTIFICATION_LIFE_TIME = 60 * 60 * 24;



    public static void sendPushToAllFriends(String title, String message){

        final int friendsNumber = User.getInstance().getFriendsNumber();
        for(int i = 0; i < friendsNumber; i++) {
            sendPushToFriend(User.getInstance().getFriend(i).getParseUser().getObjectId(), title, message);
        }
    }

    public static void sendPushToMultipleFriends(List<String> friendsIds, String title, String message){

        final int friendsNumber = friendsIds.size();
        for(int i = 0; i < friendsNumber; i++) {
            sendPushToFriend(friendsIds.get(i), title, message);
        }
    }

    public static void sendPushToFriend(String friendId, String title, String message){

        ParseQuery<ParseUser> friendQuery = ParseUser.getQuery();
        friendQuery.whereEqualTo(ParseConsts.ID, friendId);

        ParseQuery<ParseInstallation> installationQuery = ParseInstallation.getQuery();
        installationQuery.whereMatchesQuery(ParseConsts.INSTALLATION_USER, friendQuery);

        ParsePush push = new ParsePush();
        push.setQuery(installationQuery);
        push.setExpirationTimeInterval(NOTIFICATION_LIFE_TIME);
        push.setData(createJSONForMessage(title, message));
        push.sendInBackground();
    }

    private static JSONObject createJSONForMessage(String title, String message){

        JSONObject data = new JSONObject();
        try {

            data.put("title", title);
            data.put("alert", message);
            data.put("badge", "Increment");
            data.put("sound", "cheering.caf");

        } catch (JSONException e) {
            Log.e("FTPush", "createJSONForMessage - JSONException : " + e.getMessage());
        }

        return data;

    }
}
