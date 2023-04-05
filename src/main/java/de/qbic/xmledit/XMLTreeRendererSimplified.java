package de.qbic.xmledit;


import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class XMLTreeRendererSimplified extends DefaultTreeCellRenderer{

    private static final String SPAN_FORMAT = "<span style='color:%s;'>%s</span>";
    private final ImageIcon employeeIcon;

    public XMLTreeRendererSimplified() {

        employeeIcon = (ImageIcon) this.getIcon();
    }
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

        if (node.getUserObject().toString().startsWith("@")) {
            this.setText("");
            this.setIcon(null);
        }
        else if (node.getUserObject().toString().startsWith("#")) {
            this.setText("");
            this.setIcon(null);

        }
        else if (node.getUserObject().toString().startsWith(":")) {
            this.setText("");
            this.setIcon(null);

        }
        else {
            String text = String.format(SPAN_FORMAT, "blue", node.getUserObject());
            this.setText("<html>" + text + "</html>");
            this.setIcon(employeeIcon);
        }
        return this;
    }
}

