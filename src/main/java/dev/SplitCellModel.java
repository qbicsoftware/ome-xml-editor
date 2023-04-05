package dev;

import javax.swing.table.AbstractTableModel;
// A SplitCellModel class that can create a table with one column header but multiple cells per row
class SplitCellModel extends AbstractTableModel {

    // The data for the table
    private Object[][] data;

    // The constructor takes the data as an argument
    public SplitCellModel(Object[][] data) {
        this.data = data;
    }

    // The method that returns the number of rows in the table
    public int getRowCount() {
        return data.length;
    }

    // The method that returns the number of columns in the table
    public int getColumnCount() {
        return data[0].length; // get the number of columns from the first row
    }

    // The method that returns the value at a given cell
    public Object getValueAt(int row, int column) {
        return data[row][column]; // return the value from the data array
    }

    // The method that returns the name of a given column
    public String getColumnName(int column) {
        return "Header"; // only one column header
    }
}