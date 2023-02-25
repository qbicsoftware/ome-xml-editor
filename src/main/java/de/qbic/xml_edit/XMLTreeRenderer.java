package de.qbic.xml_edit;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class XMLTreeRenderer extends DefaultTreeCellRenderer {
    private static final String SPAN_FORMAT = "<span style='color:%s;'>%s</span>";
    private final ImageIcon employeeIcon;

    public XMLTreeRenderer() {

        employeeIcon = (ImageIcon) this.getIcon();
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

        if (node.getUserObject().toString().startsWith("@")) {
            String text = String.format(SPAN_FORMAT, "green", node.getUserObject());
            text += " [" + String.format(SPAN_FORMAT, "orange", "Argument") + "]";
            this.setText("<html>" + text + "</html>");
            this.setIcon(employeeIcon);
        }
        else if (node.getUserObject().toString().startsWith("#")) {
            String text = String.format(SPAN_FORMAT, "red", node.getUserObject());
            text += " [" + String.format(SPAN_FORMAT, "orange", "Text") + "]";
            this.setText("<html>" + text + "</html>");
            this.setIcon(employeeIcon);

        }
        else if (node.getUserObject().toString().startsWith(":")) {
            String text = String.format(SPAN_FORMAT, "grey", node.getUserObject());
            text += " [" + String.format(SPAN_FORMAT, "orange", "Value") + "]";
            this.setText("<html>" + text + "</html>");
            this.setIcon(employeeIcon);

        }
        else {
            String text = String.format(SPAN_FORMAT, "blue", node.getUserObject());
            text += " [" + String.format(SPAN_FORMAT, "orange", "Folder") + "]";
            this.setText("<html>" + text + "</html>");
            this.setIcon(employeeIcon);
        }
        return this;
    }
}
