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

    public Principal authenticate(UserRealm realm,
            String pathInContext,
            Request request,
            Response response) throws IOException {
        // Get the user if we can
        Principal user=null;
        String credentials = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (credentials!=null )
        {
            try
            {
                if(Log.isDebugEnabled())Log.debug("Credentials: "+credentials);
                credentials = credentials.substring(credentials.indexOf(' ')+1);
                //credentials = B64Code.decode(credentials, StringUtil.__UTF8);
                int i = credentials.indexOf(':');
                String username = credentials.substring(0,i);
                String password = credentials.substring(i+1);
                user = realm.authenticate(username,password,request);

                if (user==null)
                {
                    Log.warn("AUTH FAILURE: user {}",StringUtil.printable(username));
                }
                else
                {
                    request.setAuthType(Constraint.__BASIC_AUTH);
                    request.setUserPrincipal(user);
                }
            }
            catch (Exception e)
            {
                Log.warn("AUTH FAILURE: "+e.toString());
                Log.ignore(e);
            }
        }

        // Challenge if we have no user
        if (user==null && response!=null)
            sendChallenge(realm,response);

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
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "basic realm=\""+realm.getName()+'"');
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
