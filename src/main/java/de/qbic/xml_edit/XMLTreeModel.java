package de.qbic.xml_edit;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.Vector;

public class XMLTreeModel implements TreeModel {

    private XMLNode root;
    private Vector<TreeModelListener> treeModelListeners =
            new Vector<TreeModelListener>();

    XMLTreeModel(XMLNode root) {
        this.root = root;

    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        XMLNode n = (XMLNode) parent;
        return n.getChildAt(index);
    }

    @Override
    public int getChildCount(Object parent) {
        XMLNode n = (XMLNode) parent;
        return n.getChildCount();
    }

    @Override
    public boolean isLeaf(Object node) {
        XMLNode n = (XMLNode) node;
        return n.isLeaf();
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        System.out.println("*** valueForPathChanged : "
                + path + " --> " + newValue);
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        XMLNode p = (XMLNode) parent;
        XMLNode c = (XMLNode) parent;
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
