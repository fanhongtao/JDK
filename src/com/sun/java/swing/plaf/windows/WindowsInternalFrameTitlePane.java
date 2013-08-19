/*
 * @(#)WindowsInternalFrameTitlePane.java	1.12 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
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

    private Font titleFont;
    private int shadowOffset;
    private Color shadowColor;

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

	if (XPStyle.getXP() == null) {
	    selectedTitleGradientColor =
		    UIManager.getColor("InternalFrame.activeTitleGradient");
	    notSelectedTitleGradientColor =
		    UIManager.getColor("InternalFrame.inactiveTitleGradient");
	    Color activeBorderColor =
		    UIManager.getColor("InternalFrame.activeBorderColor");
	    setBorder(BorderFactory.createLineBorder(activeBorderColor, 1));
	}
    }


    protected void createButtons() {
	super.createButtons();
	if (XPStyle.getXP() != null) {
	    iconButton.setContentAreaFilled(false);
	    maxButton.setContentAreaFilled(false);
	    closeButton.setContentAreaFilled(false);
	}
    }

    public void paintComponent(Graphics g)  {
        paintTitleBackground(g);

	String title = frame.getTitle();
        if (title != null) {
            boolean isSelected = frame.isSelected();
            Font oldFont = g.getFont();
            Font newFont = (titleFont != null) ? titleFont : getFont();
            g.setFont(newFont);

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

	    if(WindowsGraphicsUtils.isLeftToRight(frame) ) {
		if (r.x == 0)  r.x = frame.getWidth()-frame.getInsets().right;
		    titleX = systemLabel.getX() + systemLabel.getWidth() + 2;
		    titleW = r.x - titleX - 3;
		    title = getTitle(frame.getTitle(), fm, titleW);
	    } else {
		titleX = systemLabel.getX() - 2
			 - SwingUtilities.computeStringWidth(fm,title);
	    }
	    if (shadowOffset != 0 && shadowColor != null) {
		g.setColor(shadowColor);
		g.drawString(title, titleX + shadowOffset, baseline + shadowOffset);
	    }
	    g.setColor(isSelected ? selectedTextColor : notSelectedTextColor);
            g.drawString(title, titleX, baseline);
            g.setFont(oldFont);
        }
    }

    public Dimension getPreferredSize() {
	return getMinimumSize();
    }

    public Dimension getMinimumSize() {
	Dimension d = super.getMinimumSize();

	XPStyle xp = XPStyle.getXP();
	if (xp != null) {
	    // Note: Don't know how to calculate height on XP,
	    // the captionbarheight is 25 but native caption is 30 (maximized 26)
	    d.height = xp.getInt("sysmetrics.captionbarheight", d.height);
	    if (frame.isMaximum()) {
		d.height += 1;
	    } else {
		d.height += 5;
	    }
	}
	return d;
    }

    protected void paintTitleBackground(Graphics g) {
	XPStyle xp = XPStyle.getXP();
	if (xp != null) {
	    XPStyle.Skin skin = xp.getSkin(frame.isIcon() ? "window.mincaption"
				      : (frame.isMaximum() ? "window.maxcaption"
							   : "window.caption"));

	    skin.paintSkin(g, 0,  0, getSize().width, getSize().height, frame.isSelected() ? 0 : 1);
	} else {
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
    }

    protected void assembleSystemMenu() {
        systemPopupMenu = new JPopupMenu();
        addSystemMenuItems(systemPopupMenu);
        enableActions();
        systemLabel = new JLabel(frame.getFrameIcon());
        systemLabel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
		showSystemPopupMenu(e.getComponent());
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

    protected void showSystemMenu(){
	showSystemPopupMenu(systemLabel);
    }

    private void showSystemPopupMenu(Component invoker){
	Dimension dim = new Dimension();
	Border border = frame.getBorder();
	if (border != null) {
	    dim.width += border.getBorderInsets(frame).left +
		border.getBorderInsets(frame).right;
	    dim.height += border.getBorderInsets(frame).bottom +
		border.getBorderInsets(frame).top;
	}
	if (!frame.isIcon()) {
	    systemPopupMenu.show(invoker,
                getX() - dim.width,
                getY() + getHeight() - dim.height);
	} else {
	    systemPopupMenu.show(invoker,
                getX() - dim.width,
                getY() - systemPopupMenu.getPreferredSize().height -
		     dim.height);
	}
    }

    protected PropertyChangeListener createPropertyChangeListener() {
        return new WindowsPropertyChangeHandler();
    }

    protected LayoutManager createLayout() {
        return new WindowsTitlePaneLayout();
    }

    public class WindowsTitlePaneLayout extends BasicInternalFrameTitlePane.TitlePaneLayout {
	private Insets captionMargin = null;
	private Insets contentMargin = null;
	private XPStyle xp = XPStyle.getXP();

	WindowsTitlePaneLayout() {
	    if (xp != null) {
		captionMargin = xp.getMargin("window.caption.captionmargins");
		contentMargin = xp.getMargin("window.caption.contentmargins");
	    }
	    if (captionMargin == null) {
		captionMargin = new Insets(0, 2, 0, 2);
	    }
	    if (contentMargin == null) {
		contentMargin = new Insets(0, 0, 0, 0);
	    }
	}

	private int layoutButton(JComponent button, String category,
				 int x, int y, int w, int h, boolean leftToRight) {
	    if (xp != null) {
		// Ignore offset parameters, because Windows does, and some third party
		// themes have bad values for them.
		XPStyle.Skin skin = xp.getSkin(category);
		if (skin.getImage() != null) {
		    w = skin.getWidth();
		    h = skin.getHeight();
		}
	    }
	    if (!leftToRight) {
		x -= w;
	    }
	    button.setBounds(x, y, w, h);
	    if (leftToRight) {
		x += w + 2;
	    } else {
		x -= 2;
	    }
	    return x;
	}

        public void layoutContainer(Container c) {
            boolean leftToRight = WindowsGraphicsUtils.isLeftToRight(frame);
	    int x, y;
            int w = getWidth();
            int h = getHeight();

	    // System button
	    Icon icon = frame.getFrameIcon();
	    int iconHeight = (icon != null) ? icon.getIconHeight() : 0;
	    x = (leftToRight) ? captionMargin.left : w - captionMargin.right;
	    y = (h - iconHeight) / 2 + 1;
	    layoutButton(systemLabel, "window.sysbutton", x, y, 16, 16, leftToRight);

	    // Right hand buttons
	    if (xp != null) {
		x = (leftToRight) ? w - captionMargin.right - 2 : captionMargin.left + 2;
		y = contentMargin.top + captionMargin.top;
		if (frame.isMaximum()) {
		    y += 1;
		} else {
		    y += 5;
		}
	    } else {
		x = (leftToRight) ? w - captionMargin.right : captionMargin.left;
		// The +1 is due to the way the line border is drawn and modifies
		// the look of the title pane.
		y = (h - 16) / 2 + 1;
	    }

	    if(frame.isClosable()) {
		x = layoutButton(closeButton, "window.closebutton", x, y, 16, 14, !leftToRight);
	    } 

	    if(frame.isMaximizable()) {
		x = layoutButton(maxButton, "window.maxbutton", x, y, 16, 14, !leftToRight);
	    }

	    if(frame.isIconifiable()) {
		layoutButton(iconButton, "window.minbutton", x, y, 16, 14, !leftToRight);
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
