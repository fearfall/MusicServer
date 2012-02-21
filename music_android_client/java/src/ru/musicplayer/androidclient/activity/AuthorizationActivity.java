package ru.musicplayer.androidclient.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
    private EditText myMusicIpEdit;
    private EditText myAuthIpEdit;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authorization);

        myApplication = (MusicApplication) getApplicationContext();
        myApplication.register(this);

        myUsernameEdit = (EditText) findViewById(R.id.usernameEditText);
        myPwdEdit = (EditText) findViewById(R.id.pwdEditText);
        final Button myLoginButton = (Button) findViewById(R.id.loginButton);
        
        myUsernameEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode != 66) // = Enter
                    return false;
                if (keyEvent.getAction() != 0)
                    return false;
                myPwdEdit.requestFocus();
                return true;
            }
        });

       myPwdEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode != 66) // = Enter
                    return false;
                if (keyEvent.getAction() != 0)
                    return false;
                InputMethodManager imm = (InputMethodManager)getSystemService(SearchActivity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 2);
                myLoginButton.performClick();
                return true;
            }
        });

        myMusicIpEdit = (EditText) findViewById(R.id.musicIpEditText);
        myMusicIpEdit.setEnabled(true);
        myMusicIpEdit.setText(Request.getMusicServerIp());

        myAuthIpEdit = (EditText) findViewById(R.id.authIpEditText);
        myAuthIpEdit.setEnabled(true);
        myAuthIpEdit.setText(Request.getAuthServerIp());

        myStatus = (TextView) findViewById(R.id.status);
        
        myLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = myUsernameEdit.getText().toString();
                String pwd = myPwdEdit.getText().toString();
                if (username.length() == 0) {
                    myApplication.showToast("Empty user name!");
                    myUsernameEdit.requestFocus();
                    return;
                }
                //myApplication.setCredentials(username, pwd);
                Request.init(username, pwd);
                try {
//                    String login = Request.login(username, pwd);
//                    myApplication.showToast(login);
//                    if (login.contains("success")) {

                        AllPlaylistsResult result = Request.getAllPlaylists();
                        if (result.getMyStatus() != null) {
                            myApplication.showToast(result.getMyStatus());
                        } else {
                            showLogin();
                            myApplication.setPlaylists(result);
                        }
                    //}
                } catch (IOException e) {
                    myApplication.showErrorMessage("Login & Get all playlists", e.getMessage());
                }
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
                Request.setIp(myMusicIpEdit.getText().toString(), myAuthIpEdit.getText().toString());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myApplication.remove(this);
    }
}
