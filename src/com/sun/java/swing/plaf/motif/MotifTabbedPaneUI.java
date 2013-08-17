/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.motif;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.io.Serializable; 

/**
 * A Motif L&F implementation of TabbedPaneUI.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.44 02/06/02
 * @author Amy Fowler
 * @author Philip Milne
 */
public class MotifTabbedPaneUI extends BasicTabbedPaneUI
{

// Instance variables initialized at installation

    protected Color unselectedTabBackground;
    protected Color unselectedTabForeground;
    protected Color unselectedTabShadow;
    protected Color unselectedTabHighlight;


// UI creation

    public static ComponentUI createUI(JComponent tabbedPane) {
        return new MotifTabbedPaneUI();
    }


// UI Installation/De-installation


    protected void installDefaults() {
        super.installDefaults();

        unselectedTabBackground = UIManager.getColor("TabbedPane.unselectedTabBackground");
        unselectedTabForeground = UIManager.getColor("TabbedPane.unselectedTabForeground");
        unselectedTabShadow = UIManager.getColor("TabbedPane.unselectedTabShadow");
        unselectedTabHighlight = UIManager.getColor("TabbedPane.unselectedTabHighlight");
    }

    protected void uninstallDefaults() {
        super.uninstallDefaults();

        unselectedTabBackground = null;
        unselectedTabForeground = null;
        unselectedTabShadow = null;
        unselectedTabHighlight = null;
    }

// UI Rendering

   protected void paintContentBorderTopEdge(Graphics g, int tabPlacement,
                                            int selectedIndex, 
                                            int x, int y, int w, int h) {

        g.setColor(lightHighlight);
        if (tabPlacement != TOP || selectedIndex < 0) {
            g.drawLine(x, y, x+w-2, y);
        } else {
            Rectangle selRect = rects[selectedIndex];

            g.drawLine(x, y, selRect.x - 1, y);
            if (selRect.x + selRect.width < x + w - 2) {
                g.drawLine(selRect.x + selRect.width, y, 
                           x+w-2, y);
            } 
        }
    }

    protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement,
                                               int selectedIndex,
                                               int x, int y, int w, int h) { 
        g.setColor(shadow);
        if (tabPlacement != BOTTOM || selectedIndex < 0) {
            g.drawLine(x+1, y+h-1, x+w-1, y+h-1);
        } else {
            Rectangle selRect = rects[selectedIndex];

            g.drawLine(x+1, y+h-1, selRect.x - 1, y+h-1);
            if (selRect.x + selRect.width < x + w - 2) {
                g.drawLine(selRect.x + selRect.width, y+h-1, x+w-2, y+h-1);
            } 
        }
    }

    protected void paintContentBorderRightEdge(Graphics g, int tabPlacement,
                                               int selectedIndex,
                                               int x, int y, int w, int h) { 

        g.setColor(shadow);
        if (tabPlacement != RIGHT || selectedIndex < 0) {
            g.drawLine(x+w-1, y+1, x+w-1, y+h-1);
        } else {
            Rectangle selRect = rects[selectedIndex];

            g.drawLine(x+w-1, y+1, x+w-1, selRect.y - 1);

            if (selRect.y + selRect.height < y + h - 2 ) {
                g.drawLine(x+w-1, selRect.y + selRect.height, 
                           x+w-1, y+h-2);
            } 
        }
    }

    protected void paintTabBackground(Graphics g,
                                      int tabPlacement, int tabIndex,
                                      int x, int y, int w, int h,
		 		      boolean isSelected ) {
        g.setColor(isSelected? tabPane.getBackgroundAt(tabIndex) : unselectedTabBackground);
        switch(tabPlacement) {
          case LEFT:
              g.fillRect(x+1, y+1, w-1, h-2);
              break;
          case RIGHT:
              g.fillRect(x, y+1, w-1, h-2);
              break;
          case BOTTOM:
              g.fillRect(x+1, y, w-2, h-3);
              g.drawLine(x+2, y+h-3, x+w-3, y+h-3);
              g.drawLine(x+3, y+h-2, x+w-4, y+h-2);
              break;
          case TOP:
          default:
              g.fillRect(x+1, y+3, w-2, h-3);
              g.drawLine(x+2, y+2, x+w-3, y+2);
              g.drawLine(x+3, y+1, x+w-4, y+1);
        }

    }

    protected void paintTabBorder(Graphics g,
                                  int tabPlacement, int tabIndex,
                                  int x, int y, int w, int h,
				  boolean isSelected) { 
        g.setColor(isSelected? lightHighlight : unselectedTabHighlight);

        switch(tabPlacement) {
          case LEFT:
              g.drawLine(x, y+2, x, y+h-3);
              g.drawLine(x+1, y+1, x+1, y+2);
              g.drawLine(x+2, y, x+2, y+1);
              g.drawLine(x+3, y, x+w-1, y);
              g.setColor(isSelected? shadow : unselectedTabShadow);
              g.drawLine(x+1, y+h-3, x+1, y+h-2);
              g.drawLine(x+2, y+h-2, x+2, y+h-1);
              g.drawLine(x+3, y+h-1, x+w-1, y+h-1);
              break;
          case RIGHT:
              g.drawLine(x, y, x+w-3, y);
              g.setColor(isSelected? shadow : unselectedTabShadow);
              g.drawLine(x+w-3, y, x+w-3, y+1);
              g.drawLine(x+w-2, y+1, x+w-2, y+2);
              g.drawLine(x+w-1, y+2, x+w-1, y+h-3);
              g.drawLine(x+w-2, y+h-3, x+w-2, y+h-2);
              g.drawLine(x+w-3, y+h-2, x+w-3, y+h-1);
              g.drawLine(x, y+h-1, x+w-3, y+h-1);
              break;
          case BOTTOM:
              g.drawLine(x, y, x, y+h-3);
              g.drawLine(x+1, y+h-3, x+1, y+h-2);
              g.drawLine(x+2, y+h-2, x+2, y+h-1);
              g.setColor(isSelected? shadow : unselectedTabShadow);
              g.drawLine(x+3, y+h-1, x+w-4, y+h-1);
              g.drawLine(x+w-3, y+h-2, x+w-3, y+h-1);
              g.drawLine(x+w-2, y+h-3, x+w-2, y+h-2);
              g.drawLine(x+w-1, y, x+w-1, y+h-3);
              break;
          case TOP:
          default:
              g.drawLine(x, y+2, x, y+h-1);
              g.drawLine(x+1, y+1, x+1, y+2);
              g.drawLine(x+2, y, x+2, y+1);
              g.drawLine(x+3, y, x+w-4, y);
              g.setColor(isSelected? shadow : unselectedTabShadow);
              g.drawLine(x+w-3, y, x+w-3, y+1);
              g.drawLine(x+w-2, y+1, x+w-2, y+2);
              g.drawLine(x+w-1, y+2, x+w-1, y+h-1);
        }

    }

    protected void paintFocusIndicator(Graphics g, int tabPlacement,
                                       Rectangle[] rects, int tabIndex, 
                                       Rectangle iconRect, Rectangle textRect,
                                       boolean isSelected) {
        Rectangle tabRect = rects[tabIndex];
        if (tabPane.hasFocus() && isSelected) {
            int x, y, w, h;
	    g.setColor(focus);
            switch(tabPlacement) {
              case LEFT:
                  x = tabRect.x + 3;
                  y = tabRect.y + 3;
                  w = tabRect.width - 6;
                  h = tabRect.height - 7;
                  break;
              case RIGHT:
                  x = tabRect.x + 2;
                  y = tabRect.y + 3;
                  w = tabRect.width - 6;
                  h = tabRect.height - 7;
                  break;
              case BOTTOM:
                  x = tabRect.x + 3;
                  y = tabRect.y + 2;
                  w = tabRect.width - 7;
                  h = tabRect.height - 6;
                  break;
              case TOP:
              default:
                  x = tabRect.x + 3;
                  y = tabRect.y + 3;
                  w = tabRect.width - 7;
                  h = tabRect.height - 6;
            }
            g.drawRect(x, y, w, h);
        }
    }

    protected int getTabRunIndent(int tabPlacement, int run) {
        return run*3;
    }

    protected int getTabRunOverlay(int tabPlacement) {
        tabRunOverlay = (tabPlacement == LEFT || tabPlacement == RIGHT)?
            (int)Math.round((float)maxTabWidth * .10) :
            (int)Math.round((float)maxTabHeight * .22);
        return tabRunOverlay;
    } 
       
}
