package de.qbic.omeedit;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.Vector;


public class XMLTreeModel extends DefaultTreeModel {

    private XMLNode root;
    private Vector<TreeModelListener> treeModelListeners =
            new Vector<TreeModelListener>();

    protected boolean filterIsActive;
    /*
    XMLTreeModel(XMLNode root) {
        super(root);
        this.root = root;

    }
     */
    public XMLTreeModel(TreeNode root) {
        this(root, false);

    }

    public XMLTreeModel(TreeNode root, boolean asksAllowsChildren) {
        this(root, false, false);
    }

    public XMLTreeModel(TreeNode root, boolean asksAllowsChildren,
                              boolean filterIsActive) {
        super(root, asksAllowsChildren);
        this.filterIsActive = filterIsActive;
        this.root = (XMLNode) root;
    }

    public void activateFilter(boolean newValue) {
        filterIsActive = newValue;
    }

    public boolean isActivatedFilter() {
        return filterIsActive;
    }

    public Object getChild(Object parent, int index) {
        if (filterIsActive) {
            if (parent instanceof XMLNode) {
                return ((XMLNode) parent).getChildAt(index,
                        filterIsActive);
            }
        }
        return ((TreeNode) parent).getChildAt(index);
    }

    public int getChildCount(Object parent) {
        if (filterIsActive) {
            if (parent instanceof XMLNode) {
                return ((XMLNode) parent).getChildCount(filterIsActive);
            }
        }
        return ((TreeNode) parent).getChildCount();
    }
    @Override
    public Object getRoot() {
        return root;
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
