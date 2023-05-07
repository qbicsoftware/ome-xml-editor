package de.qbic.xmledit;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Enumeration;

public class XMLNode extends DefaultMutableTreeNode {
    private ArrayList<String> attributes;
    private String nodeType = "element";
    protected boolean isVisible;

    public XMLNode() {
        this(null);
    }
    /*
    public XMLNode(DefaultMutableTreeNode defaultMutableTreeNode) {
        super(defaultMutableTreeNode);
        attributes = new ArrayList<>();
    }

    public XMLNode() {
        super();
        attributes = new ArrayList<>();
    }

     */
    public XMLNode(Object userObject) {
        this(userObject, true, true);
        attributes = new ArrayList<>();
    }

    public XMLNode(Object userObject, boolean allowsChildren,
                         boolean isVisible) {
        super(userObject, allowsChildren);
        this.isVisible = isVisible;
    }

    public TreeNode getChildAt(int index, boolean filterIsActive) {
        if (!filterIsActive) {
            return super.getChildAt(index);
        }
        if (children == null) {
            throw new ArrayIndexOutOfBoundsException("node has no children");
        }

        int realIndex = -1;
        int visibleIndex = -1;
        Enumeration e = children.elements();
        while (e.hasMoreElements()) {
            XMLNode node = (XMLNode) e.nextElement();
            if (node.isVisible()) {
                visibleIndex++;
            }
            realIndex++;
            if (visibleIndex == index) {
                return (TreeNode) children.elementAt(realIndex);
            }
        }

        throw new ArrayIndexOutOfBoundsException("index unmatched");
        //return (TreeNode)children.elementAt(index);
    }

    public int getChildCount(boolean filterIsActive) {
        if (!filterIsActive) {
            return super.getChildCount();
        }
        if (children == null) {
            return 0;
        }

        int count = 0;
        Enumeration e = children.elements();
        while (e.hasMoreElements()) {
            XMLNode node = (XMLNode) e.nextElement();
            if (node.isVisible()) {
                count++;
            }
        }

        return count;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public boolean isVisible() {
        return isVisible;
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
            setVisible(type.equals("element"));
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
            return null;
            // throw new IllegalArgumentException("This node is not an element node and therefore has no ID.");
        }
        return null;
        // throw new IllegalArgumentException("This element node has no ID attribute.");
    }
}
