package dev;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.image.BufferedImage;

// A custom TableCellRenderer that draws two sub-columns within one column
class SplitHeaderRenderer extends JLabel implements TableCellRenderer {

    // The width of the first sub-column
    private int firstWidth;

    // The constructor takes the width of the first sub-column as an argument
    public SplitHeaderRenderer(int firstWidth) {
        this.firstWidth = firstWidth;
        // Set some properties for the label
        setOpaque(true);
        setBorder(BorderFactory.createRaisedBevelBorder());
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
    }

    // The method that returns the component to display for the header cell
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        // Get the width and height of the header cell
        int width = table.getColumnModel().getColumn(column).getWidth();
        int height = 50;

        // Get the value as a string array
        String[] values = (String[]) value;

        // Create a buffered image to draw on
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        // Draw a line to separate the two sub-columns
        g2.setColor(Color.BLACK);
        g2.drawLine(firstWidth, 0, firstWidth, height);

        // Draw the text for the first sub-column
        g2.setColor(Color.WHITE);
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int x = (firstWidth - fm.stringWidth(values[0])) / 2;
        int y = (height - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(values[0], x, y);

        // Draw the text for the second sub-column
        x = firstWidth + (width - firstWidth - fm.stringWidth(values[1])) / 2;
        g2.drawString(values[1], x, y);

        // Dispose the graphics context
        g2.dispose();

        // Set the icon of the label to the buffered image
        setIcon(new ImageIcon(image));

        // Return the label as the component to display
        return this;
    }
}
