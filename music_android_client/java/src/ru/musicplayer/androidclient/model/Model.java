package ru.musicplayer.androidclient.model;

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
    protected boolean wasFilled = false;

   /* public Model (String name, String mbid) {
        this.name = name;
        this.mbid = mbid;
    }
    
    public Model (String name) {
        this.name = name;
        this.mbid = null;
    }*/
    
    public abstract Model[] getContent ();

    public void fillWith (Model model) {
        if (getClass() != model.getClass())
            throw new RuntimeException("Cant't fill " + getClass().getSimpleName() + " with " + model.getClass().getSimpleName());
        if (model.getName() != null && !model.getName().equals(""))
            name = model.getName();
        if (model.getMbid() != null && !model.getMbid().equals(""))
            mbid = model.getMbid();
        wasFilled = true;
    }
    
    public boolean needsUpdate() {
        return !wasFilled;
    }

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
    
    public String getName() {
        return name;
    }
}
