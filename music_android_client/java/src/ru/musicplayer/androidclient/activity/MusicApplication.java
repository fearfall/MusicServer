package ru.musicplayer.androidclient.activity;

import android.app.*;
import android.content.*;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import ru.musicplayer.androidclient.model.Model;
import ru.musicplayer.androidclient.model.Playlist;
import ru.musicplayer.androidclient.model.PlaylistIterator;
import ru.musicplayer.androidclient.model.Track;
import ru.musicplayer.androidclient.network.Request;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 23/01/12
 * Time: 14:42
 * To change this template use File | Settings | File Templates.
 */
public class MusicApplication extends Application {
    public static final int ALL = 0;
    public static final int ARTISTS = 1;
    public static final int ALBUMS = 2;
    public static final int TRACKS = 3;
    
   /* private ListView searchArtists;
    private ListView searchAlbums;
    private ListView searchTracks;  */

    private MusicPlayerServiceInterface ourPlayer;
    private SeekBar seekBarProgress;
    private TextView myTiming;
    private NotificationManager myNotificationManager;
    private TextView myBuffering;
    private ImageButton playButton;
    private TextView playingTrack;

    private Playlist ourHistory;
    private LinkedList<Playlist> myPlayLists = new LinkedList<Playlist>();
    private int currentPlaylist = -1;
    private Context myContext;

//    private String myUsername;
//    private String myPassword;
//    private boolean isAuthorized;

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

    @Override
    public void onCreate() {
        super.onCreate();
        myNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        ourHistory = new Playlist("Search history");
        currentPlaylist = 0;
        bindService(new Intent(this, MusicPlayerService.class), myServiceConnection, BIND_AUTO_CREATE);
    }

    public void initControls(Context c, View pt, View pb, View t, View b, View sbp,
                             View stopButton, View skipFwdButton, View skipBackButton) {
        myContext = c;
        playingTrack = (TextView)pt;//(TextView) findViewById(R.id.playing_track_name);
        playButton = (ImageButton) pb;//(ImageButton) findViewById(R.id.button_play);

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
                    showErrorMessage("", e.getMessage());
                }
            }
        });

        myTiming = (TextView) t; //(TextView) findViewById(R.id.timing);
        myBuffering = (TextView) b; //(TextView) findViewById(R.id.buffering);
        seekBarProgress = (SeekBar) sbp;// (SeekBar)findViewById(R.id.seekBarTestPlay);
        
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
                    showErrorMessage("Seek", e.getMessage());
                }
                //}
                return true;
            }
        });

        //final ImageButton stopButton = sb;//(ImageButton) findViewById(R.id.button_stop);
        stopButton.setEnabled(true);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (ourPlayer.isPlayingMode()) {
                        ourPlayer.stop();
                    }
                } catch (RemoteException e) {
                    showErrorMessage("Stop", e.getMessage());
                }
            }
        });

        //final ImageButton skipFwdButton = sfb;//(ImageButton) findViewById(R.id.button_fwd);
        skipFwdButton.setEnabled(true);
        skipFwdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicApplication.this.next();
            }
        });

        //final ImageButton skipBackButton = sbb;//(ImageButton) findViewById(R.id.button_back);
        skipBackButton.setEnabled(true);
        skipBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Track back;
                /*if (currentPlaylist == -1)
                    back = ourHistory.next();
                else   */
                back = PlaylistIterator.back(getCurrentPlaylist());
                if (back == null) {
                    showToast("Current playlist is empty or currently selected is the first track.");
                    return;
                }
                tryPlay(back, "Backward");
            }
        });
    }

    public void next() {
        Track next;
        /*if (currentPlaylist == -1)
  next = ourHistory.back();
else  */
        next = PlaylistIterator.next(getCurrentPlaylist());
        if (next == null) {
            showToast("Current playlist is empty or currently selected is the last track.");
            return;
        }
        tryPlay(next, "Forward");
    }

    public Playlist getCurrentPlaylist() {
        if (currentPlaylist == -1)
            return ourHistory;
        return myPlayLists.get(currentPlaylist);
    }

    public Track currentPlayingTrack () {
        return getCurrentPlaylist().getPlayingTrack();
    }

    public LinkedList<Playlist> getAllPlayLists() {
        LinkedList<Playlist> all = new LinkedList<Playlist>();
        //all.push(ourHistory);
        if (!myPlayLists.isEmpty()) {
            Collections.sort(myPlayLists);
            all.addAll(myPlayLists);
        }
        all.add(0, ourHistory);
        return all;
    }

    public Model[] getAllPlayListsArray() {
        List<Playlist> list = getAllPlayLists();
        return list.toArray(new Model[list.size()]);
    }

    public void notifyUser (String text) {
        long when = System.currentTimeMillis();
        Notification notification = new Notification(R.drawable.ic_notification, text, when);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        CharSequence contentTitle = "Music Player";
        Intent notificationIntent = new Intent(this, MusicPlayerService.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(this, contentTitle, text, contentIntent);
        myNotificationManager.notify(R.layout.main, notification);
    }

    public void setPlayerStatus (String text) {
        playingTrack.setText(text);
    }

    private void tryPlay (Track track, String action) {
        try {
            if (ourPlayer.isPlaying(track.getMbid()))
                return;
        } catch (RemoteException e) {
            showErrorMessage(action, e.getMessage());
        }

        try {
            notifyUser(getString(R.string.retrievingUrl) + track.getName() + "...");
            Track trueTrack = (Track) Request.get("track", track.getMbid());
            if (trueTrack == null) {
                notifyUser(getString(R.string.noUrl) + track.getName());
                return;
            }
            try {
                if (!ourPlayer.play(trueTrack.getName(), trueTrack.getUrl(), trueTrack.getMbid())) {
                    notifyUser(getString(R.string.deadUrl) + track.getName());
                } else {
                    ourHistory.addAndPlay(trueTrack);
                }
            } catch (RemoteException e) {
                showErrorMessage(action, e.getMessage());
            }
        } catch (IOException e) {
            showErrorMessage(track.getName(), e.getMessage());
        }
    }

    public void setProgress(float currentPosition, int length) {
        seekBarProgress.setProgress((int)((currentPosition/length)*100));
    }

    public void setTiming(int milliseconds) {
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

    public void onBufferingUpdate(int percent) {
        myBuffering.setText(Integer.toString(percent));
        seekBarProgress.setSecondaryProgress(percent);
    }

    public void showErrorMessage(String title, String text) {
        Log.e(getString(R.string.app_name), text);
        AlertDialog alertDialog = new AlertDialog.Builder(myContext).create();
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

    public void showToast (String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    public void trackClicked(Track item, boolean writeToHistory) {
        try {
            if (ourPlayer.isPlaying(item.getMbid())) {
                ourPlayer.stop();
            } else {
                Track track;
                try {
                    notifyUser(getString(R.string.retrievingUrl) + item.getName() + "...");
                    track = (Track) Request.get("track", item.getMbid());
                    if (track == null) {
                        notifyUser(getString(R.string.noUrl) + item.getName());
                        setPlayerStatus(getString(R.string.noTrackName) + item.getName());
                        return;
                    }
                } catch (IOException e) {
                    setPlayerStatus(getString(R.string.noTrackName));
                    showErrorMessage(item.getName(), e.getMessage());
                    return;
                }

                if (!ourPlayer.play(track.getName(), track.getUrl(), track.getMbid())) {
                    notifyUser(getString(R.string.deadUrl) + item.getName());
                    setPlayerStatus(getString(R.string.noTrackName));
                    //setPlayerStatus(getString(this, R.string.deadUrl) + item.getName());
                    //showErrorMessage(item.getName(), "Dead track URL :-(", this);
                } else {
                    if (writeToHistory) {
                        ourHistory.addAndPlay(track);
                    } else {
                        getCurrentPlaylist().setPlaying(item.getMbid());
                    }
                }
            }

        } catch (Exception e) {
            showErrorMessage("", e.getMessage());
        }
    }
    
    public void setPlayImage (int id) {
        playButton.setImageResource(id);
    }
    
    public void setPlayButtonEnabled (boolean enabled) {
        playButton.setEnabled(enabled);
    }
    
    public Playlist newPlaylist (String name) {
        if (name.length() == 0) {
            showToast("You have to enter the name!");
            return null;
        }
        for (Playlist p: myPlayLists) {
            if (p.getName().equals(name)) {
                showToast("You already have playlist with this name!");
                return null;
            }
        }
        try {
            showToast(Request.playlistAction("create", name));
            Playlist playlist = new Playlist(name);
            myPlayLists.push(playlist);
            return playlist;
        } catch (IOException e) {
            showErrorMessage("Add playlist", e.getMessage());
            return null;
        }
    }
    
    public void setCurrentPlaylist (int index) {
        currentPlaylist = index;
        //todo: inform PlayingActivity
        Track first = myPlayLists.get(currentPlaylist).start();
        trackClicked(first, false);
    }
        
    public Playlist getHistory() {
        return ourHistory;
    }
    
   /* public boolean isAuthorized() {
        return isAuthorized;
    }

    public void setCredentials(String username, String password) {
        myUsername = username;
        myPassword = password;
        //Request.init(myUsername, myPassword);
    }  */

}
