package dev;

// Create some sample data for the table

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
class Test {

    public static void main(String[] args) {
        // Create a custom TableCellRenderer that can display an array of objects
        class ArrayRenderer extends JLabel implements TableCellRenderer {

            // The constructor sets some properties for the label
            public ArrayRenderer() {
                setOpaque(true);
                setBorder(BorderFactory.createEtchedBorder());
                setHorizontalAlignment(SwingConstants.CENTER);
                setVerticalAlignment(SwingConstants.CENTER);
            }

            // The method that returns the component to display for the table cell
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                // Get the value as an object array
                Object[] values = (Object[]) value;

                // Create a string builder to concatenate the values
                StringBuilder sb = new StringBuilder();

                // Loop through the values and append them to the string builder
                for (int i = 0; i < values.length; i++) {
                    sb.append(values[i]);
                    // Add a comma and a space between values, except for the last one
                    if (i < values.length - 1) {
                        sb.append(", ");
                    }
                }

                // Set the text of the label to the string builder's content
                setText(sb.toString());

                // Set the background and foreground colors according to the selection state
                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                } else {
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
                }

                // Return the label as the component to display
                return this;
            }
        }
        // A custom TableCellRenderer that can display a single object
        class ObjectRenderer extends JLabel implements TableCellRenderer {

            // The constructor sets some properties for the label
            public ObjectRenderer() {
                setOpaque(true);
                setBorder(BorderFactory.createEtchedBorder());
                setHorizontalAlignment(SwingConstants.CENTER);
                setVerticalAlignment(SwingConstants.CENTER);
            }

            // The method that returns the component to display for the table cell
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                // Set the text of the label to the string representation of the value
                setText(value.toString());

                // Set the background and foreground colors according to the selection state
                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                } else {
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
                }

                // Return the label as the component to display
                return this;
            }
        }

        // Create some sample data for the table
        Object[][] data = {
                {"Alice", "Female", 25},
                {"Bob", "Male", 30},
                {"Charlie", "Male", 35},
                {"David", "Male", 40},
                {"Eve", "Female", 45}
        };

        // Create an instance of SplitCellModel with the data
        SplitCellModel model = new SplitCellModel(data);

        // Create a JTable with the model
        JTable table = new JTable(model);


        // Set the custom TableCellRenderer for the table cells
        // Set the custom TableCellRenderer for each column in the table
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(new ObjectRenderer());
        }
        // Create a JFrame to display the table
        JFrame frame = new JFrame("Split Cell Table");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JScrollPane(table)); // add a scroll pane for the table
        frame.pack(); // adjust the frame size
        frame.setVisible(true); // show the frame
    }
}
