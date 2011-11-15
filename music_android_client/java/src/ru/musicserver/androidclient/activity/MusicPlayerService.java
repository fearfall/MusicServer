package ru.musicserver.androidclient.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.util.Log;

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
	private static final int NOTIFY_ID = R.layout.main_search;

	@Override
    public void onCreate() {
		super.onCreate();
        myPlayer = new MediaPlayer();
		myNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

    @Override
    public void onDestroy() {
		myPlayer.stop();
		myPlayer.release();
		myNotificationManager.cancel(NOTIFY_ID);
	}

	public IBinder getBinder() {
		return mBinder;
	}

    public IBinder onBind(Intent intent) {
        return null;
    }

    private final MusicPlayerServiceInterface.Stub mBinder = new MusicPlayerServiceInterface.Stub() {
        @Override
		public void play(String trackName, String trackUrl) throws DeadObjectException {
			try {
                Notification notification = new Notification(R.drawable.playbackstart, trackName, 0);
			    myNotificationManager.notify(NOTIFY_ID, notification);

			    myPlayer.reset();

			    myPlayer.setDataSource("/home/kate/au/test.mp3");
			    myPlayer.prepare();
			    myPlayer.start();
                myCurrentTrack = trackUrl;

			    myPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer arg0) {
                        myPlayer.stop();
                    }
                });

            } catch (IOException e) {
                Log.e(getString(R.string.app_name), e.getMessage());
            }
		}

        @Override
        public void pause() throws DeadObjectException {
			Notification notification = new Notification(R.drawable.playbackpause, "pause", 0);
			myNotificationManager.notify(NOTIFY_ID, notification);
			myPlayer.pause();
		}

        @Override
		public void stop() throws DeadObjectException {
			myNotificationManager.cancel(NOTIFY_ID);
			myPlayer.stop();
		}

        @Override
        public String getPlayingTrackUrl () {
            return myCurrentTrack;
        }
	};
}
