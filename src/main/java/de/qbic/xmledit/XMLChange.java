package de.qbic.xmledit;

import java.util.LinkedList;

public class XMLChange {
    private LinkedList<String> location;
    public String oldContent;
    private String newValue;
    public String changeType;
    private XMLNode myNode;
    private boolean valid;

    private String validationError = null;

    XMLChange(String mod, LinkedList<String> loc) {
        setChangeType(mod);
        setLocation(loc);
    }
    XMLChange(String mod, LinkedList<String> loc, String newContent) {
        setChangeType(mod);
        setLocation(loc);
        setNewValue(newContent);
    }
    public void setNewValue(String newC){
        this.newValue = newC;
    }

    public String getNewValue() {
        return newValue;
    }

    public LinkedList<String> getLocation() {
        return location;
    }

    public void setLocation(LinkedList<String> location) {
        this.location = location;
    }
    public String getChangeType(){
        return this.changeType;
    }
    public void setChangeType(String changeType){
        this.changeType = changeType;
    }

    public void setValidity(boolean valid){
        this.valid = valid;
    }
    public boolean getValidity(){
        return this.valid;
    }
    // getValidationError() returns a string with the error message
    public String getValidationError(){
        return this.validationError;
    }
    // setValidationError() sets the error message
    public void setValidationError(String error){
        this.validationError = error;
    }

}
