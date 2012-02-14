package ru.musicplayer.androidclient.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import ru.musicplayer.androidclient.model.AllPlaylistsResult;
import ru.musicplayer.androidclient.network.Request;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 11/02/12
 * Time: 01:59
 * To change this template use File | Settings | File Templates.
 */
public class AuthorizationActivity extends Activity {
    private MusicApplication myApplication;
    private TextView myStatus;
    private EditText myUsernameEdit;
    private EditText myPwdEdit;
    private EditText myIpEdit;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authorization);

        myApplication = (MusicApplication) getApplicationContext();

        myUsernameEdit = (EditText) findViewById(R.id.usernameEditText);
        myPwdEdit = (EditText) findViewById(R.id.pwdEditText);
        myIpEdit = (EditText) findViewById(R.id.ipEditText);
        myIpEdit.setEnabled(true);

        myStatus = (TextView) findViewById(R.id.status);

        Button myLoginButton = (Button) findViewById(R.id.loginButton);
        myLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = myUsernameEdit.getText().toString();
                String pwd = myPwdEdit.getText().toString();
                //myApplication.setCredentials(username, pwd);
                Request.init(username, pwd);
                try {
                    AllPlaylistsResult result = Request.getAllPlaylists();
                    if (result.getMyStatus() != null) {
                        myApplication.showToast(result.getMyStatus());
                    } else {
                        myApplication.setPlaylists(result);

                    }
                } catch (IOException e) {
                    myApplication.showErrorMessage("Get all playlists", e.getMessage());
                }

                showLogin();
                //todo: validate
            }
        });
        Button myRegisterButton = (Button) findViewById(R.id.registerButton);
        showLogout();
        myRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = myUsernameEdit.getText().toString();
                String pwd = myPwdEdit.getText().toString();
                try {
                    String register = Request.register(username, pwd);
                    myApplication.showToast(register);
                    if (register.contains("success")) {
                        showLogin();
                        //myApplication.setCredentials(username, pwd);
                    }
                } catch (IOException e) {
                    myApplication.showErrorMessage("Register", e.getMessage());
                }
            }
        });

        Button setIp = (Button)findViewById(R.id.ipButton);
        setIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Request.setIp(myIpEdit.getText().toString());
            }
        });
    }

    private void showLogin() {
        myStatus.setText("You are ONLINE now");
        myStatus.setTextColor(Color.GREEN);
//        myLoginButton.setEnabled(false);
//        myUsernameEdit.setEnabled(false);
//        myPwdEdit.setEnabled(false);

        //myLoginButton.setText("Logout");
        //todo: set logout
    }

    private void showLogout() {
        myStatus.setText("You are OFFLINE now");
        myStatus.setTextColor(Color.RED);
//        myLoginButton.setEnabled(true);
//        myUsernameEdit.setEnabled(true);
//        myPwdEdit.setEnabled(true);
        //myLoginButton.setText("Login");
        //todo: set login
    }

}
