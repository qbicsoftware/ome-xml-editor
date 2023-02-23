package de.qbic.xml_edit;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class XMLNode extends DefaultMutableTreeNode {
    private ArrayList<String> attributes;
    private String nodeType;
    public XMLNode(DefaultMutableTreeNode defaultMutableTreeNode) {
        super(defaultMutableTreeNode);
         attributes = new ArrayList<>();
    }
    public XMLNode(String newNode) {
        super(newNode);
        attributes = new ArrayList<>();
    }
    public XMLNode() {
        super();
        attributes = new ArrayList<>();
    }
    public ArrayList<String> getAttributes() {
        return this.attributes ;
    }
    public void setAttributes(ArrayList<String> attr) {
        this.attributes = attr;
    }

    public void addAttributes(ArrayList<String> attr) {
        this.attributes.addAll(attr);
    }

    public void addAttributes(String attr) {
        this.attributes.add(attr);
    }

    @Override
    public XMLNode getFirstChild() {
        return (XMLNode) super.getFirstChild();
    }
    @Override
    public XMLNode getParent() {
        return (XMLNode) super.getParent();
    }
    public String getType() {
        return this.nodeType;
    }
}
