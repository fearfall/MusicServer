package ru.musicserver.android.ui;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 10/17/11
 * Time: 12:24 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Item {
    public enum ItemType {Artist, Album, Track};
    public String toString();
    public String getUrl();
    public ItemType getType();
}
