package ru.musicplayer.androidclient.model;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 28/01/12
 * Time: 21:25
 * To change this template use File | Settings | File Templates.
 */
public class ResultCount {
    private int myArtistsSize;
    private int myAlbumsSize;
    private int myTracksSize;

    public ResultCount () {
        myAlbumsSize = 0;
        myArtistsSize = 0;
        myTracksSize = 0;
    }

    public ResultCount (int artistsSize, int albumsSize, int tracksSize) {
        myAlbumsSize = albumsSize;
        myArtistsSize = artistsSize;
        myTracksSize = tracksSize;
    }

    public int getArtistsSize() {
        return myArtistsSize;
    }

    public int getAlbumsSize() {
        return myAlbumsSize;
    }

    public int getTracksSize() {
        return myTracksSize;
    }
}
