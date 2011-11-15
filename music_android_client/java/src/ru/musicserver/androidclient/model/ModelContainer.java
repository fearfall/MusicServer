package ru.musicserver.androidclient.model;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 11/2/11
 * Time: 6:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class ModelContainer extends Model {
    private Model[] models;

    public ModelContainer(String caption, Model[] models) {
        name = caption;
        this.models = models;
    }

    @Override
    public Model[] getContent() {
        return models;
    }

    @Override
    public String toString() {
        return name;
    }
}
