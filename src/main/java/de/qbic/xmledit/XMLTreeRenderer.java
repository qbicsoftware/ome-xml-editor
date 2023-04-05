package de.qbic.xmledit;

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
        /*
        JTextArea textArea = new JTextArea(node.getUserObject().toString());
        JPanel component = new JPanel();
        // set Border
        component.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        component.add(textArea);
        */
        if (node.getUserObject().toString().startsWith("@")) {

            String text = String.format(SPAN_FORMAT, "green", node.getUserObject());
            text += " [" + String.format(SPAN_FORMAT, "orange", "Attribute") + "]";
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
            this.remove(this);
            String text = String.format(SPAN_FORMAT, "grey", node.getUserObject());
            text += " [" + String.format(SPAN_FORMAT, "orange", "Value") + "]";
            this.setText("<html>" + text + "</html>");
            this.setIcon(employeeIcon);

        }
        else {
            String text = String.format(SPAN_FORMAT, "blue", node.getUserObject());
            text += " [" + String.format(SPAN_FORMAT, "orange", "Element") + "]";
            this.setText("<html>" + text + "</html>");
            this.setIcon(employeeIcon);
        }
        return this;
    }
}
