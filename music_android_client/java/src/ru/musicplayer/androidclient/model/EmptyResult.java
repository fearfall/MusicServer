package ru.musicplayer.androidclient.model;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 11/3/11
 * Time: 12:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class EmptyResult extends Model {

    public EmptyResult (String message) {
        //super(message);
        name = message;
    }

    @Override
    public Model[] getContent() {
        return new EmptyResult[] {this};
    }
}
