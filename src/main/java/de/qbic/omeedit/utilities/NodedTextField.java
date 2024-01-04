package de.qbic.omeedit.utilities;

import de.qbic.omeedit.utilities.XMLNode;

import javax.swing.*;

public class NodedTextField extends JTextField {
    private XMLNode myNode;
    public NodedTextField(XMLNode node) {
        super(node.getUserObject().toString());
        myNode = node;
    }
    public NodedTextField() {
        super();
    }
    public XMLNode getNode() {
        return this.myNode;
    }
    public void setNode(XMLNode node) {
        this.myNode = node;
    }



}
