package ru.musicplayer.androidclient.activity;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
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
    MusicApplication myApplication;

	@Override
    public void onCreate() {
		super.onCreate();
        myApplication = (MusicApplication) getApplication();
        myPlayerData = new PlayerData();
        mediaPlayer = new MediaPlayer();

        /*mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int i) {
                MainActivity.onBufferingUpdate(i);
            }
        }); */
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer arg0) {
                mediaPlayer.stop();
                
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
                myApplication.setPlayerStatus(message);
                break;
            case PAUSE:
                icon = R.drawable.button_play;
                tickerText = "Paused ";
                break;
            case STOP:
                icon = R.drawable.button_play;
                tickerText = "Stopped.";
                myApplication.setPlayerStatus(tickerText);
                break;
            default:
                throw new RuntimeException("Wrong player mode!");
        }
        tickerText += message;
        myApplication.setPlayImage(icon);
    }

    private final MusicPlayerServiceInterface.Stub mBinder = new MusicPlayerServiceInterface.Stub() {
        private final Handler handler = new Handler();
        /** Method which updates the SeekBar primary progress by current song playing position*/
        private void primarySeekBarProgressUpdater() {
            //myApplication.setProgress((float) mediaPlayer.getCurrentPosition(), myPlayerData.getMediaFileLengthInMilliseconds());
            if (mediaPlayer.isPlaying()) {
                Runnable notification = new Runnable() {
                    public void run() {
                        myApplication.setTiming(mediaPlayer.getCurrentPosition());
                        myApplication.setProgress((float) mediaPlayer.getCurrentPosition(), myPlayerData.getMediaFileLengthInMilliseconds());
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
                myApplication.setPlayButtonEnabled(true);
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
                        myApplication.showErrorMessage("Play -> Back to played resource", "Play:\n" + e.getMessage()
                                + "\nBack to played resource: \n" + e2.getMessage());
                        return false;
                    }
                }
                myApplication.showErrorMessage("Play", e.getMessage());
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
            notifyActivity(PlayerData.Mode.STOP, "");
            myApplication.setPlayButtonEnabled(false);
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
