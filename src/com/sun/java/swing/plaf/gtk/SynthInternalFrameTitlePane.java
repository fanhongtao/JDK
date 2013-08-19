/*
 * @(#)SynthInternalFrameTitlePane.java	1.14 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.border.*;
import javax.swing.event.InternalFrameEvent;
import java.util.EventListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;
import java.beans.PropertyVetoException;

/**
 * The class that manages a basic title bar
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @version 1.14 01/23/03
 *  (originally from version 1.52 of BasicInternalFrameTitlePane)
 * @author David Kloba
 * @author Joshua Outwater
 * @author Steve Wilson
 */
class SynthInternalFrameTitlePane extends JComponent implements SynthUI {
    protected JButton minimizeButton;
    protected JButton maximizeButton;
    protected JButton closeButton;

    protected JPopupMenu systemPopupMenu;
    protected JButton menuButton;

    protected JInternalFrame frame;

    protected Icon maximizeIcon;
    protected Icon restoreIcon;
    protected Icon minimizeIcon;
    protected Icon closeIcon;

    protected PropertyChangeListener propertyChangeListener;

    protected Action closeAction;
    protected Action maximizeAction;
    protected Action iconifyAction;
    protected Action restoreAction;
    protected Action moveAction;
    protected Action sizeAction;

    // PENDING: This should be looked up as needed.
    protected static final String CLOSE_CMD =
        UIManager.getString("InternalFrameTitlePane.closeButtonText");
    protected static final String ICONIFY_CMD =
        UIManager.getString("InternalFrameTitlePane.minimizeButtonText");
    protected static final String RESTORE_CMD =
        UIManager.getString("InternalFrameTitlePane.restoreButtonText");
    protected static final String MAXIMIZE_CMD =
        UIManager.getString("InternalFrameTitlePane.maximizeButtonText");
    protected static final String MOVE_CMD =
        UIManager.getString("InternalFrameTitlePane.moveButtonText");
    protected static final String SIZE_CMD =
        UIManager.getString("InternalFrameTitlePane.sizeButtonText");

    private String closeButtonToolTip;
    private String minimizeButtonToolTip;
    private String restoreButtonToolTip;
    private String maximizeButtonToolTip;

    private SynthStyle style;

    public SynthInternalFrameTitlePane(JInternalFrame f) {
        fetchStyle(this);

	frame = f;
	installTitlePane();

	menuButton.setName("InternalFrameTitlePane.menuButton");
        minimizeButton.setName("InternalFrameTitlePane.iconifyButton");
        maximizeButton.setName("InternalFrameTitlePane.maximizeButton");
        closeButton.setName("InternalFrameTitlePane.closeButton");
    }

    public String getUIClassID() {
        return "InternalFrameTitlePaneUI";
    }

    protected void installTitlePane() {
	installDefaults();
        installListeners();
        
	createActions();
	enableActions();
	createActionMap();

	setLayout(createLayout());

	assembleSystemMenu();
	createButtons();
	addSubComponents();

    }

    public SynthContext getContext(JComponent c) {
        return getContext(c, getComponentState(c));
    }

    public SynthContext getContext(JComponent c, int state) {
        return SynthContext.getContext(SynthContext.class, c,
                    SynthLookAndFeel.getRegion(c), style, state);
    }

    private Region getRegion(JComponent c) {
        return SynthLookAndFeel.getRegion(c);
    }

    private int getComponentState(JComponent c) {
        if (frame != null) {
            if (frame.isSelected()) {
                return SELECTED;
            }
        }
        return SynthLookAndFeel.getComponentState(c);
    }

    protected void addSubComponents() {
	add(menuButton);
	add(minimizeButton);
	add(maximizeButton);
	add(closeButton);
    }

    protected void createActions() {
	maximizeAction = new MaximizeAction();
	iconifyAction = new IconifyAction();
	closeAction = new CloseAction();
	restoreAction = new RestoreAction();
	moveAction = new MoveAction();
	sizeAction = new SizeAction();
    }

    ActionMap createActionMap() {
	ActionMap map = new ActionMapUIResource();
	map.put("showSystemMenu", new ShowSystemMenuAction(true));
	map.put("hideSystemMenu", new ShowSystemMenuAction(false));
	return map;
    }

    protected void installListeners() {
        if(propertyChangeListener == null) {
            propertyChangeListener = createPropertyChangeListener();
        }
	frame.addPropertyChangeListener(propertyChangeListener);
    }

    protected void uninstallListeners() {
	frame.removePropertyChangeListener(propertyChangeListener);
    }

    private void fetchStyle(JComponent c) {
        SynthContext context = getContext(this, ENABLED);
        SynthStyle oldStyle = style;
        style = SynthLookAndFeel.updateStyle(context, this);
        if (style != oldStyle) {
            maximizeIcon = style.getIcon(context,"InternalFrameTitlePane.maximizeIcon");
            restoreIcon = style.getIcon(context,"InternalFrameTitlePane.restoreIcon");
            minimizeIcon = style.getIcon(context,"InternalFrameTitlePane.iconifyIcon");
            closeIcon = style.getIcon(context,"InternalFrameTitlePane.closeIcon");
        }
        context.dispose();
    }

    protected void installDefaults() {
        closeButtonToolTip =
                UIManager.getString("InternalFrame.closeButtonToolTip");
        minimizeButtonToolTip =
                UIManager.getString("InternalFrame.iconButtonToolTip");
        restoreButtonToolTip =
                UIManager.getString("InternalFrame.restoreButtonToolTip");
        maximizeButtonToolTip =
                UIManager.getString("InternalFrame.maxButtonToolTip");
    }


    protected void uninstallDefaults() {
        SynthContext context = getContext(this, ENABLED);

        style.uninstallDefaults(context);
        context.dispose();
        style = null;
    }

    protected void createButtons() {
	minimizeButton = createNoFocusButton();
	minimizeButton.addActionListener(iconifyAction);
        if (minimizeButtonToolTip != null && minimizeButtonToolTip.length() != 0) {
            minimizeButton.setToolTipText(minimizeButtonToolTip);
        }

	maximizeButton = createNoFocusButton();
	maximizeButton.addActionListener(maximizeAction);

	closeButton = createNoFocusButton();  
	closeButton.addActionListener(closeAction);
        if (closeButtonToolTip != null && closeButtonToolTip.length() != 0) {
            closeButton.setToolTipText(closeButtonToolTip);
        }

        setButtonIcons();
    }

    protected void setButtonIcons() {
        if(frame.isIcon()) {
	    if (restoreIcon != null) {
		minimizeButton.setIcon(restoreIcon);
	    }
            if (restoreButtonToolTip != null && restoreButtonToolTip.length() != 0) {
                minimizeButton.setToolTipText(restoreButtonToolTip);
            }
	    if (maximizeIcon != null) {
		maximizeButton.setIcon(maximizeIcon);
	    }
            if (maximizeButtonToolTip != null && maximizeButtonToolTip.length() != 0) {
                maximizeButton.setToolTipText(maximizeButtonToolTip);
            }
        } else if (frame.isMaximum()) {
	    if (minimizeIcon != null) {
		minimizeButton.setIcon(minimizeIcon);
	    }
            if (minimizeButtonToolTip != null && minimizeButtonToolTip.length() != 0) {
                minimizeButton.setToolTipText(minimizeButtonToolTip);
            }
	    if (restoreIcon != null) {
		maximizeButton.setIcon(restoreIcon);
	    }
            if (restoreButtonToolTip != null && restoreButtonToolTip.length() != 0) {
                maximizeButton.setToolTipText(restoreButtonToolTip);
            }
        } else {
	    if (minimizeIcon != null) {
		minimizeButton.setIcon(minimizeIcon);
	    }
            if (minimizeButtonToolTip != null && minimizeButtonToolTip.length() != 0) {
                minimizeButton.setToolTipText(minimizeButtonToolTip);
            }
	    if (maximizeIcon != null) {
		maximizeButton.setIcon(maximizeIcon);
	    }
            if (maximizeButtonToolTip != null && maximizeButtonToolTip.length() != 0) {
                maximizeButton.setToolTipText(maximizeButtonToolTip);
            }
        }
	if (closeIcon != null) {
	    closeButton.setIcon(closeIcon);
	}
    }

    protected void assembleSystemMenu() {
        systemPopupMenu = new JPopupMenu();
        addSystemMenuItems(systemPopupMenu);
	enableActions();
        menuButton = createNoFocusButton();
	menuButton.setIcon(frame.getFrameIcon());
        menuButton.addMouseListener(new MouseAdapter() {
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
        // PENDING: this should all be localizable!
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
	menu.add(new JSeparator());
	mi = (JMenuItem)menu.add(closeAction);
	mi.setMnemonic('C');
    }

    // SynthInternalFrameTitlePane has no UI, we'll invoke paint on it.
    public void paintComponent(Graphics g) {
        SynthContext context = getContext(this);
        SynthLookAndFeel.update(context, g);
        context.dispose();
    }

    /**
     * Post a WINDOW_CLOSING-like event to the frame, so that it can
     * be treated like a regular Frame.
     */
    protected void postClosingEvent(JInternalFrame frame) {
        InternalFrameEvent e = new InternalFrameEvent(
            frame, InternalFrameEvent.INTERNAL_FRAME_CLOSING);
        // Try posting event, unless there's a SecurityManager.
        if (JInternalFrame.class.getClassLoader() == null) {
            try {
                Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(e);
                return;
            } catch (SecurityException se) {
                // Use dispatchEvent instead.
            }
        }
        frame.dispatchEvent(e);
    }


    protected void enableActions() {
        restoreAction.setEnabled(frame.isMaximum() || frame.isIcon());
        maximizeAction.setEnabled(frame.isMaximizable() && !frame.isMaximum());
        iconifyAction.setEnabled(frame.isIconifiable() && !frame.isIcon());
        closeAction.setEnabled(frame.isClosable());
        sizeAction.setEnabled(false);
        moveAction.setEnabled(false);
    }


    protected PropertyChangeListener createPropertyChangeListener() {
        return new PropertyChangeHandler();
    }

    protected LayoutManager createLayout() {
        SynthContext context = getContext(this);
	LayoutManager lm =
	    (LayoutManager)style.get(context, "InternalFrameTitlePane.titlePaneLayout");
	context.dispose();
        return (lm != null) ? lm : new TitlePaneLayout();
    }


    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */
    class PropertyChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {

            String prop = (String)evt.getPropertyName();

            if (SynthLookAndFeel.shouldUpdateStyle(evt)) {
                fetchStyle(SynthInternalFrameTitlePane.this);
            }
            if (JInternalFrame.IS_SELECTED_PROPERTY.equals(prop)) {
                repaint();
                return;
            } 

            if (JInternalFrame.IS_ICON_PROPERTY.equals(prop) ||
                    JInternalFrame.IS_MAXIMUM_PROPERTY.equals(prop)) {
                setButtonIcons();
                enableActions();
                return;
            }

            if (prop.equals("closable")) {
                if ((Boolean)evt.getNewValue() == Boolean.TRUE) {
                    add(closeButton);
                } else {
                    remove(closeButton);
                }
            } else if (prop.equals("maximizable")) {
                if ((Boolean)evt.getNewValue() == Boolean.TRUE) {
                    add(maximizeButton);
                } else {
                    remove(maximizeButton);
                }
            } else if (prop.equals("iconable")) {
                if ((Boolean)evt.getNewValue() == Boolean.TRUE) {
                    add(minimizeButton);
                } else {
                    remove(minimizeButton);
                }
            }
            enableActions();

            revalidate();
            repaint();
        }

    }  // end PropertyHandler class

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */
    class TitlePaneLayout implements LayoutManager {
        public void addLayoutComponent(String name, Component c) {}
        public void removeLayoutComponent(Component c) {}    
        public Dimension preferredLayoutSize(Container c)  {
	    return minimumLayoutSize(c);
	}
    
        public Dimension minimumLayoutSize(Container c) {
            // Calculate width.
            int width = 22;
 
            if (frame.isClosable()) {
                width += 19;
            }
            if (frame.isMaximizable()) {
                width += 19;
            }
            if (frame.isIconifiable()) {
                width += 19;
            }

            FontMetrics fm = getFontMetrics(getFont());
            String frameTitle = frame.getTitle();
            int title_w = frameTitle != null ? fm.stringWidth(frameTitle) : 0;
            int title_length = frameTitle != null ? frameTitle.length() : 0;

            // Leave room for three characters in the title.
            if (title_length > 3) {
                int subtitle_w =
                    fm.stringWidth(frameTitle.substring(0, 3) + "...");
                width += (title_w < subtitle_w) ? title_w : subtitle_w;
            } else {
                width += title_w;
            }

            // Calculate height.
            Icon icon = frame.getFrameIcon();
            int fontHeight = fm.getHeight();
            fontHeight += 2;
            int iconHeight = 0;
            if (icon != null) {
                // SystemMenuBar forces the icon to be 16x16 or less.
                iconHeight = Math.min(icon.getIconHeight(), 16);
            }
            iconHeight += 2;
      
            int height = Math.max( fontHeight, iconHeight );

            Dimension dim = new Dimension(width, height);

            // Take into account the border insets if any.
            if (getBorder() != null) {
                Insets insets = getBorder().getBorderInsets(c);
                dim.height += insets.top + insets.bottom;
                dim.width += insets.left + insets.right;
            }
            return dim;
	}
    
        public void layoutContainer(Container c) {
            boolean leftToRight = SynthLookAndFeel.isLeftToRight(frame);

            int w = getWidth();
            int h = getHeight();
            int x;

            Icon closeIcon = closeButton.getIcon();
            int buttonHeight = (closeIcon != null) ? closeIcon.getIconHeight(): 12;
	    if (buttonHeight == 0) {
		buttonHeight = 12;
	    }
            //int buttonWidth = closeButton.getIcon().getIconWidth();

            Icon icon = frame.getFrameIcon();
            int iconHeight = (icon != null) ? icon.getIconHeight() : buttonHeight;

	    Insets insets = frame.getInsets();

            x = (leftToRight) ? insets.left : w - 16 - insets.right;
            menuButton.setBounds(x, (h - iconHeight) / 2, 16, 14);

            x = (leftToRight) ? w - 16 - insets.right : insets.left;

            if (frame.isClosable()) {
                closeButton.setBounds(x, (h - buttonHeight) / 2, 16, 14);
                x += (leftToRight) ? -(16 + 2) : 16 + 2;
            } 

            if (frame.isMaximizable()) {
                maximizeButton.setBounds(x, (h - buttonHeight) / 2, 16, 14);
                x += (leftToRight) ? -(16 + 2) : 16 + 2;
            }

            if (frame.isIconifiable()) {
                minimizeButton.setBounds(x, (h - buttonHeight) / 2, 16, 14);
            } 
        }
    } // end TitlePaneLayout

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */  
    class CloseAction extends AbstractAction {
        public CloseAction() {
            super(CLOSE_CMD);
        }

        public void actionPerformed(ActionEvent e) {
            if(frame.isClosable()) {
                frame.doDefaultCloseAction();
            }
        }      
    } // end CloseAction

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */
    class MaximizeAction extends AbstractAction {
        public MaximizeAction() {
	    super(MAXIMIZE_CMD);
        }

        public void actionPerformed(ActionEvent e) {
            if (frame.isMaximizable()) {
                if (!frame.isMaximum()) {
                    try {
                        frame.setMaximum(true);
                    } catch (PropertyVetoException e5) { }
                } else {
                    try { 
                        frame.setMaximum(false); 
                    } catch (PropertyVetoException e6) { }
                }
            }
        }
    } // MaximizeAction

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */
    class IconifyAction extends AbstractAction {
        public IconifyAction() {
	    super(ICONIFY_CMD);
        }

        public void actionPerformed(ActionEvent e) {
            if (frame.isIconifiable()) {
                if (!frame.isIcon()) {
                    try {
                        frame.setIcon(true);
                    } catch (PropertyVetoException e1) { }
                } else{
                    try {
                        frame.setIcon(false);
                    } catch (PropertyVetoException e1) { }
                }
            }
	}
    } // end IconifyAction

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */
    class RestoreAction extends AbstractAction {
        public RestoreAction() {
	    super(RESTORE_CMD);
        }

        public void actionPerformed(ActionEvent e) {
            if (frame.isMaximizable() && frame.isMaximum()) {
                try {
                    frame.setMaximum(false);
                } catch (PropertyVetoException e4) { }
            } 
            else if (frame.isIconifiable() && frame.isIcon()) {
                try {
                    frame.setIcon(false);
                } catch (PropertyVetoException e4) { }
            }
       }      
    } // end RestoreAction

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */
    class MoveAction extends AbstractAction {
        public MoveAction() {
            super(MOVE_CMD);
        }

        public void actionPerformed(ActionEvent e) {
            // This action is currently undefined
	}      
    } // end MoveAction

    /*
     * Handles showing and hiding the system menu.
     */
    private class ShowSystemMenuAction extends AbstractAction {
        private boolean show;	// whether to show the menu

        public ShowSystemMenuAction(boolean show) {
            this.show = show;
        }

        public void actionPerformed(ActionEvent e) {
            if (show) {
                // TODO: FIX THIS!!!
            } else {
                systemPopupMenu.setVisible(false);
            }
        }      
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */
    class SizeAction extends AbstractAction {
        public SizeAction() {
            super(SIZE_CMD);
        }

        public void actionPerformed(ActionEvent e) {
            // This action is currently undefined
        }      
    } // end SizeAction


    private JButton createNoFocusButton() {
        JButton button = new JButton();
        button.setFocusable(false);
        button.setMargin(new Insets(0,0,0,0));
        return button;
    }
}   // End Title Pane Class
