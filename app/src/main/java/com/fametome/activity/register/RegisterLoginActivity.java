package com.fametome.activity.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fametome.Dialog.FTDialog;
import com.fametome.R;
import com.fametome.activity.member.MainActivity;
import com.fametome.util.FTWifi;
import com.fametome.util.ParseConsts;
import com.fametome.widget.LoadingButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

public class RegisterLoginActivity extends Activity{

    LoadingButton connect = null;
    Button register = null;
    EditText usernameView = null;
    EditText passwordView = null;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_login);

        connect = (LoadingButton)findViewById(R.id.connect);
        register = (Button)findViewById(R.id.register);
        usernameView = (EditText)findViewById(R.id.username);
        passwordView = (EditText)findViewById(R.id.password);

        final ParseUser user = ParseUser.getCurrentUser();

        if(user != null){
            startActivity(new Intent(RegisterLoginActivity.this, MainActivity.class));
            finish();
        }

        connect.setOnClickListener(clickConnect);
        register.setOnClickListener(clickRegister);
    }

    private View.OnClickListener clickConnect = new View.OnClickListener(){
        @Override
        public void onClick(View v){

            if(FTWifi.isNetworkAvailable(RegisterLoginActivity.this)) {

                connect.startLoading();

                final String username = usernameView.getText().toString().trim();
                final String password = passwordView.getText().toString().trim();

                ParseUser.logInInBackground(username, password, new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {

                            // Inscription au Push
                            ParseInstallation currentInstallation = ParseInstallation.getCurrentInstallation();
                            currentInstallation.put(ParseConsts.INSTALLATION_USER, ParseUser.getCurrentUser());
                            currentInstallation.saveEventually();

                            startActivity(new Intent(RegisterLoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            connect.stopLoading();

                            FTDialog dialog = new FTDialog(RegisterLoginActivity.this);
                            dialog.setTitle(R.string.register_login_error_title);
                            dialog.setMessage(R.string.register_login_error_message);
                            dialog.show();
                        }
                    }
                });

            }else{
                FTDialog dialog = new FTDialog(RegisterLoginActivity.this);
                dialog.setTitle(R.string.register_login_without_network_title);
                dialog.setMessage(R.string.register_login_without_network_message);
                dialog.show();
            }

        }
    };

    private View.OnClickListener clickRegister = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            startActivity(new Intent(RegisterLoginActivity.this, RegisterStep1Activity.class));
            finish();
        }
    };

}
