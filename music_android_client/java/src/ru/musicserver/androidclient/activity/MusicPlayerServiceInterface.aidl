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
	void play(String trackName, String trackUrl);
	String getPlayingTrackUrl ();
}
