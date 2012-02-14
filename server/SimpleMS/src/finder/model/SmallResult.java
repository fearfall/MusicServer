package finder.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 05/02/12
 * Time: 21:01
 */
public class SmallResult<T> {
    List<T> models = new ArrayList<T>();
    int realOffset = 0;

    public SmallResult () {}
    
    public SmallResult (List<T> models, int offset) {
        this.models = models == null ? new ArrayList<T>() : models;
        realOffset = offset;
    }
    
    public boolean isEmpty() {
        return models.isEmpty();
    }
}
