package de.qbic.xml_edit;

import javax.swing.*;

public class NodedTextField extends JTextField {
    private XMLNode myNode;
    NodedTextField(XMLNode node) {
        super(node.getUserObject().toString());
        myNode = node;
    }
    NodedTextField() {
    }
    public XMLNode getNode() {
        return this.myNode;
    }
    public void setNode(XMLNode node) {
        this.myNode = node;
    }

}
