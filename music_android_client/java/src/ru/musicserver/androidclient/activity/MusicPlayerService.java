package ru.musicserver.androidclient.activity;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.TextView;

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
    private String myCurrentTrack = null;
	//private NotificationManager myNotificationManager;
    //private static final int ourNotifyId = R.layout.main;
    private Mode myMode;

    private TextView myInfo;

    private enum Mode {PLAY, PAUSE, STOP}

    /*public MusicPlayerService (TextView info) {
        myInfo = info;
    }          */

	@Override
    public void onCreate() {
		super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        myMode = Mode.STOP;
		//myNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

    @Override
    public void onDestroy() {
		mediaPlayer.stop();
        myMode = Mode.STOP;
		mediaPlayer.release();
		//myNotificationManager.cancel(ourNotifyId);
	}

    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void notifyActivity (Mode mode, String message) {
        int icon;
        String tickerText;
        myMode = mode;
        switch (mode) {
            case PLAY:
                icon = R.drawable.button_pause;
                tickerText = "Playing ";
                break;
            case PAUSE:
                icon = R.drawable.button_play;
                tickerText = "Paused ";
                break;
            case STOP:
                icon = R.drawable.button_pause;
                tickerText = "Stopped.";
                break;
            default:
                throw new RuntimeException("Wrong player mode!");
        }

        tickerText += message;
        MainActivity.playingTrack.setText(tickerText);
        MainActivity.playButton.setImageResource(icon);
        //myInfo.setText(tickerText);

        /*long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, tickerText, when);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        Context context = getApplicationContext();
        CharSequence contentTitle = "Music Player";
        Intent notificationIntent = new Intent (this, MusicPlayerService.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        notification.setLatestEventInfo(context, contentTitle, tickerText, contentIntent);

        myNotificationManager.notify(ourNotifyId, notification); */

    }

    private final MusicPlayerServiceInterface.Stub mBinder = new MusicPlayerServiceInterface.Stub() {
        @Override
		public boolean play(String trackName, String trackUrl, String trackId) throws DeadObjectException {
			try {
                if (trackUrl == null)
                    throw new IOException();

                notifyActivity(Mode.PLAY, trackName);

			    mediaPlayer.reset();

			    //ourPlayer.setDataSource("http://mp3type.ru/download.php?id=31312&ass=britney_spears_-_criminal_(original_radio_edit).mp3");
                mediaPlayer.setDataSource(trackUrl);
			    mediaPlayer.prepare();
			    mediaPlayer.start();
                MainActivity.playButton.setEnabled(true);
                myCurrentTrack = trackId;

			    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer arg0) {
                        mediaPlayer.stop();
                    }
                });

            } catch (IOException e) {
                notifyActivity(Mode.STOP, "");
                return false;
            }
            return true;
		}

        @Override
        public void pause() throws DeadObjectException {
            notifyActivity(Mode.PAUSE, "");
			mediaPlayer.pause();
		}

        @Override
		public void stop() throws DeadObjectException {
			//myNotificationManager.cancel(ourNotifyId);
            notifyActivity(Mode.STOP, "");
            MainActivity.playButton.setEnabled(false);
			mediaPlayer.stop();
		}

        @Override
        public String getPlayingTrackId () {
            return myCurrentTrack;
        }

        @Override
        public boolean isPlaying(String trackMbid) throws RemoteException {
            return myMode==Mode.PLAY && myCurrentTrack != null && myCurrentTrack.equals(trackMbid);
        }

        @Override
        public boolean isPlayingMode() {
            return myMode == Mode.PLAY;
        }

        @Override
        public void resume (String trackName) {
            notifyActivity(Mode.PLAY, trackName);
            mediaPlayer.start();
        }
    };
}
