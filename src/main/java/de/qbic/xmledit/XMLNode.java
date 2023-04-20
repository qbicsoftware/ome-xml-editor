package de.qbic.xmledit;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class XMLNode extends DefaultMutableTreeNode {
    private ArrayList<String> attributes;
    private String nodeType = "element";
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
    public void setType(String type) {
        if (type.equals("element") || type.equals("attribute") || type.equals("text") || type.equals("value")) {
            this.nodeType = type;
        } else {
            System.out.println("Invalid node type");
        }
    }
    /**
     * Return all children of this node as an array of XMLNode objects.
     */
    public XMLNode[] getChildren() {
        XMLNode[] children = new XMLNode[getChildCount()];
        for (int i = 0; i < children.length; i++) {
            children[i] = (XMLNode) getChildAt(i);
        }
        return children;
    }

    /**
     * Get ID of this node.
     */
    public String getID() {
        if (this.getUserObject().equals("OME")) {
            return null;
        }
        else if (this.getType().equals("element")) {
            for (XMLNode child : this.getChildren()) {
                if (child.getType().equals("attribute") && child.getUserObject().equals("ID")) {
                    return child.getFirstChild().toString();
                }
            }
        }
        else {
            throw new IllegalArgumentException("This node is not an element node and therefore has no ID.");
        }
        throw new IllegalArgumentException("This element node has no ID attribute.");
    }
}
