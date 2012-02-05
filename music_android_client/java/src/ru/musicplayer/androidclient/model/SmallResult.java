package ru.musicplayer.androidclient.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 05/02/12
 * Time: 21:17
 * To change this template use File | Settings | File Templates.
 */
public class SmallResult<T> {
    List<T> models = new ArrayList<T>();
    int realOffset = 0;

    public SmallResult (List<T> models, int offset) {
        this.models = models == null ? new ArrayList<T>() : models;
        realOffset = offset;
    }
    public SmallResult () { }


    public boolean isEmpty() {
        return models.isEmpty();
    }

    public List<T> getModels() {
        return models;
    }

    public int getOffset() {
        return realOffset;
    }
}