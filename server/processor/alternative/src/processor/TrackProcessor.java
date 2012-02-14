package processor;

import model.Track;

import java.util.Calendar;
import java.util.concurrent.BlockingQueue;

/**
 * User: Alice Afonina
 * Email:fearfall@gmail.com
 * Date: 2/2/12
 * Time: 10:33 AM
 */
public class TrackProcessor implements Runnable{
    //private ConcurrentLinkedQueue<Track> queue;
    //private BlockingQueue<Track> queue;
    private Track track;
    /*public TrackProcessor(ConcurrentLinkedQueue<Track> queue) {
        this.queue = queue;
    }

    public TrackProcessor(BlockingQueue<Track> queue) {
        this.queue = queue;
    }*/

    public TrackProcessor(Track track) {
        this.track = track;
    }

    public void run() {
        track.setStart(System.currentTimeMillis());
        if(track.loadTrackInfo("317437", "76b4cf3676fbe9e076fbe9e0f376da3d08f76fb76fae9e804bf890503cbd8ca")) {
            track.saveResult();
        }
    }
}
