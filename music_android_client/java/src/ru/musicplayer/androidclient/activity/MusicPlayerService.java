package ru.musicplayer.androidclient.activity;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 11/2/11
 * Time: 2:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class MusicPlayerService extends Service {

    private MediaPlayer mediaPlayer;

    private PlayerData myPlayerData;

    /*private String myCurrentTrack = null;
    private String myCurrentTrackUrl = null;
    private int myCurrentPosition = 0;
    private int mediaFileLengthInMilliseconds;
    private Mode myMode;*/

    //private TextView myInfo;

  //  private enum Mode {PLAY, PAUSE, STOP}


	@Override
    public void onCreate() {
		super.onCreate();
        myPlayerData = new PlayerData();

        mediaPlayer = new MediaPlayer();
        initMediaPlayer(mediaPlayer);

        /*mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int i) {
                MainActivity.onBufferingUpdate(i);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer arg0) {
                mediaPlayer.stop();
            }
        });   */
	}

    private void initMediaPlayer (MediaPlayer player) {
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        /*player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int i) {
                ((MusicApplication)getApplication()).onBufferingUpdate(i);
            }
        }); */
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer arg0) {
                arg0.stop();
            }
        });
    }

    @Override
    public void onDestroy() {
		mediaPlayer.stop();
       // myMode = Mode.STOP;
		mediaPlayer.release();
		((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(R.layout.main);
	}

    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void notifyActivity (PlayerData.Mode mode, String message) {
        int icon;
        String tickerText;
        myPlayerData.setMode(mode);
        switch (mode) {
            case PLAY:
                icon = R.drawable.button_pause;
                tickerText = "Playing ";
                ((MusicApplication)getApplication()).setPlayerStatus(message);
                break;
            case PAUSE:
                icon = R.drawable.button_play;
                tickerText = "Paused ";
                break;
            case STOP:
                icon = R.drawable.button_play;
                tickerText = "Stopped.";
                ((MusicApplication)getApplication()).setPlayerStatus(tickerText);
                break;
            default:
                throw new RuntimeException("Wrong player mode!");
        }

        tickerText += message;
        ((MusicApplication)getApplication()).setPlayImage(icon);
        //MainActivity.notifyUser(tickerText, getApplicationContext());
    }

    private final MusicPlayerServiceInterface.Stub mBinder = new MusicPlayerServiceInterface.Stub() {
        private final Handler handler = new Handler();
        /** Method which updates the SeekBar primary progress by current song playing position*/
        private void primarySeekBarProgressUpdater() {
            ((MusicApplication)getApplication()).setProgress((float) mediaPlayer.getCurrentPosition(), myPlayerData.getMediaFileLengthInMilliseconds());
            if (mediaPlayer.isPlaying()) {
                Runnable notification = new Runnable() {
                    public void run() {
                        ((MusicApplication)getApplication()).setTiming(mediaPlayer.getCurrentPosition());
                        primarySeekBarProgressUpdater();
                    }
                };
                handler.postDelayed(notification, 500);
            }
        }

        @Override
		public boolean play(String trackName, String trackUrl, String trackId) throws DeadObjectException {
            //PlayerData old = new PlayerData(myPlayerData);
            int oldPosition = -1;
			try {
                if (trackUrl == null)
                    throw new IOException("Null track(" + trackName + ") Url.");


                if (mediaPlayer.isPlaying())
                    oldPosition = mediaPlayer.getCurrentPosition();

                //MediaPlayer mp = new MediaPlayer();
                mediaPlayer.reset();
                mediaPlayer.setDataSource(trackUrl);
			    mediaPlayer.prepare();
			    mediaPlayer.start();

                notifyActivity(PlayerData.Mode.PLAY, trackName);
                myPlayerData.setMediaFileLengthInMilliseconds(mediaPlayer.getDuration());
                primarySeekBarProgressUpdater();
                ((MusicApplication)getApplication()).setPlayButtonEnabled(true);
                myPlayerData.setCurrentTrack(trackId);
                myPlayerData.setCurrentTrackUrl(trackUrl);
            } catch (IOException e) {
                //myPlayerData.restoreFrom(old);
                if (oldPosition != -1) {
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(myPlayerData.getCurrentTrackUrl());
                        mediaPlayer.prepare();
                        mediaPlayer.seekTo(oldPosition);
                        mediaPlayer.start();
                        primarySeekBarProgressUpdater();
                    } catch (IOException e2) {
                        ((MusicApplication)getApplication()).showErrorMessage("Play -> Back to played resource", "Play:\n" + e.getMessage()
                                + "\nBack to played resource: \n" + e2.getMessage());
                        return false;
                    }
                }
                ((MusicApplication)getApplication()).showErrorMessage("Play", e.getMessage());
                return false;
            }
            return true;
		}

        @Override
        public void pause() throws DeadObjectException {
            notifyActivity(PlayerData.Mode.PAUSE, "");
			mediaPlayer.pause();
		}

        @Override
		public void stop() throws DeadObjectException {
			//myNotificationManager.cancel(ourNotifyId);
            notifyActivity(PlayerData.Mode.STOP, "");
            ((MusicApplication)getApplication()).setPlayButtonEnabled(false);
			mediaPlayer.stop();
		}

        @Override
        public String getPlayingTrackId () {
            return myPlayerData.getCurrentTrack();
        }

        @Override
        public boolean isPlaying(String trackMbid) throws RemoteException {
            return mediaPlayer.isPlaying() && myPlayerData.plays(trackMbid);
        }

        @Override
        public boolean isPlayingMode() {
            return mediaPlayer.isPlaying();
        }

        @Override
        public void seekTo(int playPositionInMilliseconds) throws RemoteException {
            mediaPlayer.seekTo(playPositionInMilliseconds);
        }

        @Override
        public int getCurrentPlayPosition() throws RemoteException {
            return mediaPlayer.getCurrentPosition();
        }

        @Override
        public void resume (String trackName) {
            notifyActivity(PlayerData.Mode.PLAY, trackName);
            mediaPlayer.start();
            primarySeekBarProgressUpdater();
        }
    };
}
