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

        ContextHandler context = new ContextHandler();
        context.setContextPath("/search");
        context.setResourceBase(".");
        context.setClassLoader(Thread.currentThread().getContextClassLoader());
        server.setHandler(context);
        SimpleDBConnection connection = new SimpleDBConnection();
        context.setHandler(new SearchHandler(connection));
        server.start();
        server.join();
    }
}