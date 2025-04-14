package cl.scian.contourj.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;

import javax.swing.JFrame;

public class GUIUtils {

    /**
     * Positions a JFrame more or less cleverly next a {@link Component}. Source: https://github.com/fiji/FilamentDetector/blob/ffc15d6ce062db5610a3b0e69adc2aeb0ee9835c/src/main/java/sc/fiji/filamentdetector/gui/GUIUtils.java#L70
     */
    public static void positionWindow(final JFrame gui, final Component component) {

        if (null != component) {
            // Get total size of all screens
            final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            final GraphicsDevice[] gs = ge.getScreenDevices();
            int screenWidth = 0;
            for (GraphicsDevice g : gs) {
                final DisplayMode dm = g.getDisplayMode();
                screenWidth += dm.getWidth();
            }

            final Point windowLoc = component.getLocation();
            final Dimension windowSize = component.getSize();
            final Dimension guiSize = gui.getSize();
            if (guiSize.width > windowLoc.x) {
                if (guiSize.width > screenWidth - (windowLoc.x + windowSize.width)) {
                    gui.setLocationRelativeTo(null); // give up
                } else {
                    // put it to the right
                    gui.setLocation(windowLoc.x + windowSize.width, windowLoc.y);
                }
            } else {
                // put it to the left
                gui.setLocation(windowLoc.x - guiSize.width, windowLoc.y);
            }

        } else {
            gui.setLocationRelativeTo(null);
        }
    }

}
