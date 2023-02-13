package de.qbic.xml_edit;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.Vector;

public class XMLTreeModel implements TreeModel {

    private XmlNode root;
    private Vector<TreeModelListener> treeModelListeners =
            new Vector<TreeModelListener>();

    XMLTreeModel(XmlNode root) {
        this.root = root;

    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        XmlNode n = (XmlNode) parent;
        return n.getChildAt(index);
    }

    @Override
    public int getChildCount(Object parent) {
        XmlNode n = (XmlNode) parent;
        return n.getChildCount();
    }

    @Override
    public boolean isLeaf(Object node) {
        XmlNode n = (XmlNode) node;
        return n.isLeaf();
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        System.out.println("*** valueForPathChanged : "
                + path + " --> " + newValue);
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        XmlNode p = (XmlNode) parent;
        XmlNode c = (XmlNode) parent;
        return p.getIndex(c);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        treeModelListeners.addElement(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        treeModelListeners.removeElement(l);
    }
}
