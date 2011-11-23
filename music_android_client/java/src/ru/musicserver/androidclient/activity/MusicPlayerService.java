package ru.musicserver.androidclient.activity;

import android.app.Notification;
import android.app.NotificationManager;
//import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.DeadObjectException;
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
    private MediaPlayer myPlayer;
    private String myCurrentTrack = null;

	private NotificationManager myNotificationManager;
	//private static final int NOTIFY_ID = R.layout.main_search;
    private static int ourNotifyId;
    private enum Mode {PLAY, PAUSE, STOP};

	@Override
    public void onCreate() {
		super.onCreate();
        myPlayer = new MediaPlayer();
        myPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		myNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //private Notification.Builder myNotificationBuilder;
        ourNotifyId = R.layout.main_search;
	}

    @Override
    public void onDestroy() {
		myPlayer.stop();
		myPlayer.release();
		myNotificationManager.cancel(ourNotifyId);
	}

    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void notifyActivity (Mode mode, String message) {
        int icon;
        String tickerText;
        switch (mode) {
            case PLAY:
                icon = R.drawable.playbackstart;
                tickerText = "Playing ";
                break;
            case PAUSE:
                icon = R.drawable.playbackpause;
                tickerText = "Paused ";
                break;
            case STOP:
                icon = R.drawable.playbackpause;
                tickerText = "Stopped.";
                break;
            default:
                throw new RuntimeException("Wrong player mode!");
        }

        tickerText += message;
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, tickerText, when);

        Context context = getApplicationContext();
        CharSequence contentTitle = "Music Player";
        Intent notificationIntent = new Intent (this, MusicPlayerService.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        notification.setLatestEventInfo(context, contentTitle, tickerText, contentIntent);

        myNotificationManager.notify(ourNotifyId, notification);
    }

    private final MusicPlayerServiceInterface.Stub mBinder = new MusicPlayerServiceInterface.Stub() {



        @Override
		public boolean play(String trackName, String trackUrl, String trackId) throws DeadObjectException {
			try {
                notifyActivity(Mode.PLAY, trackName);
                if (trackUrl == null)
                    throw new IOException();

			    myPlayer.reset();

			    //myPlayer.setDataSource("http://mp3type.ru/download.php?id=31312&ass=britney_spears_-_criminal_(original_radio_edit).mp3");
                myPlayer.setDataSource(trackUrl);
			    myPlayer.prepare();
			    myPlayer.start();
                myCurrentTrack = trackId;

			    myPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer arg0) {
                        myPlayer.stop();
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
			myPlayer.pause();
		}

        @Override
		public void stop() throws DeadObjectException {
			myNotificationManager.cancel(ourNotifyId);
			myPlayer.stop();
		}

        @Override
        public String getPlayingTrackId () {
            return myCurrentTrack;
        }

        @Override
        public boolean isPlaying(String trackMbid) throws RemoteException {
            return myCurrentTrack != null && myCurrentTrack.equals(trackMbid);
        }
    };
}
