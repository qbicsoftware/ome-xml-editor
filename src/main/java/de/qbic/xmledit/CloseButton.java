package de.qbic.xmledit;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

public class CloseButton extends JPanel {
    int ICON_WIDTH = 16;
    int ICON_HEIGHT = 16;
    int ARROW_SIZE = 7;
    int PADDING = 5;
    public CloseButton(final String title, ImageIcon icon, MouseListener e) {

        Image image = icon.getImage(); // transform it
        Image newimg = image.getScaledInstance(ICON_WIDTH, ICON_HEIGHT,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        icon = new ImageIcon(newimg);  // transform it back

        JLabel ic = new JLabel(icon);

        ic.setSize(icon.getIconWidth(), icon.getIconHeight());

        JLabel text = new JLabel(title);
        text.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        text.setOpaque(false);

        ButtonTab button = new ButtonTab();
        button.addMouseListener(e);
        button.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        button.setOpaque(false);

        JPanel p = new JPanel();
        p.setSize(getWidth() - icon.getIconWidth(), icon.getIconHeight());
        p.setOpaque(false);
        p.add(text);
        p.add(button);

        add(ic);
        add(p);
    }
    public CloseButton(final String title, MouseListener e) {
        // create a buffered image with transparent background
        BufferedImage image = new BufferedImage(ICON_WIDTH, ICON_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        // get graphics object from image
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        // set anti-aliasing for smoother edges
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // set stroke width and color
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(Color.BLACK);
        // draw arrow shape using lines
        g2d.drawLine(ICON_WIDTH / 4, ICON_HEIGHT / 4, ICON_WIDTH / 4 * 3, ICON_HEIGHT / 4); // horizontal line
        g2d.drawLine(ICON_WIDTH / 4 * 3, ICON_HEIGHT / 4, ICON_WIDTH / 4 * 3 - ARROW_SIZE, ICON_HEIGHT / 4 - ARROW_SIZE); // upper diagonal line
        g2d.drawLine(ICON_WIDTH / 4 * 3, ICON_HEIGHT / 4, ICON_WIDTH / 4 * 3 - ARROW_SIZE , ICON_HEIGHT / 4 + ARROW_SIZE); // lower diagonal line
        g2d.drawLine(ICON_WIDTH / 4 , ICON_HEIGHT / 4 , ICON_WIDTH / 4 , ICON_HEIGHT - PADDING); // vertical line

        // dispose graphics object
        g2d.dispose();

        // create an ImageIcon from the buffered image
        ImageIcon icon = new ImageIcon(image);

        JLabel ic = new JLabel(icon);

        ic.setSize(icon.getIconWidth(), icon.getIconHeight());

        JLabel text = new JLabel(title);
        text.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        text.setOpaque(false);

        ButtonTab button = new ButtonTab();
        button.addMouseListener(e);
        button.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        button.setOpaque(false);

        JPanel p = new JPanel();
        p.setSize(getWidth() - icon.getIconWidth(), icon.getIconHeight());
        p.setOpaque(false);
        p.add(text);
        p.add(button);

        add(ic);
        add(p);
    }

    private class ButtonTab extends JButton {

        public ButtonTab() {
            setOpaque(false);
            setPreferredSize(new Dimension(ICON_WIDTH, ICON_HEIGHT));
            setToolTipText("Close");

            setUI(new BasicButtonUI());

            setFocusable(false);
            setBorderPainted(false);

            addMouseListener(listener);
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
            g2.setStroke(new BasicStroke(2));
            g2.setColor(new Color(126, 118, 91));

            if (getModel().isRollover()) {
                g2.setColor(Color.WHITE);
            }

            int delta = 3;
            g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
            g2.dispose();
        }
    }

    private final MouseListener listener = new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setContentAreaFilled(true);
                button.setBackground(new Color(215, 65, 35));
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setContentAreaFilled(false); //transparent
            }
        }
    };
}