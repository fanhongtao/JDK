/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

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
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.36 10/21/02
 * @author David Kloba
 * @author Steve Wilson
 */
public class BasicInternalFrameTitlePane extends JComponent
{
    protected JMenuBar menuBar;
    protected JButton iconButton;
    protected JButton maxButton;
    protected JButton closeButton;

    protected JMenu windowMenu;
    protected JInternalFrame frame;

    protected Color selectedTitleColor;
    protected Color selectedTextColor;
    protected Color notSelectedTitleColor;
    protected Color notSelectedTextColor;

    protected Icon maxIcon;
    protected Icon minIcon;
    protected Icon iconIcon;
    protected Icon closeIcon;

    protected PropertyChangeListener propertyChangeListener;

    protected Action closeAction;
    protected Action maximizeAction;
    protected Action iconifyAction;
    protected Action restoreAction;
    protected Action moveAction;
    protected Action sizeAction;

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
    private String iconButtonToolTip;
    private String restoreButtonToolTip;
    private String maxButtonToolTip;


    public BasicInternalFrameTitlePane(JInternalFrame f) {
	frame = f;
	installTitlePane();
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

    protected void addSubComponents() {
	add(menuBar);
	add(iconButton);
	add(maxButton);
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
        if( propertyChangeListener == null ) {
            propertyChangeListener = createPropertyChangeListener();
        }
	frame.addPropertyChangeListener(propertyChangeListener);
    }

    protected void uninstallListeners() {
	frame.removePropertyChangeListener(propertyChangeListener);
    }

    protected void installDefaults() {
        maxIcon = UIManager.getIcon("InternalFrame.maximizeIcon");
	minIcon = UIManager.getIcon("InternalFrame.minimizeIcon");
	iconIcon = UIManager.getIcon("InternalFrame.iconifyIcon");
	closeIcon = UIManager.getIcon("InternalFrame.closeIcon");

	selectedTitleColor = UIManager.getColor("InternalFrame.activeTitleBackground");
	selectedTextColor = UIManager.getColor("InternalFrame.activeTitleForeground");
	notSelectedTitleColor = UIManager.getColor("InternalFrame.inactiveTitleBackground");
	notSelectedTextColor = UIManager.getColor("InternalFrame.inactiveTitleForeground");
        setFont(UIManager.getFont("InternalFrame.titleFont"));
        closeButtonToolTip =
                UIManager.getString("InternalFrame.closeButtonToolTip");
        iconButtonToolTip =
                UIManager.getString("InternalFrame.iconButtonToolTip");
        restoreButtonToolTip =
                UIManager.getString("InternalFrame.restoreButtonToolTip");
        maxButtonToolTip =
                UIManager.getString("InternalFrame.maxButtonToolTip");


    }


    protected void uninstallDefaults() {
    }


    protected void createButtons() {
	iconButton = new NoFocusButton();
	iconButton.addActionListener(iconifyAction);
        if (iconButtonToolTip != null && iconButtonToolTip.length() != 0) {
            iconButton.setToolTipText(iconButtonToolTip);
        }

	maxButton = new NoFocusButton();
	maxButton.addActionListener(maximizeAction);

	closeButton = new NoFocusButton();  
	closeButton.addActionListener(closeAction);
        if (closeButtonToolTip != null && closeButtonToolTip.length() != 0) {
            closeButton.setToolTipText(closeButtonToolTip);
        }

        setButtonIcons();
    }

    protected void setButtonIcons() {
	if(frame.isIcon()) {
	    iconButton.setIcon(minIcon);
            if (restoreButtonToolTip != null &&
                    restoreButtonToolTip.length() != 0) {
                iconButton.setToolTipText(restoreButtonToolTip);
            }
	    maxButton.setIcon(maxIcon);
            if (maxButtonToolTip != null && maxButtonToolTip.length() != 0) {
                maxButton.setToolTipText(maxButtonToolTip);
            }
        } else if (frame.isMaximum()) {
	    iconButton.setIcon(iconIcon);
	    maxButton.setIcon(minIcon);
            if (restoreButtonToolTip != null &&
                    restoreButtonToolTip.length() != 0) {
                maxButton.setToolTipText(restoreButtonToolTip);
            }
        } else {
	    iconButton.setIcon(iconIcon);
            if (iconButtonToolTip != null && iconButtonToolTip.length() != 0) {
                iconButton.setToolTipText(iconButtonToolTip);
            }
	    maxButton.setIcon(maxIcon);
            if (maxButtonToolTip != null && maxButtonToolTip.length() != 0) {
                maxButton.setToolTipText(maxButtonToolTip);
            }
        }

	closeButton.setIcon(closeIcon);
    }


    protected void assembleSystemMenu() {
        menuBar = createSystemMenuBar();
	windowMenu = createSystemMenu();	    
	menuBar.add(windowMenu);
	addSystemMenuItems(windowMenu);
	enableActions();
    }

    protected void addSystemMenuItems(JMenu systemMenu) {
        JMenuItem mi = (JMenuItem)systemMenu.add(restoreAction);
	mi.setMnemonic('R');
	mi = (JMenuItem)systemMenu.add(moveAction);
	mi.setMnemonic('M');
	mi = (JMenuItem)systemMenu.add(sizeAction);
	mi.setMnemonic('S');
	mi = (JMenuItem)systemMenu.add(iconifyAction);
	mi.setMnemonic('n');
	mi = (JMenuItem)systemMenu.add(maximizeAction);
	mi.setMnemonic('x');
	systemMenu.add(new JSeparator());
	mi = (JMenuItem)systemMenu.add(closeAction);
	mi.setMnemonic('C');
    }

    protected JMenu createSystemMenu() {
	return new JMenu("    ");
    }

    protected JMenuBar createSystemMenuBar() {
	menuBar = new SystemMenuBar();
	menuBar.setBorderPainted(false);
	return menuBar;
    }
      
    protected void showSystemMenu(){
	//      windowMenu.setPopupMenuVisible(true);
      //      windowMenu.setVisible(true);
      windowMenu.doClick();
    }

    public void paintComponent(Graphics g)  {
	boolean isSelected = frame.isSelected();

	if(isSelected)
	    g.setColor(selectedTitleColor);
	else
	    g.setColor(notSelectedTitleColor);
	g.fillRect(0, 0, getWidth(), getHeight());

	if(frame.getTitle() != null) {
	    Font f = g.getFont();
	    g.setFont(UIManager.getFont("InternalFrame.titleFont"));
	    if(isSelected)
		g.setColor(selectedTextColor);
	    else
		g.setColor(notSelectedTextColor);

            // Center text vertically.
	    FontMetrics fm = g.getFontMetrics();
            int fmHeight = fm.getHeight() - fm.getLeading();
            int baseline = (18 - fmHeight) / 2 + 
                fm.getAscent() + fm.getLeading();

            int titleX;
            String title = frame.getTitle();
            if( BasicGraphicsUtils.isLeftToRight(frame) ) {
                titleX = menuBar.getX() + menuBar.getWidth() + 2;
            } else {
                titleX = menuBar.getX() - 2
                         - SwingUtilities.computeStringWidth(fm,title);
            }
            
	    g.drawString(title, titleX, baseline);
	    g.setFont(f);
	}
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
	maximizeAction.setEnabled(frame.isMaximizable() && !frame.isMaximum() ); 
	iconifyAction.setEnabled(frame.isIconifiable() && !frame.isIcon()); 
	closeAction.setEnabled(frame.isClosable());
	sizeAction.setEnabled(false);
	moveAction.setEnabled(false);
    }


    protected PropertyChangeListener createPropertyChangeListener() {
        return new PropertyChangeHandler();
    }

    protected LayoutManager createLayout() {
        return new TitlePaneLayout();
    }


    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */
    public class PropertyChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {

	    String prop = (String)evt.getPropertyName();

	    if(JInternalFrame.IS_SELECTED_PROPERTY.equals(prop)) {
	        repaint();
		return;
	    } 

	    if(JInternalFrame.IS_ICON_PROPERTY.equals(prop) ||
	       JInternalFrame.IS_MAXIMUM_PROPERTY.equals(prop)) {
		setButtonIcons();
		enableActions();
		return;
	    }

            if( prop.equals("closable") ) {
                if( (Boolean)evt.getNewValue() == Boolean.TRUE )
                    add(closeButton);
                else
                    remove(closeButton);
            } else if( prop.equals("maximizable") ) {
                if( (Boolean)evt.getNewValue() == Boolean.TRUE )
                    add(maxButton);
                else
                    remove(maxButton);
            } else if( prop.equals("iconifiable") ) {
                if( (Boolean)evt.getNewValue() == Boolean.TRUE )
                    add(iconButton);
                else
                    remove(iconButton);
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
    public class TitlePaneLayout implements LayoutManager {
        public void addLayoutComponent(String name, Component c) {}
        public void removeLayoutComponent(Component c) {}    
        public Dimension preferredLayoutSize(Container c)  {
	    return new Dimension(100, 18);
	}
    
        public Dimension minimumLayoutSize(Container c) {
	    return preferredLayoutSize(c);
	}
    
        public void layoutContainer(Container c) {
            boolean leftToRight = BasicGraphicsUtils.isLeftToRight(frame);
            
	    int w = getWidth();
            int x;

            x = (leftToRight) ? 2 : w - 16 - 2;
            menuBar.setBounds(x, 1, 16, 16);

            x = (leftToRight) ? w - 16 - 2 : 2;
            
	    if(frame.isClosable()) {
                closeButton.setBounds(x, 2, 16, 14);
                x += (leftToRight) ? -(16 + 2) : 16 + 2;
	    } 
            
	    if(frame.isMaximizable()) {
	        maxButton.setBounds(x, 2, 16, 14);
		x += (leftToRight) ? -(16 + 2) : 16 + 2;
	    }
        
	    if(frame.isIconifiable()) {
	        iconButton.setBounds(x, 2, 16, 14);
	    } 
	}
    } // end TitlePaneLayout

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */  
    public class CloseAction extends AbstractAction {
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
    public class MaximizeAction extends AbstractAction {
        public MaximizeAction() {
	    super(MAXIMIZE_CMD);
        }

        public void actionPerformed(ActionEvent e) {
	    if(frame.isMaximizable()) {
	        if(!frame.isMaximum()) {
		    try { frame.setMaximum(true); } catch (PropertyVetoException e5) { }
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
    public class IconifyAction extends AbstractAction {
        public IconifyAction() {
	    super(ICONIFY_CMD);
        }

        public void actionPerformed(ActionEvent e) {
	    if(frame.isIconifiable()) {
	      if(!frame.isIcon()) {
		try { frame.setIcon(true); } catch (PropertyVetoException e1) { }
	      } else{
		try { frame.setIcon(false); } catch (PropertyVetoException e1) { }
	      }
	    }
	}
    } // end IconifyAction

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */
    public class RestoreAction extends AbstractAction {
        public RestoreAction() {
	    super(RESTORE_CMD);
        }

        public void actionPerformed(ActionEvent e) {
	    if(frame.isMaximizable() && frame.isMaximum()) {
	        try { frame.setMaximum(false); } catch (PropertyVetoException e4) { }
	    } 
	    else if ( frame.isIconifiable() && frame.isIcon() ) {
	      try { frame.setIcon(false); } catch (PropertyVetoException e4) { }
	    }
	}      
    } // end RestoreAction

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */
    public class MoveAction extends AbstractAction {
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
		windowMenu.doClick();
	    } else {
		windowMenu.setVisible(false);
	    }
	}      
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */
    public class SizeAction extends AbstractAction {
        public SizeAction() {
	    super(SIZE_CMD);
        }

        public void actionPerformed(ActionEvent e) {
	    // This action is currently undefined
	}      
    } // end SizeAction


    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */
    public class SystemMenuBar extends JMenuBar {
	public boolean isFocusTraversable() { return false; }
	public void requestFocus() {}
	public void paint(Graphics g) {
	    Icon icon = frame.getFrameIcon();
	    if (icon == null) {
	      icon = UIManager.getIcon("InternalFrame.icon");
	    }
	    if (icon != null) {
	        // Resize to 16x16 if necessary.
	        if (icon instanceof ImageIcon && (icon.getIconWidth() > 16 || icon.getIconHeight() > 16)) {
		    Image img = ((ImageIcon)icon).getImage();
		    ((ImageIcon)icon).setImage(img.getScaledInstance(16, 16, Image.SCALE_SMOOTH));
		}
		icon.paintIcon(this, g, 0, 0);
	    }
	}

	public boolean isOpaque() { 
	    return true; 
	}
    } // end SystemMenuBar


    private class NoFocusButton extends JButton {
      public NoFocusButton() { setFocusPainted(false); }
	public boolean isFocusTraversable() { return false; }
	public void requestFocus() {};
        public boolean isOpaque() { return true; }
    };  // end NoFocusButton

}   // End Title Pane Class

