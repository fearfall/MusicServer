import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.io.*;
import java.net.*;
import javax.net.ssl.*;

/**
 * Created by IntelliJ IDEA.
 * User: lana
 * Date: 2/9/12
 * Time: 12:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleSslConnector {
    public static void main(String[] args) throws IOException, MalformedURLException {

        //URL url = new URL("https://localhost:8443/playlist/?action=save&name=myplaylist2&entries=[{'mbid':'b2d122f9-eadb-4930-a196-8f221eeb0c66','data':'track_title2','order':2},{'mbid':'b2d122f9-eadb-4930-a196-8f221eeb0c66','data':'track_title3','order':3}]");
        URL url = new URL("https://localhost:8443/login");
        HttpsURLConnection connection = null;
        connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        DataOutputStream writer = null;
        writer = new DataOutputStream(connection.getOutputStream());
        writer.writeBytes("credentials=Basic " + Base64.encode("lana:GoodLuck".getBytes()));
        writer.flush();
        writer.close();
        BufferedReader in = null;
        in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
         System.out.println(line);
        }
        in.close();

    }
}
