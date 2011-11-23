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
        SimpleDBConnection connection;
        try {
            connection = new SimpleDBConnection();
            ContextHandler searchContext = new ContextHandler();
            searchContext.setContextPath("/search");
            searchContext.setResourceBase(".");
            searchContext.setClassLoader(Thread.currentThread().getContextClassLoader());
            server.addHandler(searchContext);
            searchContext.setHandler(new SearchHandler(connection));

            ContextHandler getContext = new ContextHandler();
            getContext.setContextPath("/get");
            getContext.setResourceBase(".");
            getContext.setClassLoader(Thread.currentThread().getContextClassLoader());
            server.addHandler(getContext);

            getContext.setHandler(new GetHandler(connection));
        } catch (Exception e) {
            ErrorHandler errorHandler = new ErrorHandler();
            server.setHandler(errorHandler);
        }
        server.start();
        server.join();
    }
}