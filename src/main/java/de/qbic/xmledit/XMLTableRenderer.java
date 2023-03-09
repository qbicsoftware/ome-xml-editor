package de.qbic.xmledit;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class XMLTableRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        // Get the component from the super class
        Component c = super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, column);
        // Get the model from the table
        XMLTableModel model = (XMLTableModel) table.getModel();
        // Get the row color from the model
        Color color = model.getRowColor(row);
        // Set the background color of the component
        c.setBackground(color);
        // Return the component
        return c;
    }
}
