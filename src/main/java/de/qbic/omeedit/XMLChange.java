package de.qbic.omeedit;

import javax.swing.tree.TreeNode;
import java.io.Serializable;
import java.util.LinkedList;

public class XMLChange implements Serializable {
    private static final long serialVersionUID = 1L;
    private final LinkedList<String> location = new LinkedList<>();
    private final LinkedList<XMLNode> nodePath = new LinkedList<>();
    private XMLNode toBeChangedNode;
    private String changeType;
    private String ID;
    public String nodeType = "element";
    private boolean valid;
    private String validationError = null;

    XMLChange(String mod, XMLNode newN) {
        System.out.println("- - - - Creating new XMLChange - - - -");
        setChangeType(mod);
        setToBeChangedNode(newN);
        if (newN.getType().equals("element") && changeType.equals("add")) {
            nodePath.removeLast();
        }
        setLocation(toBeChangedNode.getPath());
    }
    public void setID(String id){
        this.ID = id;
    }
    public String getID(){
        return this.ID;
    }
    public void setToBeChangedNode(XMLNode newN){
        this.toBeChangedNode = newN;
        System.out.println("To be changed node: " + toBeChangedNode.getUserObject());
        setNodeType(toBeChangedNode.getType());
        setNodePath(toBeChangedNode.getPath());
    }

    public XMLNode getToBeChangedNode() {
        return toBeChangedNode;
    }
    public void setLocation(TreeNode[] treePath) {
        for (TreeNode n : treePath){
            location.add((n.toString()));
        }
    }

    public LinkedList<String> getLocation() {
        return location;
    }
    /**
     * Translates the Location of the change into a query, Since the node structure of the dom and
     * the XMKNode are different, the query is not the same as the location. For example an
     * attribute is not a normal node in the dom, but it is in the XMLNode structure. This
     * influences where a change needs to be applied.
     */
    public LinkedList<String> getQuery(){
        LinkedList<String> query = new LinkedList<>();
        switch (this.changeType) {
            case "add":
                query.addAll(this.location);
                break;
            case "modify":
                switch (this.getNodeType()) {
                    case "element":
                        query.addAll(this.location);
                        break;
                    case "attribute":
                    case "text":
                        query.addAll(this.location);
                        query.removeLast();
                        break;
                }
            case "delete":
                switch (this.getNodeType()) {
                    case "element":
                        query.addAll(this.location);
                        break;
                    case "attribute":
                    case "text":
                        query.addAll(this.location);
                        query.removeLast();
                        break;
                }
        }
        System.out.println("Query: " + query);
        System.out.println("Location: " + this.location);
        return query;
    }
    public String getChangeType(){
        return this.changeType;
    }
    public void setChangeType(String changeType){
        if (changeType.equals("add") || changeType.equals("delete") || changeType.equals("modify")){
            this.changeType = changeType;
            System.out.println("Change type: " + changeType);
        } else {
            System.out.println("Invalid change type");
        }
    }
    public void setNodeType(String type){
        if (type.equals("element") || type.equals("attribute") || type.equals("text") || type.equals("value")) {
            this.nodeType = type;
            System.out.println("Node type: " + type);
        } else {
            System.out.println("Invalid node type");
        }
    }
    public String getNodeType(){
        return this.nodeType;
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
    public void setNodePath(TreeNode[] treePath){
        System.out.print("Node path:");
        for (TreeNode n : treePath){
            if (((XMLNode) n).getType().equals("element")){
                nodePath.add((XMLNode) n);
                System.out.print(" " + n);
            }
        }
        System.out.println();
    }
    public LinkedList<XMLNode> getNodePath(){
        return this.nodePath;
    }
}
