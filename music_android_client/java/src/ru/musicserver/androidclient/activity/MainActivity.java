package ru.musicserver.androidclient.activity;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.*;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TextView;
import ru.musicserver.androidclient.model.Playlist;
import ru.musicserver.androidclient.model.Track;
import ru.musicserver.androidclient.network.Request;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 11/30/11
 * Time: 8:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainActivity extends TabActivity {
    public static MusicPlayerServiceInterface ourPlayer;

    //private Button streamButton;
	public static ImageButton playButton;
	public static TextView playingTrack;

    public static Playlist ourSearchHistory;
    public static LinkedList<Playlist> ourPlayLists = new LinkedList<Playlist>();
    public static int currentPlaylist = -1;

    public Track currentPlayingTrack () {
        if (currentPlaylist == -1)
            return ourSearchHistory.getPlayingTrack();
        return ourPlayLists.get(currentPlaylist).getPlayingTrack();
    }

	//private boolean isPlaying;
	//private StreamingMediaPlayer audioStreamer;


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
        ourSearchHistory = new Playlist("Search history", 5);
        initControls();

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

    private void initControls() {
    	playingTrack = (TextView) findViewById(R.id.playing_track_name);

		playButton = (ImageButton) findViewById(R.id.button_play);

		playButton.setEnabled(false);

		playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    if (ourPlayer.isPlayingMode()) {
                        ourPlayer.pause();
                        //playButton.setImageResource(R.drawable.button_play);
                    } else {
                        ourPlayer.resume(currentPlayingTrack().getName());
                        //playButton.setImageResource(R.drawable.button_pause);
                    }
                } catch (RemoteException e) {
                    showErrorMessage(e.getMessage(), MainActivity.this);
                }

            }});
    }

    public static void showErrorMessage(String text,  Context context) {
        Log.e(context.getString(R.string.app_name), text);
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Music player error");
        alertDialog.setMessage(text);
        alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setIcon(R.drawable.icon);
        alertDialog.show();
        //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    public static LinkedList<Playlist> getAllPlayLists() {
        LinkedList<Playlist> all = new LinkedList<Playlist>();
        all.push(ourSearchHistory);
        if (!ourPlayLists.isEmpty()) {
            Collections.sort(ourPlayLists);
            all.addAll(ourPlayLists);
        }
        return all;
    }

    public static void trackClicked(Track item, Context context) {
        try {
            if (ourPlayer.isPlaying(item.getMbid())) {
                ourPlayer.stop();
            } else {
                Track track;
                try {
                    track = (Track) Request.get("track", item.getMbid());
                } catch (IOException e) {
                    showErrorMessage(e.getMessage(), context);
                    return;
                }

                if (!ourPlayer.play(track.getName(), track.getUrl(), track.getMbid()))
                    showErrorMessage("Dead song URL :-(", context);
                else {
                    MainActivity.ourSearchHistory.addAndPlay(track);
                }
            }
        } catch (Exception e) {
            showErrorMessage(e.getMessage(), context);
        }
    }


}
