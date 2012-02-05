package ru.musicplayer.androidclient.model;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 11/2/11
 * Time: 12:31 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Model {
    protected String myName;
    //TODO: Что такое shift? -- это сдвиг относительно левого края экрана для визуального разделения сущностей разного
    // уровня: чем больше shift, тем глубже вложена сущность.
    protected String shift = "";
    protected String myMbid;
    protected boolean wasFilled = false;

    public Model (String name, String mbid) {
        myName = name;
        myMbid = mbid;
    }
    
    public Model (String name) {
        myName = name;
        myMbid = null;
    }
    
    public abstract Model[] getContent ();

    public void fillWith (Model model) {
        if (getClass() != model.getClass())
            throw new RuntimeException("Cant't fill " + getClass().getSimpleName() + " with " + model.getClass().getSimpleName());
        myName = model.getName();
        myMbid = model.getMbid();
        wasFilled = true;
    }
    
    public boolean needsUpdate() {
        return !wasFilled;
    }

    public String toString() {
        return shift + myName;
    }

    public void setShift (String shift) {
        this.shift = shift;
    }

    public String getShift () {
        return shift;
    }

    public String getMbid() {
        return myMbid;
    }
    
    public String getName() {
        return myName;
    }
}
