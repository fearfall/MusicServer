package finder.model;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 05/02/12
 * Time: 21:04
 * To change this template use File | Settings | File Templates.
 */
public class Counter {
    private int value;
    public Counter(int i) {
        value = i;
    }
    
    public void increase() {
        value++;
    }
    
    public int getValue() {
        return value;
    }
}
