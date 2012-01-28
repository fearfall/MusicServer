package ru.musicserver.androidclient.model;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 11/2/11
 * Time: 12:31 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Model {
    protected String name;
    //TODO: Что такое shift? -- это сдвиг относительно левого края экрана для визуального разделения сущностей разного
    // уровня: чем больше shift, тем глубже вложена сущность.
    protected String shift = "";
    protected String mbid;

    public abstract Model[] getContent ();

    public String toString() {
        return shift + name;
    }

    public void setShift (String shift) {
        this.shift = shift;
    }

    public String getShift () {
        return shift;
    }

    public String getMbid() {
        return mbid;
    }
}
