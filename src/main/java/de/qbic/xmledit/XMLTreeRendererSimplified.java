package de.qbic.xmledit;


import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class XMLTreeRendererSimplified extends DefaultTreeCellRenderer{

    private static final String SPAN_FORMAT = "<span style='color:%s;'>%s</span>";
    private final ImageIcon employeeIcon;
    private XMLTree myTree;

    public XMLTreeRendererSimplified(XMLTree tree) {
        myTree = tree;

        employeeIcon = (ImageIcon) this.getIcon();
    }
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        XMLNode node = (XMLNode) value;
        if (node.getType().equals("attribute") || node.getType().equals("text") || node.getType().equals("value")) {
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

