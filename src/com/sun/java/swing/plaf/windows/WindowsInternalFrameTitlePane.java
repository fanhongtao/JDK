/*
 * @(#)WindowsInternalFrameTitlePane.java	1.7 02/02/11
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.windows;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class WindowsInternalFrameTitlePane extends BasicInternalFrameTitlePane {
    private Color selectedTitleGradientColor;
    private Color notSelectedTitleGradientColor;
    private JPopupMenu systemPopupMenu;
    private JLabel systemLabel;

    public WindowsInternalFrameTitlePane(JInternalFrame f) {
        super(f);
    }

    protected void addSubComponents() {
        add(systemLabel);
        add(iconButton);
        add(maxButton);
        add(closeButton);
    }

    protected void installDefaults() {
        super.installDefaults();
        selectedTitleGradientColor =
                UIManager.getColor("InternalFrame.activeTitleGradient");
        notSelectedTitleGradientColor =
                UIManager.getColor("InternalFrame.inactiveTitleGradient");
        Color activeBorderColor =
                UIManager.getColor("InternalFrame.activeBorderColor");
        setBorder(BorderFactory.createLineBorder(activeBorderColor, 1));
    }

    public void paintComponent(Graphics g)  {
        paintTitleBackground(g);

        if(frame.getTitle() != null) {
            boolean isSelected = frame.isSelected();
            Font f = g.getFont();
            g.setFont(getFont());
            if(isSelected)
                g.setColor(selectedTextColor);
            else
                g.setColor(notSelectedTextColor);

            // Center text vertically.
            FontMetrics fm = g.getFontMetrics();
            int baseline = (getHeight() + fm.getAscent() - fm.getLeading() -
                    fm.getDescent()) / 2;

            int titleX;
            Rectangle r = new Rectangle(0, 0, 0, 0);
            if (frame.isIconifiable())  r = iconButton.getBounds();
            else if (frame.isMaximizable())  r = maxButton.getBounds();
            else if (frame.isClosable())  r = closeButton.getBounds();
            int titleW;
    
            String title = frame.getTitle();
            if(WindowsGraphicsUtils.isLeftToRight(frame) ) {
                if (r.x == 0)  r.x = frame.getWidth()-frame.getInsets().right;
                    titleX = systemLabel.getX() + systemLabel.getWidth() + 2;
                    titleW = r.x - titleX - 3;
                    title = getTitle(frame.getTitle(), fm, titleW);
            } else {
                titleX = systemLabel.getX() - 2
                         - SwingUtilities.computeStringWidth(fm,title);
            }
            g.drawString(title, titleX, baseline);
            g.setFont(f);
        }
    }

    protected void paintTitleBackground(Graphics g) {
        Boolean gradientsOn = (Boolean)LookAndFeel.getDesktopPropertyValue(
            "win.frame.captionGradientsOn", Boolean.valueOf(false));
        if (gradientsOn.booleanValue() && g instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D)g;
            Paint savePaint = g2.getPaint();
        
            boolean isSelected = frame.isSelected();
            int w = getWidth();

            if (isSelected) {
                GradientPaint titleGradient = new GradientPaint(0,0, 
                        selectedTitleColor,
                        (int)(w*.75),0, 
                        selectedTitleGradientColor);
                g2.setPaint(titleGradient);
            } else {
                GradientPaint titleGradient = new GradientPaint(0,0, 
                        notSelectedTitleColor,
                        (int)(w*.75),0, 
                        notSelectedTitleGradientColor);   
                g2.setPaint(titleGradient);
            }
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setPaint(savePaint);
        } else {
            super.paintTitleBackground(g);
        }
    }

    protected void assembleSystemMenu() {
        systemPopupMenu = new JPopupMenu();
        addSystemMenuItems(systemPopupMenu);
        enableActions();
        systemLabel = new JLabel(frame.getFrameIcon());
        systemLabel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Dimension dim = new Dimension();
                Border border = frame.getBorder();
                if (border != null) {
                    dim.width += border.getBorderInsets(frame).left +
                                 border.getBorderInsets(frame).right;
                    dim.height += border.getBorderInsets(frame).bottom +
                                  border.getBorderInsets(frame).top;
                }
                if (!frame.isIcon()) {
                    systemPopupMenu.show(e.getComponent(),
                        getX() - dim.width,
                        getY() + getHeight() - dim.height);
                } else {
                    systemPopupMenu.show(e.getComponent(),
                        getX() - dim.width,
                        getY() - systemPopupMenu.getPreferredSize().height -
                            dim.height);
                }
            }
        });
    }

    protected void addSystemMenuItems(JPopupMenu menu) {
        JMenuItem mi = (JMenuItem)menu.add(restoreAction);
        mi.setMnemonic('R');
        mi = (JMenuItem)menu.add(moveAction);
        mi.setMnemonic('M');
        mi = (JMenuItem)menu.add(sizeAction);
        mi.setMnemonic('S');
        mi = (JMenuItem)menu.add(iconifyAction);
        mi.setMnemonic('n');
        mi = (JMenuItem)menu.add(maximizeAction);
        mi.setMnemonic('x');
        systemPopupMenu.add(new JSeparator());
        mi = (JMenuItem)menu.add(closeAction);
        mi.setMnemonic('C');
    }

    protected PropertyChangeListener createPropertyChangeListener() {
        return new WindowsPropertyChangeHandler();
    }

    protected LayoutManager createLayout() {
        return new WindowsTitlePaneLayout();
    }

    public class WindowsTitlePaneLayout extends
            BasicInternalFrameTitlePane.TitlePaneLayout {
        public void layoutContainer(Container c) {
            boolean leftToRight = WindowsGraphicsUtils.isLeftToRight(frame);
            
            int w = getWidth();
            int h = getHeight();
            int x;

            int buttonHeight = closeButton.getIcon().getIconHeight();
            //int buttonWidth = closeButton.getIcon().getIconWidth();

            Icon icon = frame.getFrameIcon();
            int iconHeight = 0;
            if (icon != null) {
                iconHeight = icon.getIconHeight();
            }

            x = (leftToRight) ? 2 : w - 16 - 2;
            systemLabel.setBounds(x, (h - iconHeight) / 2, 16, 16);

            x = (leftToRight) ? w - 16 - 2 : 2;
            
            // The +1 is due to the way the line border is drawn and modifies
            // the look of the title pane.
            if(frame.isClosable()) {
                closeButton.setBounds(x, (h - buttonHeight) / 2 + 1, 16, 14);
                x += (leftToRight) ? -(16 + 2) : 16 + 2;
            } 
            
            if(frame.isMaximizable()) {
                maxButton.setBounds(x, (h - buttonHeight) / 2 + 1, 16, 14);
                x += (leftToRight) ? -(16 + 2) : 16 + 2;
            }
        
            if(frame.isIconifiable()) {
                iconButton.setBounds(x, (h - buttonHeight) / 2 + 1, 16, 14);
            } 
        }
    } // end WindowsTitlePaneLayout

    public class WindowsPropertyChangeHandler extends PropertyChangeHandler {
        public void propertyChange(PropertyChangeEvent evt) {
	    String prop = (String)evt.getPropertyName();

            // Update the internal frame icon for the system menu.
            if (JInternalFrame.FRAME_ICON_PROPERTY.equals(prop) &&
                    systemLabel != null) {
                systemLabel.setIcon(frame.getFrameIcon());
            }

            super.propertyChange(evt);
        }
    }
}
