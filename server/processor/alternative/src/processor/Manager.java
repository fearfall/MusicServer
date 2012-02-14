package processor;

import model.Track;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.util.concurrent.*;

/**
 * User: Alice Afonina
 * Email:fearfall@gmail.com
 * Date: 2/2/12
 * Time: 7:08 AM
 */
public class Manager {
    private static Logger LOG = Logger.getLogger(Manager.class);
    public static void main(String[] args) {
        QueueAdder adder = new QueueAdder();
        Thread worker = new Thread (adder);
        worker.start();
        try {
            worker.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
