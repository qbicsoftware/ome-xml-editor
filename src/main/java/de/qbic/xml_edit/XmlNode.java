package de.qbic.xml_edit;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class XmlNode extends DefaultMutableTreeNode {
    private ArrayList<String> attributes;
    public XmlNode(DefaultMutableTreeNode defaultMutableTreeNode) {
        super(defaultMutableTreeNode);
         attributes = new ArrayList<>();
    }
    public XmlNode(String newNode) {
        super(newNode);
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
}
