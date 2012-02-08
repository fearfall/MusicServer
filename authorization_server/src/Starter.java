import handlers.PlaylistHandler;
import handlers.RegisterHandler;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.security.*;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.HashSessionIdManager;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: lana
 * Date: 12/1/11
 * Time: 10:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class Starter {

    public static void main(String[] args) throws Exception, IOException, IllegalAccessException, InstantiationException, InterruptedException {
        Server server = new Server();

//        Connector connector = new SelectChannelConnector();
//        connector.setPort(6009);
//        server.setConnectors(new Connector[] {connector});

        SslSocketConnector sslConnector = new SslSocketConnector();
        sslConnector.setPort(8443);
        sslConnector.setKeystore("config/jetty-ssl.keystore");
        sslConnector.setKeyPassword("jetty6");
        sslConnector.setPassword("jetty6");
        sslConnector.setProtocol("SSL");
        sslConnector.setNeedClientAuth(false);
        sslConnector.setWantClientAuth(true);
        server.addConnector(sslConnector);

        Constraint constraint = new Constraint();
        constraint.setName(Constraint.__BASIC_AUTH);
        constraint.setRoles(new String[] {"authUser"});
        constraint.setAuthenticate(true);

        ConstraintMapping playlistConstraint = new ConstraintMapping();
        playlistConstraint.setConstraint(constraint);
        playlistConstraint.setPathSpec("/playlist/*");

        SecurityHandler securityHandler = new SecurityHandler();
        securityHandler.setUserRealm(new JDBCUserRealm("authUserRealm", "config/realm.properties"));
        securityHandler.setConstraintMappings(new ConstraintMapping[] {playlistConstraint});

        PlaylistHandler playlistHandler = new PlaylistHandler();
        ContextHandler playlistContextHandler = new ContextHandler();
        playlistContextHandler.setContextPath("/playlist");
        playlistContextHandler.setResourceBase(".");
        playlistContextHandler.addHandler(playlistHandler);

        RegisterHandler registerHandler = new RegisterHandler();
        ContextHandler registerContextHandler = new ContextHandler("/register");
        registerContextHandler.setResourceBase(".");
        registerContextHandler.addHandler(registerHandler);

        HandlerCollection handlers = new HandlerCollection();
        handlers.setHandlers(new Handler[] {
                securityHandler,
                playlistContextHandler,
                registerContextHandler
        });

        server.setHandler(handlers);
        server.setSessionIdManager(new HashSessionIdManager());
        server.start();
        server.join();
    }
}
