package de.qbic.xmledit;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseListener;

public class CloseButton extends JPanel {
    int ICON_WIDTH = 16;
    int ICON_HEIGHT = 16;
    public CloseButton(final String title, ImageIcon icon, MouseListener e) {

        Image image = icon.getImage(); // transform it
        Image newimg = image.getScaledInstance(ICON_WIDTH, ICON_HEIGHT,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        icon = new ImageIcon(newimg);  // transform it back

        // create a label that displays the icon
        JLabel ic = new JLabel(icon);
        ic.setSize(icon.getIconWidth(), icon.getIconHeight());
        ic.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));

        // create a label that displays the title
        JLabel text = new JLabel(title);
        text.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
        text.setOpaque(false);

        // create a button with a custom look
        ButtonTab button = new ButtonTab();
        button.setSize(ICON_WIDTH, ICON_HEIGHT);
        button.addMouseListener(e);
        button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        button.setOpaque(false);

        // create a layout for the panel
        SpringLayout layout = new SpringLayout();
        // set constraints for the layout such that the close button is always on the right
        layout.putConstraint(SpringLayout.EAST, button, 1, SpringLayout.EAST, this);
        layout.putConstraint(SpringLayout.NORTH, button, 4, SpringLayout.NORTH, this);
        //layout.putConstraint(SpringLayout.SOUTH, button, 0, SpringLayout.SOUTH, this);
        // set constrains for the layout such that the icon is always on the left
        layout.putConstraint(SpringLayout.WEST, ic, 1, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, ic, 4, SpringLayout.NORTH, this);
        // set constraints for the layout such that the text is always in the middle
        layout.putConstraint(SpringLayout.WEST, text, 8, SpringLayout.EAST, ic);
        layout.putConstraint(SpringLayout.EAST, text, 8, SpringLayout.WEST, button);
        layout.putConstraint(SpringLayout.NORTH, text, 4, SpringLayout.NORTH, this);

        // add the components to the panel
        this.setLayout(layout);
        this.add(ic);
        this.add(text);
        this.add(button);
        // add new line border
        //this.setBorder(new LineBorder(Color.BLACK, 1));

    }

    private class ButtonTab extends JButton {

        public ButtonTab() {
            setPreferredSize(new Dimension(ICON_WIDTH, ICON_HEIGHT));
            setToolTipText("Close");

            setUI(new BasicButtonUI());

            setFocusable(false);
            setBorderPainted(false);

            setRolloverEnabled(true);

        }

        @Override
        public void updateUI() {
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();

            if (getModel().isPressed()) {
                g2.translate(1, 1);
            }
            g2.setStroke(new BasicStroke(3));
            g2.setColor(new Color(126, 118, 91));

            if (getModel().isRollover()) {
                g2.setColor(Color.RED);
            }

            int delta = 3;
            g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
            g2.dispose();
        }
    }
}