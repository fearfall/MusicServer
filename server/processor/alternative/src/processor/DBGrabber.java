package processor;

import model.ResultType;
import model.Track;
import model.TrackInfo;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * User: Alice Afonina
 * Email:fearfall@gmail.com
 * Date: 2/2/12
 * Time: 6:41 AM
 */
public class DBGrabber {
    private SimpleJdbcTemplate jdbcTemplate;
    private static DBGrabber dbGrabber;
    private final Properties properties;

    public static DBGrabber getInstance() {
        if(dbGrabber == null) {
            dbGrabber = new DBGrabber();
        }
        return dbGrabber;
    }
    private DBGrabber() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");//:1521
        dataSource.setUrl("jdbc:postgresql://localhost/musicbrainz_db");
        dataSource.setUsername("musicbrainz");
        dataSource.setPassword("");
        this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);
        properties = new Properties();
        try {
            properties.load(DBGrabber.class.getResourceAsStream("/processor/sql.properties"));
            
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    Map<String, Set<String>> getPuidsFromBD(Item artist) {
        final Map<String, Set<String>> result = new HashMap<String, Set<String>>();
        String statement = properties.getProperty("select.track_ids");
        try {
            jdbcTemplate.getJdbcOperations().query(statement, new RowCallbackHandler() {
                public void processRow(ResultSet resultSet) throws SQLException {
                    String mbid = resultSet.getString(1);
                    String puid = resultSet.getString(2);
                    if(result.containsKey(mbid)) {
                        result.get(mbid).add(puid);
                    } else {
                        Set<String> puids = new HashSet<String>();
                        puids.add(puid);
                        result.put(mbid, puids);
                    }
                }
            }, artist.getName());
        } catch (Exception e) {e.printStackTrace();}
        return result;
    }
   
    List<Track> getTracks(final Item artist) {
        final Map<String, Set<String>> puids = DBGrabber.getInstance().getPuidsFromBD(artist);
        final List<Track> result = new LinkedList<Track>();
        String statement = properties.getProperty("select.info");
        try {
            jdbcTemplate.getJdbcOperations().query(statement, new RowCallbackHandler() {
                public void processRow(ResultSet resultSet) throws SQLException {
                    String mbid = resultSet.getString(1);
                    String name = resultSet.getString(2);
                    int length = resultSet.getInt(3);
                    String artistMbid = resultSet.getString(4);
                    String albumMbid = resultSet.getString(5);
                    //gid, name, length, artist, release_mbid


                    //todo create generating query text module
                    String query;
                    if(artist.getName().equals("[unknown]")){
                        query = name;
                    } else {
                        query = name + " " + artist.getName();
                    }
                    result.add(new Track(mbid, name, artist.getMbid(), albumMbid, artist.getName(), puids.get(mbid), query, length));
                }
            }, artist.getName().toLowerCase());
        } catch (Exception e) {e.printStackTrace();}
        return result;

    }

    public void saveTrack(Track track, TrackInfo finalTrackInfo, boolean questionable) {
        String statement = properties.getProperty("insert.success");
        try {
            //(aid, mbid, quality, url, questionable)
            jdbcTemplate.getJdbcOperations().update(statement, finalTrackInfo.getAid(),
                                            UUID.fromString(track.getMbid()),
                                            0,
                                            finalTrackInfo.getUrl(),
                                            questionable ? 1:0);
            finish(track, (questionable)?ResultType.S_Q : ResultType.S_OK);
            /*if(questionable) {
                //puid, mbid, url
                statement = properties.getProperty("insert.questionable");
                jdbcTemplate.getJdbcOperations().update(statement, finalTrackInfo.getPuid(),
                                                                    track.getMbid(),
                                                                   finalTrackInfo.getUrl());
            }*/

        } catch (Exception e) {e.printStackTrace();}

    }
    
    public void saveError(Track track, String message, ResultType type) {
        //artist, name, mbid, error
        String statement = properties.getProperty("insert.unfinished");
        try {
            jdbcTemplate.getJdbcOperations().update(statement, track.getArtistName(),
                    track.getName(),
                    UUID.fromString(track.getMbid()),
                    message);
        } catch (Exception e) {e.printStackTrace();}
        finish(track, type);
    }

    public void finish(Track track, ResultType type) {
        String statement = properties.getProperty("insert.proceeded");
        try {
            //mbid, res_time, res_type
            jdbcTemplate.getJdbcOperations().update(statement, UUID.fromString(track.getMbid()),
                    track.getTime(), type.toString());
            System.out.println("FINISHED WITH RESULT " + type.toString());
        } catch (Exception e) {e.printStackTrace();}
    }

    public List<String> getArtistMbids(final String puid) {
        final List<String> result = new LinkedList<String>();
        String statement = properties.getProperty("select.proceeded");
        try {
           jdbcTemplate.getJdbcOperations().query(statement, new RowCallbackHandler() {
                        public void processRow(ResultSet resultSet) throws SQLException {
                            result.add(resultSet.getString(1));
                        }
                    },   puid);
        } catch (Exception e) {e.printStackTrace();}
        return result;
    }
}

