package com.fametome.activity.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.fametome.Dialog.FTDialog;
import com.fametome.R;
import com.fametome.activity.member.MainActivity;
import com.fametome.fragment.NavigationDrawerFragment;
import com.fametome.util.FTWifi;
import com.fametome.util.ParseConsts;
import com.fametome.widget.LoadingButton;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegisterStep1Activity extends Activity {

    LoadingButton proceed = null;
    EditText usernameView = null;
    EditText passwordView = null;
    EditText emailView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step_1);

        proceed = (LoadingButton)findViewById(R.id.proceed);
        usernameView = (EditText)findViewById(R.id.username);
        passwordView = (EditText)findViewById(R.id.password);
        emailView = (EditText)findViewById(R.id.email);

        proceed.setOnClickListener(clickProceed);
    }

    private View.OnClickListener clickProceed = new View.OnClickListener(){
        @Override
        public void onClick(View v){

            if(FTWifi.isNetworkAvailable(RegisterStep1Activity.this)){

                proceed.startLoading();


                final String username = usernameView.getText().toString().trim();
                final String password = passwordView.getText().toString().trim();
                final String email = emailView.getText().toString().trim();

                if(username.length() == 0){
                    proceed.stopLoading();

                    FTDialog dialog = new FTDialog(RegisterStep1Activity.this);
                    dialog.setTitle(getString(R.string.register_step_1_empty_username_title));
                    dialog.setMessage(getString(R.string.register_step_1_empty_username_message));
                    dialog.show();

                }else if(password.length() == 0){
                    proceed.stopLoading();

                    FTDialog dialog = new FTDialog(RegisterStep1Activity.this);
                    dialog.setTitle(getString(R.string.register_step_1_empty_password_title));
                    dialog.setMessage(getString(R.string.register_step_1_empty_password_message));
                    dialog.show();

                }else if(email.length() == 0){
                    proceed.stopLoading();

                    FTDialog dialog = new FTDialog(RegisterStep1Activity.this);
                    dialog.setTitle(getString(R.string.register_step_1_empty_email_title));
                    dialog.setMessage(getString(R.string.register_step_1_empty_email_message));
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

                                Intent mainActivityIntent = new Intent(RegisterStep1Activity.this, MainActivity.class);
                                mainActivityIntent.putExtra("fragmentPosInMenu", NavigationDrawerFragment.FRAGMENT_ACCOUNT);

                                startActivity(mainActivityIntent);
                                finish();
                            } else {
                                proceed.stopLoading();

                                FTDialog dialog = new FTDialog(RegisterStep1Activity.this);
                                dialog.setTitle(R.string.register_error_title);
                                dialog.setMessage(R.string.register_error_message);
                                dialog.show();

                            }
                        }
                    });
                }
            }else{
                FTDialog dialog = new FTDialog(RegisterStep1Activity.this);
                dialog.setTitle(R.string.register_step_1_register_without_network_title);
                dialog.setMessage(R.string.register_step_1_register_without_network_message);
                dialog.show();
            }
        }
    };

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegisterStep1Activity.this, RegisterLoginActivity.class));
        super.onBackPressed();
    }

}
