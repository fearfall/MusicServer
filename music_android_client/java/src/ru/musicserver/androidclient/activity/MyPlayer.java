package ru.musicserver.androidclient.activity;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 12/7/11
 * Time: 10:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyPlayer {
    public enum Mode {PLAY, PAUSE, STOP}

    private String myCurrentTrack = null;
    private String myCurrentTrackUrl = null;
    private int myCurrentPosition = 0;
    private int myMediaFileLengthInMilliseconds = 0;
    private Mode myMode;

    public MyPlayer () {
        myMode = Mode.STOP;
    }

    public MyPlayer (MyPlayer player) {
        myCurrentPosition = player.myCurrentPosition;
        myMediaFileLengthInMilliseconds = player.myMediaFileLengthInMilliseconds;
        myCurrentTrack = player.myCurrentTrack;
        myCurrentTrackUrl = player.myCurrentTrackUrl;
        myMode = player.myMode;
    }

    // save player to new object and return it
    public static MyPlayer save (MyPlayer player) {
        if (player == null)
            return null;
        return new MyPlayer(player);
    }

    //set player from this
    public void restoreFrom(MyPlayer player) {
        myCurrentPosition = player.myCurrentPosition;
        myMediaFileLengthInMilliseconds = player.myMediaFileLengthInMilliseconds;
        myCurrentTrack = player.myCurrentTrack;
        myCurrentTrackUrl = player.myCurrentTrackUrl;
        myMode = player.myMode;


     /*   player.myCurrentTrack = myCurrentTrack;
        player.myMode = myMode;
        player.myCurrentPosition = myCurrentPosition;
        player.myCurrentTrackUrl = myCurrentTrackUrl;
        player.myMediaFileLengthInMilliseconds = myMediaFileLengthInMilliseconds;      */
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
