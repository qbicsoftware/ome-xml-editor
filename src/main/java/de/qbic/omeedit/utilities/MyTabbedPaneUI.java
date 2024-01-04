package de.qbic.omeedit.utilities;

import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

public class MyTabbedPaneUI extends BasicTabbedPaneUI {
    int tabHeight = 30;
    public MyTabbedPaneUI(int tabHeight) {
        // set some initial values for the fields
        tabInsets = new Insets(0, 0, 0, 0); // change the padding of the tabs
        contentBorderInsets = new Insets(0, 0, 0, 0); // change the margin of the content area
        tabAreaInsets = new Insets(0, 0, 0, 0); // change the spacing between the tabs and the content area
        tabRunOverlay = 0; // change the overlap between the tabs in different runs
        textIconGap = 5; // change the gap between the text and the icon on a tab
        this.tabHeight = tabHeight;
        // you can also change other fields such as colors, fonts, etc.
    }

    @Override
    protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
        // paint the background of the tab area with a gray color
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, tabPane.getWidth(), calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight));
        super.paintTabArea(g, tabPlacement, selectedIndex); // paint the tabs on top of the background
    }
    @Override
    protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
        return tabHeight;
    }

    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement,
                                      int tabIndex,
                                      int x, int y, int w, int h,
                                      boolean isSelected) {
        // paint the background of a single tab with a gradient color
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp;
        if (isSelected) {
            // use a light gray color for the selected tab
            gp = new GradientPaint(x, y, Color.WHITE,
                    x + w / 2, y + h / 2, Color.WHITE);
        } else {
            // use a darker gray color for the unselected tabs
            gp = new GradientPaint(x + w / 2, y + h / 2, Color.LIGHT_GRAY,
                    x + w, y + h, Color.LIGHT_GRAY);
        }
        g2d.setPaint(gp);
        g2d.fillRoundRect(x + 1, y + 1, w - 1 , h - 1 , 10 ,10); // fill a rounded rectangle with the gradient color
    }

    @Override
    protected void paintTabBorder(Graphics g, int tabPlacement,
                                  int tabIndex,
                                  int x, int y, int w, int h,
                                  boolean isSelected) {
        // do not paint any border for the tabs
    }

    @Override
    protected void paintContentBorderTopEdge(Graphics g, int tabPlacement,
                                             int selectedIndex,
                                             int x, int y, int w, int h) {
        // do not paint any border for the content area
    }

    @Override
    protected void paintContentBorderRightEdge(Graphics g, int tabPlacement,
                                               int selectedIndex,
                                               int x, int y, int w, int h) {
        // do not paint any border for the content area
    }

    @Override
    protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement,
                                              int selectedIndex,
                                              int x, int y, int w, int h) {
        // do not paint any border for the content area
    }

    @Override
    protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement,
                                                int selectedIndex,
                                                int x, int y, int w, int h) {
        // do not paint any border for the content area
    }
}
