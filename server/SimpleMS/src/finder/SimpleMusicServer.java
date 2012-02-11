package finder;

import finder.handlers.*;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandler;

/**
 * User: Alice Afonina
 * Email:fearfall@gmail.com
 * Date: 10/18/11
 * Time: 12:45 PM
 */
public class SimpleMusicServer
{
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(6006);
        try {
            ContextHandler searchContext = new ContextHandler();
            searchContext.setContextPath("/search");
            searchContext.setResourceBase(".");
            searchContext.setClassLoader(Thread.currentThread().getContextClassLoader());
            server.addHandler(searchContext);
            searchContext.setHandler(new SearchHandler());

            ContextHandler getContext = new ContextHandler();
            getContext.setContextPath("/get");
            getContext.setResourceBase(".");
            getContext.setClassLoader(Thread.currentThread().getContextClassLoader());
            server.addHandler(getContext);
            getContext.setHandler(new GetHandler());

            ContextHandler getCountContext = new ContextHandler();
            getCountContext.setContextPath("/count");
            getCountContext.setResourceBase(".");
            getCountContext.setClassLoader(Thread.currentThread().getContextClassLoader());
            server.addHandler(getCountContext);
            getCountContext.setHandler(new GetCountHandler());

        } catch (Exception e) {
            ErrorHandler errorHandler = new ErrorHandler();
            server.setHandler(errorHandler);
        }
        server.start();
        server.join();
    }
}