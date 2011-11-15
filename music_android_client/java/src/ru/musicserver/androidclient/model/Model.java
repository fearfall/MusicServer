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
    protected String shift = "";

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
}
