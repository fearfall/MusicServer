package ru.musicserver.androidclient.activity;

import android.app.*;
import android.content.*;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import ru.musicserver.androidclient.model.Playlist;
import ru.musicserver.androidclient.model.Track;
import ru.musicserver.androidclient.network.Request;

import java.io.IOException;
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
    private static SeekBar seekBarProgress;
    private static TextView myTiming;
    //private int mediaFileLengthInMilliseconds;
    private static NotificationManager myNotificationManager;

    //private Button streamButton;
	public static ImageButton playButton;
	private static TextView playingTrack;

    public static Playlist ourHistory;
    public static LinkedList<Playlist> ourPlayLists = new LinkedList<Playlist>();
    public static int currentPlaylist = -1;

    public static Playlist getCurrentPlaylist() {
        if (currentPlaylist == -1)
            return ourHistory;
        return ourPlayLists.get(currentPlaylist);
    }

    public static Track currentPlayingTrack () {
        return getCurrentPlaylist().getPlayingTrack();
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
        myNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        ourHistory = new Playlist("Search history", 5);
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
                    showErrorMessage("", e.getMessage(), MainActivity.this);
                }
            }
        });

        myTiming = (TextView) findViewById(R.id.timing);

        seekBarProgress = (SeekBar)findViewById(R.id.seekBarTestPlay);
		seekBarProgress.setMax(100);
		seekBarProgress.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
               // if(v.getId() == R.id.seekBarTestPlay){
                    /** Seekbar onTouch event handler. Method which seeks MediaPlayer to seekBar primary progress position*/
                try {
                    if(ourPlayer.isPlayingMode()){
                        SeekBar sb = (SeekBar)v;
                        int playPositionInMilliseconds = (ourPlayer.getCurrentPlayPosition() / 100) * sb.getProgress();
                        ourPlayer.seekTo(playPositionInMilliseconds);
                        return false;
                    }
                } catch (RemoteException e) {
                    showErrorMessage("Seek", e.getMessage(), getApplicationContext());
                }
                //}
                return true;
            }
        });

        final ImageButton stopButton = (ImageButton) findViewById(R.id.button_stop);
        stopButton.setEnabled(true);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (ourPlayer.isPlayingMode()) {
                        ourPlayer.stop();
                    }
                } catch (RemoteException e) {
                    showErrorMessage("Stop", e.getMessage(), MainActivity.this);
                }
            }
        });

        final ImageButton skipFwdButton = (ImageButton) findViewById(R.id.button_fwd);
        skipFwdButton.setEnabled(true);
        skipFwdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Track next;
                /*if (currentPlaylist == -1)
                    next = ourHistory.back();
                else  */
                    next = getCurrentPlaylist().next();
                Context context = getApplicationContext();
                if (next == null) {
                    showToast("Current playlist is empty or currently selected is the last track.", getApplicationContext());
                    return;
                }
                tryPlay(next, "Forward", context);
            }
        });

        final ImageButton skipBackButton = (ImageButton) findViewById(R.id.button_back);
        skipBackButton.setEnabled(true);
        skipBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Track back;
                /*if (currentPlaylist == -1)
                    back = ourHistory.next();
                else   */
                    back = getCurrentPlaylist().back();
                Context context = getApplicationContext();
                if (back == null) {
                    showToast("Current playlist is empty or currently selected is the first track.", getApplicationContext());
                    return;
                }
                tryPlay(back, "Backward", context);
            }
        });
    }

    public static void setPlayerStatus (String text) {
        MainActivity.playingTrack.setText(text);
    }

    private static void tryPlay (Track track, String action, Context context) {
        try {
            if (ourPlayer.isPlaying(track.getMbid()))
                return;
        } catch (RemoteException e) {
            showErrorMessage(action, e.getMessage(), context);
        }

        try {
            notifyUser(getString(context, R.string.retrievingUrl) + track.getName() + "...", context);
            Track trueTrack = (Track) Request.get("track", track.getMbid());
            if (trueTrack == null) {
                notifyUser(getString(context, R.string.noUrl) + track.getName(), context);
                return;
            }
            try {
                if (!ourPlayer.play(trueTrack.getName(), trueTrack.getUrl(), trueTrack.getMbid())) {
                    notifyUser(getString(context, R.string.deadUrl) + track.getName(), context);
                } else {

                    MainActivity.ourHistory.addAndPlay(trueTrack);
                }
            } catch (RemoteException e) {
                showErrorMessage(action, e.getMessage(), context);
            }
        } catch (IOException e) {
            showErrorMessage(track.getName(), e.getMessage(), context);
            return;
        }
    }


    public static void setProgress(float currentPosition, int length) {
        seekBarProgress.setProgress((int)((currentPosition/length)*100));
    }

    public static void setTiming(int milliseconds) {
        int minutes = milliseconds/(60000);
        int seconds = (milliseconds % 60000)/1000;
        String min = Integer.toString(minutes);
        if (min.length() < 2)
            min = '0' + min;
        String sec = Integer.toString(seconds);
        if (sec.length() < 2)
            sec  = '0' + sec;
        myTiming.setText(min + ':' + sec);
    }

	public static void onBufferingUpdate(int percent) {
		seekBarProgress.setSecondaryProgress(percent);
	}

    public static void showErrorMessage(String title, String text,  Context context) {
        Log.e(context.getString(R.string.app_name), text);
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Music player error: " + title);
        alertDialog.setMessage(text);
        alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setIcon(R.drawable.icon);
        alertDialog.show();
    }

    public static void showToast (String text, Context context) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static void notifyUser (String text, Context context) {
        long when = System.currentTimeMillis();
        Notification notification = new Notification(R.drawable.ic_notification, text, when);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        CharSequence contentTitle = "Music Player";
        Intent notificationIntent = new Intent(context, MusicPlayerService.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, contentTitle, text, contentIntent);
        myNotificationManager.notify(R.layout.main, notification);
    }

    public static LinkedList<Playlist> getAllPlayLists() {
        LinkedList<Playlist> all = new LinkedList<Playlist>();
        all.push(ourHistory);
        if (!ourPlayLists.isEmpty()) {
            Collections.sort(ourPlayLists);
            all.addAll(ourPlayLists);
        }
        return all;
    }

    public static String getString(Context context, int id) {
        return context.getResources().getString(id);
    }

    public static void trackClicked(Track item, Context context) {



        try {
            if (ourPlayer.isPlaying(item.getMbid())) {
                ourPlayer.stop();
            } else {
                Track track;
                try {
                    notifyUser(getString(context, R.string.retrievingUrl) + item.getName() + "...", context);
                    //setPlayerStatus(getString(context, R.string.retrievingUrl) + item.getName() + "...");
                    track = (Track) Request.get("track", item.getMbid());
                    if (track == null) {
                        notifyUser(getString(context, R.string.noUrl) + item.getName(), context);
                        setPlayerStatus(getString(context, R.string.noTrackName) + item.getName());
                        //setPlayerStatus(getString(context, R.string.noUrl) + item.getName());
                        //showErrorMessage(item.getName(), "The track URL couldn't be retrieved. We are sorry.", context);
                        return;
                    }
                } catch (IOException e) {
                    //showToast("The track URL couldn't be retrieved. We are sorry.", context);
                    setPlayerStatus(getString(context, R.string.noTrackName));
                    showErrorMessage(item.getName(), e.getMessage(), context);
                    return;
                }

                if (!ourPlayer.play(track.getName(), track.getUrl(), track.getMbid())) {
                    notifyUser(getString(context, R.string.deadUrl) + item.getName(), context);
                    setPlayerStatus(getString(context, R.string.noTrackName));
                    //setPlayerStatus(getString(context, R.string.deadUrl) + item.getName());
                    //showErrorMessage(item.getName(), "Dead track URL :-(", context);
                } else {
                    MainActivity.ourHistory.addAndPlay(track);
                }
            }

        } catch (Exception e) {
            showErrorMessage("", e.getMessage(), context);
        }
    }


}
