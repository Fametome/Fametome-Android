package com.fametome.Dialog;

import android.content.Context;

import com.fametome.R;

public class DialogManager {

    public static void showInitialisationAccountDialog(Context context){
        final FTDialog dialog = new FTDialog(context);
        dialog.setTitle(R.string.demo_popup_account_title);
        dialog.setMessage(R.string.demo_popup_account_message);
        dialog.show();
    }

    public static void showInitialisationAddFaceDialog(Context context){
        final FTDialog dialog = new FTDialog(context);
        dialog.setTitle(R.string.demo_popup_add_face_title);
        dialog.setMessage(R.string.demo_popup_add_face_message);
        dialog.show();
    }

    public static void showInitialisationInboxDialog(Context context){
        final FTDialog dialog = new FTDialog(context);
        dialog.setTitle(R.string.demo_popup_inbox_title);
        dialog.setMessage(R.string.demo_popup_inbox_message);
        dialog.show();
    }

    public static void showInitialisationOutboxDialog(Context context){
        final FTDialog dialog = new FTDialog(context);
        dialog.setTitle(R.string.demo_popup_outbox_title);
        dialog.setMessage(R.string.demo_popup_outbox_message);
        dialog.show();
    }

    public static void showInitialisationFriendsDialog(Context context){
        final FTDialog dialog = new FTDialog(context);
        dialog.setTitle(R.string.demo_popup_friends_title);
        dialog.setMessage(R.string.demo_popup_friends_message);
        dialog.show();
    }

    public static void showInitialisationFriendSearchDialog(Context context){
        final FTDialog dialog = new FTDialog(context);
        dialog.setTitle(R.string.demo_popup_friend_search_title);
        dialog.setMessage(R.string.demo_popup_friend_search_message);
        dialog.show();
    }
}
