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
    public static void main(String[] args) throws IOException {

        URL url = new URL("https://localhost:8443/playlist/?action=getall");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("authorization", "Basic "+ Base64.encode("user_name:pwd".getBytes()));
        connection.setRequestProperty("Own-Authentication-Form", "false");
        connection.setDoOutput(true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
         System.out.println(line);
        }
        in.close();
    }
}
