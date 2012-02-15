package ru.musicplayer.androidclient.network;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.xerces.impl.dv.util.Base64;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 11/02/12
 * Time: 03:16
 * To change this template use File | Settings | File Templates.
 */
public class SecureHttpClient {
    private ClientConnectionManager clientConnectionManager;
    private HttpContext context;
    private HttpParams params;
    private String username;
    private String password;
    
    public SecureHttpClient(String uname, String pwd, String url) {
        username = uname;
        password = pwd;
        setup(url);
    }

    private void setup(String url) {
        SchemeRegistry schemeRegistry = new SchemeRegistry();

        // http scheme
        // schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 6006));
        // https scheme
        schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 8443));

        params = new BasicHttpParams();
        params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 1);
        params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(1));
        params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "utf8");
        HttpConnectionParams.setConnectionTimeout(params, 60000);
       // params.setParameter("credentials", "=Basic " + Base64.encode((username+':'+password).getBytes()));

//        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(new AuthScope(url, AuthScope.ANY_PORT),
//                new UsernamePasswordCredentials(username, password));

        clientConnectionManager = new ThreadSafeClientConnManager(params, schemeRegistry);

        context = new BasicHttpContext();
//        context.setAttribute("http.auth.credentials-provider", credentialsProvider);
//
//        context.setAttribute("credentials",  "=Basic " + Base64.encode((username+':'+password).getBytes()));
    }

    public HttpResponse execute (String url) throws IOException {
        HttpClient client = new DefaultHttpClient(clientConnectionManager, params);
        return client.execute(new HttpPost(url + "&credentials=" + new String(Base64.encode((username + ':' + password).getBytes()))), context);
    }
}
