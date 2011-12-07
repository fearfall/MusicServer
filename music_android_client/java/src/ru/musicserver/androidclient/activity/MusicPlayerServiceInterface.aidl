package ru.musicserver.androidclient.activity;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 11/2/11
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
interface MusicPlayerServiceInterface {
	void pause();
	void stop();
	void resume(String trackName);
	boolean play(String trackName, String trackUrl, String trackId);
	String getPlayingTrackId();
	boolean isPlaying(String trackMbid);
	boolean isPlayingMode();
	void seekTo(int playPositionInMilliseconds);
	int getCurrentPlayPosition();


	}
