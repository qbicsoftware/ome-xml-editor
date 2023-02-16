package de.qbic.xml_edit;

import java.util.LinkedList;

public class XMLChange {
    private LinkedList<String> location;
    public String oldContent;
    private String newContent;
    public String modificationType;
    private XMLNode myNode;

    XMLChange(String mod, LinkedList<String> loc) {
        modificationType = mod;
        setLocation(loc);
    }
    public void setNewContent(String newC){
        this.newContent = newC;
    }

    public String getNewContent() {
        return newContent;
    }

    public LinkedList<String> getLocation() {
        return location;
    }

    public void setLocation(LinkedList<String> location) {
        this.location = location;
    }

}
