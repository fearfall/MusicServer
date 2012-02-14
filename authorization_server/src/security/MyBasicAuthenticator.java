package security;

import java.io.IOException;
import java.security.Principal;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.HttpHeaders;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Response;
import org.mortbay.jetty.security.Authenticator;
import org.mortbay.jetty.security.B64Code;
import org.mortbay.jetty.security.Constraint;
import org.mortbay.jetty.security.UserRealm;
import org.mortbay.log.Log;
import org.mortbay.util.StringUtil;

/**
* Created by IntelliJ IDEA.
* User: lana
* Date: 2/9/12
* Time: 9:15 AM
* To change this template use File | Settings | File Templates.
*/
public class MyBasicAuthenticator implements Authenticator {
    private static String CREDENTIALS = "credentials";

    public Principal authenticate(UserRealm realm,
            String pathInContext,
            Request request,
            Response response) throws IOException {

        Principal user=null;
        //first check if it is OPTIONS request method
        if (request.getMethod().toUpperCase().equals("OPTIONS")) {
            String accessControlHeaders = request.getHeader("Access-Control-Request-Headers");
            if (accessControlHeaders != null && accessControlHeaders.toLowerCase().contains("authorization")) {
                response.setHeader("Access-Control-Allow-Headers", accessControlHeaders);
                response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                response.setHeader("Access-Control-Max-Age", "172800");
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Connection", "Keep-Alive");
                response.setHeader("Keep-Alive", "timeout=2, max=100");
                response.setHeader("Content-Type", "text/plain");
                response.setHeader("Vary", "Accept-Encoding");
                response.setHeader("Content-Encoding", "gzip");
                response.setStatus(HttpServletResponse.SC_OK);
            }

        } else {
            // Get the user if we can
            String credentials = request.getParameter(CREDENTIALS);

            if (credentials!=null )
            {
                try
                {
                    credentials = credentials.substring(credentials.indexOf(' ')+1);
                    credentials = B64Code.decode(credentials, StringUtil.__ISO_8859_1);
                    int i = credentials.indexOf(':');
                    String username = credentials.substring(0,i);
                    String password = credentials.substring(i+1);
                    user = realm.authenticate(username,password,request);

                    if (user==null)
                    {
                        Log.warn("AUTH FAILURE: user {}",StringUtil.printable(username));
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "AUTH FAILURE: user " + username);
                        return null;
                    }
                    else
                    {
                        request.setAuthType(Constraint.__BASIC_AUTH);
                        request.setUserPrincipal(user);
                        response.setHeader(
                                "Set-Cookie",
                                "credentials="+request.getParameter(CREDENTIALS)+"; path=/; ");
                    }
                }
                catch (Exception e)
                {
                    Log.warn("AUTH FAILURE: "+e.toString());
                    Log.ignore(e);
                }
            }
            // Challenge if we have no user
            if (user==null && response!=null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
        return user;
    }

    public String getAuthMethod()
    {
        return Constraint.__BASIC_AUTH;
    }

    /* ------------------------------------------------------------ */
    public void sendChallenge(UserRealm realm,Response response)
        throws IOException
    {
        response.getWriter().println(
                "Error: please send 'credentials' parameter with encoded with Base64 scheme for value login:pwd\n");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}