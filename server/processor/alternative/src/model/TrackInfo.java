package model;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import javax.sound.midi.SysexMessage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Scanner;
import java.util.UUID;

/**
 * User: Alice Afonina
 * Email:fearfall@gmail.com
 * Date: 2/2/12
 * Time: 7:17 AM
 */
public class TrackInfo implements Comparable<TrackInfo> {
    private String url;
    private String aid;
    private int quality;
    private int length;
    private int standardLength;
    private String  puid;
    private String path;
    private static Logger LOG = Logger.getLogger(TrackInfo.class);

    public TrackInfo(String url, String aid, int length, int standardLength) {
        this.url = url;
        this.aid = aid;
        this.length = length;
        this.standardLength = standardLength;
        ConsoleAppender trackConsoleAppender = new ConsoleAppender(new PatternLayout("%d [%t] %-5p %c %x - %m%n"));
        trackConsoleAppender.setName("trackinfoConsoleAppender");
        if(LOG.getAppender("trackinfoConsoleAppender") == null)
            LOG.addAppender(trackConsoleAppender);

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getPuid() {
        return puid;
    }

    public void setPuid(String puid) {
        this.puid = puid;
    }

    public int compareTo(TrackInfo trackInfo) {
        return Integer.valueOf(Math.abs(trackInfo.getLength() - standardLength)).compareTo(Math.abs(length - standardLength));
    }

    public int retrieveUrl() {
        LOG.info("RETRIEVING URL FOR " + url);
        String dirPath = System.getProperty("user.dir") + File.separator + "tracklist";
        File dir = new File(dirPath);
        if(!dir.exists())
            dir.mkdir();

        path = dirPath + File.separator + aid + ".mp3";
        URL songUrl = null;
        try {
            songUrl = new URL(url);
            ReadableByteChannel rbc = Channels.newChannel(songUrl.openStream());
            FileOutputStream fos = new FileOutputStream(path);
            fos.getChannel().transferFrom(rbc, 0, 1 << 24);
        } catch (MalformedURLException e) {
            LOG.error("RETRIEVING URL [" + url +"] FAILED: MALFORMED URL");
            //e.printStackTrace();
        } catch (FileNotFoundException e) {
            LOG.error("RETRIEVING URL [" + url +"] FAILED: FILE NOT FOUND");
            //e.printStackTrace();
        } catch (IOException e) {
            LOG.error("RETRIEVING URL [" + url +"] FAILED: IO EXCEPTION");
            //e.printStackTrace();
        }
        //todo understand how should I get a bitrate (mp3 headers)

        return 0;
    }

    boolean loadPUID() {
        LOG.info("LOADING PUID FOR [" + url +"]");
        try {
            Process process = Runtime.getRuntime().exec(System.getProperty("user.dir") + File.separator + "resources/genpuid a7f6063296c0f1c9b75c7f511861b89b " + path);
            Scanner stream = new Scanner(process.getInputStream());
            String output = "";
            while (stream.hasNextLine()) {
                output = stream.nextLine();
            }
            if(output.contains("puid:")) {
                String[] splittedOutput = output.trim().split("puid:");
                puid = splittedOutput[splittedOutput.length - 1].trim();
                LOG.info("PUID SUCCESSFULLY LOADED FOR [" + url + "]");
            }
            new File(path).delete();
        } catch (IOException e) {
            LOG.error("LOADING PUID [" + url +"] FAILED: IO EXCEPTION");
            //e.printStackTrace();
        }
        return !"".equals(puid);
    }
}