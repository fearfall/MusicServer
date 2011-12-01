package ru.musicserver.androidclient.activity;

import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TabHost;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 11/30/11
 * Time: 8:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainActivity extends TabActivity {
    public static MusicPlayerServiceInterface ourPlayer;

    private ServiceConnection myServiceConnection = new ServiceConnection() {
        @Override
		public void onServiceDisconnected(ComponentName className) {
			ourPlayer = null;
		}
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ourPlayer = MusicPlayerServiceInterface.Stub.asInterface(iBinder);

        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.bindService(new Intent(MainActivity.this, MusicPlayerService.class), myServiceConnection, Context.BIND_AUTO_CREATE);

        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, SearchActivity.class);
        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("search").setIndicator("Search",
                res.getDrawable(R.drawable.ic_tab_search)).setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, PlaylistActivity.class);
        spec = tabHost.newTabSpec("playlists").setIndicator("Playlists",
                res.getDrawable(R.drawable.ic_tab_playlists)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, PlayingActivity.class);
        spec = tabHost.newTabSpec("playing").setIndicator("Playing",
                res.getDrawable(R.drawable.ic_tab_playing)).setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
    }


}
