package life.qbic.xmledit;

import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class XMLTableModel extends DefaultTableModel {

    // A list of colors for each row
    private List<Color> rowColors;

    // Constructor with column names and row count
    public XMLTableModel(Object[] columnNames, int rowCount) {
        super(columnNames, rowCount);
        rowColors = new ArrayList<>(rowCount);
        // Initialize the row colors with white
        for (int i = 0; i < rowCount; i++) {
            rowColors.add(Color.WHITE);
        }
    }

    // A method to change the color of a row
    public void setRowColor(int row, Color color) {
        rowColors.set(row, color);
        // Fire a table changed event to update the view
        fireTableRowsUpdated(0, row);
    }

    // A method to get the color of a row
    public Color getRowColor(int row) {
        return rowColors.get(row);
    }

    @Override
    public void addRow(Object[] rowData) {
        super.addRow(rowData);
        // Add a default color for the new row
        rowColors.add(Color.WHITE);
    }
}
