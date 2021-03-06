package ru.musicplayer.androidclient.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TextView;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 11/30/11
 * Time: 8:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainActivity extends TabActivity {
    private MusicApplication myApplication;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        myApplication = ((MusicApplication)getApplication());
        myApplication.register(this);

        myApplication.initControls(this, findViewById(R.id.playing_track_name), findViewById(R.id.button_play),
                findViewById(R.id.timing), findViewById(R.id.buffering), findViewById(R.id.seekBarTestPlay),
                findViewById(R.id.button_stop), findViewById(R.id.button_fwd), findViewById(R.id.button_back));

        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab



        intent = new Intent().setClass(this, SearchActivity.class);
        spec = tabHost.newTabSpec("search").setIndicator("Search", res.getDrawable(R.drawable.ic_tab_search)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, PlaylistActivity.class);
        spec = tabHost.newTabSpec("playlists").setIndicator("Playlists",res.getDrawable(R.drawable.ic_tab_playlists)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, PlayingActivity.class);
        spec = tabHost.newTabSpec("playing").setIndicator("Playing", res.getDrawable(R.drawable.ic_tab_playing)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, AuthorizationActivity.class);
        spec = tabHost.newTabSpec("authorization").setIndicator("Login").setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        myApplication.saveAll();
    }
    
    public void onChangeCurrentPlaylist(String name) {
        TextView currentPlaylist = (TextView) findViewById(R.id.currentPlaylist);
        currentPlaylist.setText(name);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();    
        myApplication.remove(this);
    }
}
