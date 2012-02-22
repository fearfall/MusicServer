import handlers.playlist.PlaylistHandler;
import handlers.user.LoginHandler;
import handlers.user.RegisterHandler;
import org.mortbay.jetty.*;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.security.*;
import org.mortbay.jetty.servlet.HashSessionIdManager;
import security.MyBasicAuthenticator;
import utilities.CommonDbService;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: lana
 * Date: 12/1/11
 * Time: 10:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class Starter {

    public static void main(String[] args) {
        Server server = new Server();
        SslSocketConnector sslConnector = new SslSocketConnector();
        sslConnector.setPort(8443);
        sslConnector.setKeystore("config/jetty-ssl.keystore");
        sslConnector.setKeyPassword("jetty6");
        sslConnector.setPassword("jetty6");
        sslConnector.setProtocol("SSL");
        sslConnector.setNeedClientAuth(false);
        server.addConnector(sslConnector);

        Constraint constraint = new Constraint();
        constraint.setName(Constraint.__BASIC_AUTH);
        constraint.setRoles(new String[] {"authUser"});
        constraint.setAuthenticate(true);

        ConstraintMapping playlistConstraint = new ConstraintMapping();
        playlistConstraint.setConstraint(constraint);
        playlistConstraint.setPathSpec("/playlist/*");

        ConstraintMapping loginConstraint = new ConstraintMapping();
        loginConstraint.setConstraint(constraint);
        loginConstraint.setPathSpec("/login");

        SecurityHandler securityHandler = new SecurityHandler();
        securityHandler.setAuthenticator(new MyBasicAuthenticator());
        try {
            securityHandler.setUserRealm(new JDBCUserRealm("authUserRealm", "config/realm.properties"));
            securityHandler.setConstraintMappings(new ConstraintMapping[] {
                playlistConstraint,
                loginConstraint});

            CommonDbService commonDbService = new CommonDbService();
            PlaylistHandler playlistHandler = new PlaylistHandler(commonDbService);
            ContextHandler playlistContextHandler = new ContextHandler();
            playlistContextHandler.setContextPath("/playlist");
            playlistContextHandler.setResourceBase(".");
            playlistContextHandler.addHandler(playlistHandler);

            LoginHandler loginHandler = new LoginHandler();
            ContextHandler loginContextHandler = new ContextHandler("/login");
            loginContextHandler.setResourceBase(".");
            loginContextHandler.addHandler(loginHandler);

            RegisterHandler registerHandler = new RegisterHandler(commonDbService);
            ContextHandler registerContextHandler = new ContextHandler("/register");
            registerContextHandler.setResourceBase(".");
            registerContextHandler.addHandler(registerHandler);

            HandlerCollection handlers = new HandlerCollection();
            handlers.setHandlers(new Handler[] {
                    securityHandler,
                    playlistContextHandler,
                    loginContextHandler
            });

            server.addHandler(handlers);
            server.addHandler(registerContextHandler);
            server.setSessionIdManager(new HashSessionIdManager());
            server.start();
            server.join();
        } catch (IOException e) {
            System.err.println("Fatal error: cannot open file with realm properties. Exiting...");
        } catch (ClassNotFoundException e) {
            System.err.println("Fatal error: cannot create user realm. Class is not found. Exiting...");
        } catch (InstantiationException e) {
            System.err.println("Fatal error: cannot instantiate object for user realm. Exiting...");
        } catch (IllegalAccessException e) {
            System.err.println("Fatal error: cannot set user realm. Exiting...");
        } catch (InterruptedException e) {
            System.err.println("Fatal error: connection with data base was interrupted. Alow thread to exit. Exiting...");
        } catch (SQLException e) {
            System.err.println("Fatal error: cannot create CommonDbService. Exiting...");
        } catch (Exception e) {
            System.err.println("Unknown error: exiting..");
            System.err.println(e.getMessage());
        }

    }
}
