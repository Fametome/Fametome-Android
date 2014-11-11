package com.fametome.Dialog;

import android.content.Context;

import com.fametome.R;

public class DialogManager {

    public static void showInitialisationAccountDialog(Context context){
        showDialog(context, R.string.demo_popup_account_title, R.string.demo_popup_account_message);
    }

    public static void showInitialisationAddFaceDialog(Context context){
        showDialog(context, R.string.demo_popup_add_face_title, R.string.demo_popup_add_face_message);
    }

    public static void showInitialisationInboxDialog(Context context){
        showDialog(context, R.string.demo_popup_inbox_title, R.string.demo_popup_inbox_message);
    }

    public static void showInitialisationOutboxDialog(Context context){
        showDialog(context, R.string.demo_popup_outbox_title, R.string.demo_popup_outbox_message);
    }

    public static void showInitialisationFriendsDialog(Context context){
        showDialog(context, R.string.demo_popup_friends_title, R.string.demo_popup_friends_message);
    }

    public static void showInitialisationFriendSearchDialog(Context context){
        showDialog(context, R.string.demo_popup_friend_search_title, R.string.demo_popup_friend_search_message);
    }

    public static void showDialog(Context context, int titleResId, int messageResId){
        final FTDialog dialog = new FTDialog(context);
        dialog.setTitle(titleResId);
        dialog.setMessage(messageResId);
        dialog.show();
    }
}
