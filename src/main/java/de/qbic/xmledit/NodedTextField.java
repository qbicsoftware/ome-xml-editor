package de.qbic.xmledit;

import javax.swing.*;

public class NodedTextField extends JTextField {
    private XMLNode myNode;
    NodedTextField(XMLNode node) {
        super(node.getUserObject().toString());
        myNode = node;
    }
    NodedTextField() {
        super();
    }
    public XMLNode getNode() {
        return this.myNode;
    }
    public void setNode(XMLNode node) {
        this.myNode = node;
    }



}
