package ru.musicserver.common;

import ru.musicserver.android.ui.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 10/17/11
 * Time: 12:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyProtocol {
    public static List<Item> searchRequest (String request) {
        List<Item> list = new ArrayList<Item>();
        list.add(new Artist("Rammstein", null));
        list.add(new Album ("Rosenrot", null));
        //list.add(new Track("Wo bist du", "/home/kate/au/3_semestr/android/Rammstein - Wo bist du.mp3"));
        list.add(new Track("Wo bist du", "/home/kate/au/3_semestr/android/test.mp3"));
        //list.add(new Track("Wo bist du", "android.resource://ru.musicplayer.android.ui/raw/"+"test.mp3"));
        return list;
    }
}
