package ru.musicplayer.androidclient.activity;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 12/7/11
 * Time: 10:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlayerData {
    public enum Mode {PLAY, PAUSE, STOP}

    private String myCurrentTrack = null;
    private String myCurrentTrackUrl = null;
    private int myCurrentPosition = 0;
    private int myMediaFileLengthInMilliseconds = 0;
    private Mode myMode;

    public PlayerData() {
        myMode = Mode.STOP;
    }

    public PlayerData(PlayerData player) {
        myCurrentPosition = player.myCurrentPosition;
        myMediaFileLengthInMilliseconds = player.myMediaFileLengthInMilliseconds;
        myCurrentTrack = player.myCurrentTrack;
        myCurrentTrackUrl = player.myCurrentTrackUrl;
        myMode = player.myMode;
    }

    // save data to new object and return it
    public static PlayerData save (PlayerData data) {
        if (data == null)
            return null;
        return new PlayerData(data);
    }

    //set data from this
    public void restoreFrom(PlayerData data) {
        myCurrentPosition = data.myCurrentPosition;
        myMediaFileLengthInMilliseconds = data.myMediaFileLengthInMilliseconds;
        myCurrentTrack = data.myCurrentTrack;
        myCurrentTrackUrl = data.myCurrentTrackUrl;
        myMode = data.myMode;


     /*   data.myCurrentTrack = myCurrentTrack;
        data.myMode = myMode;
        data.myCurrentPosition = myCurrentPosition;
        data.myCurrentTrackUrl = myCurrentTrackUrl;
        data.myMediaFileLengthInMilliseconds = myMediaFileLengthInMilliseconds;      */
    }

    public void setMode(Mode myMode) {
        this.myMode = myMode;
    }

    public Mode getMode() {
        return myMode;
    }

    public void setMediaFileLengthInMilliseconds(int myMediaFileLengthInMilliseconds) {
        this.myMediaFileLengthInMilliseconds = myMediaFileLengthInMilliseconds;
    }

    public int getMediaFileLengthInMilliseconds() {
        return myMediaFileLengthInMilliseconds;
    }

    public void setCurrentTrackUrl(String myCurrentTrackUrl) {
        this.myCurrentTrackUrl = myCurrentTrackUrl;
    }

    public String getCurrentTrackUrl() {
        return this.myCurrentTrackUrl;
    }

    public void setCurrentTrack(String myCurrentTrack) {
        this.myCurrentTrack = myCurrentTrack;
    }

    public String getCurrentTrack() {
        return myCurrentTrack;
    }

    public boolean plays (String trackMbid) {
        return myCurrentTrack != null && myCurrentTrack.equals(trackMbid);
    }
}
