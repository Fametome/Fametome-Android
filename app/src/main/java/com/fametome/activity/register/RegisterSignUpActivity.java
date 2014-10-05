package com.fametome.activity.register;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.fametome.Dialog.FTDialog;
import com.fametome.R;
import com.fametome.activity.member.MainActivity;
import com.fametome.database.DatabaseQuery;
import com.fametome.fragment.NavigationDrawerFragment;
import com.fametome.fragment.outbox.OutboxChooseRecipientsFragment;
import com.fametome.fragment.outbox.OutboxFragment;
import com.fametome.object.Face;
import com.fametome.object.Flash;
import com.fametome.object.Message;
import com.fametome.object.ParseFace;
import com.fametome.object.ParseFlash;
import com.fametome.object.ParseFriend;
import com.fametome.object.ParseMessage;
import com.fametome.util.FTBitmap;
import com.fametome.util.FTWifi;
import com.fametome.util.ParseConsts;
import com.fametome.widget.LoadingButton;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.lang.reflect.Array;
import java.util.Arrays;

public class RegisterSignUpActivity extends Activity {

    LoadingButton proceed = null;
    EditText usernameView = null;
    EditText passwordView = null;
    EditText emailView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_sign_up);

        proceed = (LoadingButton)findViewById(R.id.proceed);
        usernameView = (EditText)findViewById(R.id.username);
        passwordView = (EditText)findViewById(R.id.password);
        emailView = (EditText)findViewById(R.id.email);

        proceed.setOnClickListener(clickProceed);
    }

    private View.OnClickListener clickProceed = new View.OnClickListener(){
        @Override
        public void onClick(View v){

            if(FTWifi.isNetworkAvailable(RegisterSignUpActivity.this)){

                proceed.startLoading();

                final String username = usernameView.getText().toString().trim();
                final String password = passwordView.getText().toString().trim();
                final String email = emailView.getText().toString().trim();

                if(username.length() == 0){
                    proceed.stopLoading();
                    FTDialog dialog = new FTDialog(RegisterSignUpActivity.this);
                    dialog.setTitle(getString(R.string.register_sign_up_empty_username_title));
                    dialog.setMessage(getString(R.string.register_sign_up_empty_username_message));
                    dialog.show();

                }else if(password.length() == 0){
                    proceed.stopLoading();

                    FTDialog dialog = new FTDialog(RegisterSignUpActivity.this);
                    dialog.setTitle(getString(R.string.register_sign_up_empty_password_title));
                    dialog.setMessage(getString(R.string.register_sign_up_empty_password_message));
                    dialog.show();

                }else if(email.length() == 0){
                    proceed.stopLoading();

                    FTDialog dialog = new FTDialog(RegisterSignUpActivity.this);
                    dialog.setTitle(getString(R.string.register_sign_up_empty_email_title));
                    dialog.setMessage(getString(R.string.register_sign_up_empty_email_message));
                    dialog.show();

                }else {

                    ParseUser user = new ParseUser();
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setEmail(email);
                    user.put("flashSmsSendCount", 0);
                    user.put("flashPhotoSendCount", 0);
                    user.put("flashFaceSendCount", 0);
                    user.put("flashSendCount", 0);
                    user.put("messageSendCount", 0);
                    user.put("phone", "");

                    user.signUpInBackground(new SignUpCallback() {

                        public void done(ParseException e) {
                            if (e == null) {

                                // Inscription au Push
                                ParseInstallation currentInstallation = ParseInstallation.getCurrentInstallation();
                                currentInstallation.put(ParseConsts.INSTALLATION_USER, ParseUser.getCurrentUser());
                                currentInstallation.saveEventually();

                                sendDemoMessage();

                                /* Create a new initialisation for the user */
                                final DatabaseQuery databaseQuery = new DatabaseQuery(RegisterSignUpActivity.this);
                                databaseQuery.createInitialisation(false);

                                Intent mainActivityIntent = new Intent(RegisterSignUpActivity.this, MainActivity.class);
                                mainActivityIntent.putExtra("fragmentPosInMenu", NavigationDrawerFragment.FRAGMENT_ACCOUNT);

                                startActivity(mainActivityIntent);
                                finish();
                            } else {
                                proceed.stopLoading();

                                if(e.getCode() == ParseException.USERNAME_TAKEN){
                                    FTDialog dialog = new FTDialog(RegisterSignUpActivity.this);
                                    dialog.setTitle(R.string.register_sign_up_username_taken_title);
                                    dialog.setMessage(R.string.register_sign_up_username_taken_message);
                                    dialog.show();

                                }else if(e.getCode() == ParseException.EMAIL_TAKEN){
                                    FTDialog dialog = new FTDialog(RegisterSignUpActivity.this);
                                    dialog.setTitle(R.string.register_sign_up_email_taken_title);
                                    dialog.setMessage(R.string.register_sign_up_email_taken_message);
                                    dialog.show();

                                }else if(e.getCode() == ParseException.INVALID_EMAIL_ADDRESS){
                                    FTDialog dialog = new FTDialog(RegisterSignUpActivity.this);
                                    dialog.setTitle(R.string.register_sign_up_email_invalid_title);
                                    dialog.setMessage(R.string.register_sign_up_email_invalid_message);
                                    dialog.show();

                                }else{
                                    FTDialog dialog = new FTDialog(RegisterSignUpActivity.this);
                                    dialog.setTitle(R.string.register_sign_up_error_title);
                                    dialog.setMessage(R.string.register_sign_up_error_message);
                                    dialog.show();
                                }

                            }
                        }
                    });
                }
            }else{
                FTDialog dialog = new FTDialog(RegisterSignUpActivity.this);
                dialog.setTitle(R.string.register_sign_up_register_without_network_title);
                dialog.setMessage(R.string.register_sign_up_register_without_network_message);
                dialog.show();
            }
        }
    };

    public void sendDemoMessage(){

        ParseMessage message = new ParseMessage();
        ParseFriend author = new ParseFriend();
        author.setId(ParseConsts.USER_FAMETOME_ID);
        author.setUsername(ParseConsts.USER_FAMETOME_USERNAME);
        message.setAuthor(author);

        message.addFlash(new ParseFlash(getString(R.string.demo_message_flash_1)));
        message.addFlash(new ParseFlash(getString(R.string.demo_message_flash_2)));
        message.addFlash(new ParseFlash(getString(R.string.demo_message_flash_3)));
        message.addFlash(new ParseFlash(getString(R.string.demo_message_flash_4)));
        message.addFlash(new ParseFlash(new FTBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.picture_medium))));
        message.addFlash(new ParseFlash(new ParseFace(ParseConsts.USER_FAMETOME_FACE_ID)));
        message.addFlash(new ParseFlash(getString(R.string.demo_message_flash_7)));

        OutboxChooseRecipientsFragment.sendMessage(this, message, OutboxFragment.TYPE_DEMO_MESSAGE, Arrays.asList(ParseUser.getCurrentUser().getObjectId()));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegisterSignUpActivity.this, RegisterLoginActivity.class));
        super.onBackPressed();
    }

}
