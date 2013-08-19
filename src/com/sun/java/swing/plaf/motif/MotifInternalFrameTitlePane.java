/*
 * @(#)MotifInternalFrameTitlePane.java	1.25 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.motif;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.InternalFrameEvent;
import java.util.EventListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;
import java.beans.PropertyVetoException;

/**
 * Class that manages a Motif title bar
 * @version 1.25 01/23/03
 *
 * @since 1.3
 */
public class MotifInternalFrameTitlePane 
    extends JComponent implements LayoutManager, ActionListener, PropertyChangeListener 
{
    SystemButton systemButton;
    MinimizeButton minimizeButton;
    MaximizeButton maximizeButton;
    JPopupMenu systemMenu;
    Title title;
    JInternalFrame iFrame;
    Color color;
    Color highlight;
    Color shadow;

    static final Font defaultTitleFont = new Font("SansSerif", Font.PLAIN, 12);

    // The width and height of a title pane button
    public final static int BUTTON_SIZE = 19;  // 17 + 1 pixel border

    private static final String CLOSE_CMD =
        UIManager.getString("InternalFrameTitlePane.closeButtonText");
    private static final String ICONIFY_CMD =
        UIManager.getString("InternalFrameTitlePane.minimizeButtonText");
    private static final String RESTORE_CMD =
        UIManager.getString("InternalFrameTitlePane.restoreButtonText");
    private static final String MAXIMIZE_CMD =
        UIManager.getString("InternalFrameTitlePane.maximizeButtonText");
    private static final String MOVE_CMD =
        UIManager.getString("InternalFrameTitlePane.moveButtonText");
    private static final String SIZE_CMD =
        UIManager.getString("InternalFrameTitlePane.sizeButtonText");

    final int RESTORE_MENU_ITEM = 0;
    final int MOVE_MENU_ITEM = 1;
    final int SIZE_MENU_ITEM = 2;
    final int MINIMIZE_MENU_ITEM = 3;
    final int MAXIMIZE_MENU_ITEM = 4;
    final int SEPARATOR_MENU_ITEM = 5;
    final int CLOSE_MENU_ITEM = 6;
	
    public MotifInternalFrameTitlePane(JInternalFrame f) {
	iFrame = f;
	
	setPreferredSize(new Dimension(100, BUTTON_SIZE));

	systemMenu = new JPopupMenu() {
            public void show(Component invoker, int x, int y) {
                if(!iFrame.isIconifiable())
                    systemMenu.getComponentAtIndex(MINIMIZE_MENU_ITEM).setEnabled(false);
                if(!iFrame.isMaximizable()) 
                    systemMenu.getComponentAtIndex(MAXIMIZE_MENU_ITEM).setEnabled(false);
                if(!iFrame.isMaximizable() && !iFrame.isIconifiable()) 
                    systemMenu.getComponentAtIndex(RESTORE_MENU_ITEM).setEnabled(false);
                if(!iFrame.isClosable())	
                    systemMenu.getComponentAtIndex(CLOSE_MENU_ITEM).setEnabled(false);
                super.show(invoker, x, y);
            }
        };
	
	JMenuItem mi = (JMenuItem) systemMenu.add(new JMenuItem(RESTORE_CMD));
	mi.setEnabled(iFrame.isIcon());
	mi.addActionListener(this);
	/// PENDING(klobad) Move/Size actions on InternalFrame need to be determined
	mi = (JMenuItem) systemMenu.add(new JMenuItem(MOVE_CMD));
	mi.setEnabled(false);
	mi.addActionListener(this);
	mi = (JMenuItem) systemMenu.add(new JMenuItem(SIZE_CMD));
	mi.setEnabled(false);
	mi.addActionListener(this);
	mi = (JMenuItem) systemMenu.add(new JMenuItem(ICONIFY_CMD));
	mi.setEnabled(!iFrame.isIcon());
	mi.addActionListener(this);
	mi = (JMenuItem) systemMenu.add(new JMenuItem(MAXIMIZE_CMD));
	mi.addActionListener(this);
	systemMenu.add(new JSeparator());
	mi = (JMenuItem) systemMenu.add(new JMenuItem(CLOSE_CMD));
	mi.addActionListener(this); 
	
        systemButton = new SystemButton();
	systemButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                systemMenu.show(systemButton, 0, BUTTON_SIZE);
            }
        });
        systemButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
				if ((e.getClickCount() == 2)){
					if (iFrame.isClosable()) {
						try{
							iFrame.setClosed(true);
						} catch (PropertyVetoException e0) { }
					}
					systemMenu.setVisible(false);
				}
			}
		});

	minimizeButton = new MinimizeButton();
	minimizeButton.addActionListener(this);
	minimizeButton.setActionCommand(ICONIFY_CMD);

	maximizeButton = new MaximizeButton();
	maximizeButton.addActionListener(this);
	maximizeButton.setActionCommand(MAXIMIZE_CMD);

        title = new Title(iFrame.getTitle());
        title.setFont(defaultTitleFont);

	setLayout(this);

	add(systemButton);
        add(title);
	add(minimizeButton);
	add(maximizeButton);

	// Make sure these are ok to leave on?
	iFrame.addPropertyChangeListener(this);
    }


    void setColors(Color c, Color h, Color s) {
        color = c;
        highlight = h;
        shadow = s;
    }

    JPopupMenu getSystemMenu() {
        return systemMenu;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            if (CLOSE_CMD.equals(e.getActionCommand()) && iFrame.isClosable()) {
                iFrame.doDefaultCloseAction();
            } else if (ICONIFY_CMD.equals(e.getActionCommand()) &&
                    iFrame.isIconifiable()) {    //4118140
                if (!iFrame.isIcon()) {
                    iFrame.setIcon(true); 
                } else {
                    iFrame.setIcon(false); 
                }
            } else if (MAXIMIZE_CMD.equals(e.getActionCommand()) &&
                    iFrame.isMaximizable()) {
                if (!iFrame.isMaximum()) {
                    iFrame.setMaximum(true);
                } else {
                    iFrame.setMaximum(false);
                }
            } else if (RESTORE_CMD.equals(e.getActionCommand()) &&
                    iFrame.isMaximizable() && iFrame.isMaximum()) {
                iFrame.setMaximum(false);
            } else if (RESTORE_CMD.equals(e.getActionCommand()) &&
                    iFrame.isIconifiable() && iFrame.isIcon()) {
                iFrame.setIcon(false);
            }
        } catch (PropertyVetoException e0) { }

        // Dismiss popup menu if it's displayed
        if (systemMenu.isVisible()) {
            systemMenu.setVisible(false);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
	String prop = (String)evt.getPropertyName();
	JInternalFrame f = (JInternalFrame)evt.getSource();
	boolean value = false;
	if(JInternalFrame.IS_SELECTED_PROPERTY.equals(prop)) {
	    repaint();
	} else if(JInternalFrame.IS_MAXIMUM_PROPERTY.equals(prop)) {
	    value = ((Boolean)evt.getNewValue()).booleanValue();
	    systemMenu.getComponentAtIndex(RESTORE_MENU_ITEM).setEnabled(value); 
	    systemMenu.getComponentAtIndex(MAXIMIZE_MENU_ITEM).setEnabled(!value); 
	} else if(JInternalFrame.IS_ICON_PROPERTY.equals(prop)) {
	    value = ((Boolean)evt.getNewValue()).booleanValue();
	    systemMenu.getComponentAtIndex(RESTORE_MENU_ITEM).setEnabled(value);
	    if (f.isMaximizable())
	      systemMenu.getComponentAtIndex(MAXIMIZE_MENU_ITEM).setEnabled(true);
	    else
	      systemMenu.getComponentAtIndex(MAXIMIZE_MENU_ITEM).setEnabled(false); 
	    systemMenu.getComponentAtIndex(MINIMIZE_MENU_ITEM).setEnabled(!value); 
	} else if( prop.equals("maximizable") ) {
            if( (Boolean)evt.getNewValue() == Boolean.TRUE )
                add(maximizeButton);
            else
                remove(maximizeButton);
            revalidate();
            repaint();
        } else if( prop.equals("iconable") ) {
            if( (Boolean)evt.getNewValue() == Boolean.TRUE )
                add(minimizeButton);
            else
                remove(minimizeButton);
            revalidate();
            repaint();
        } else if (prop.equals(JInternalFrame.TITLE_PROPERTY)) {
            repaint();
      }
    }

    public void addLayoutComponent(String name, Component c) {}
    public void removeLayoutComponent(Component c) {}    
    public Dimension preferredLayoutSize(Container c)  {
	return new Dimension(c.getSize().width, BUTTON_SIZE);
    }
    
    public Dimension minimumLayoutSize(Container c) {
	return new Dimension(c.getSize().width, BUTTON_SIZE);
    }
    
    public void layoutContainer(Container c) {
	int w = getWidth();
	systemButton.setBounds(0, 0, BUTTON_SIZE, BUTTON_SIZE);
	int x = w - BUTTON_SIZE;

	if(iFrame.isMaximizable()) {
	    maximizeButton.setBounds(x, 0, BUTTON_SIZE, BUTTON_SIZE);
	    x -= BUTTON_SIZE;
	} else if(maximizeButton.getParent() != null) {
	    maximizeButton.getParent().remove(maximizeButton);
	}
        
	if(iFrame.isIconifiable()) {
	    minimizeButton.setBounds(x, 0, BUTTON_SIZE, BUTTON_SIZE);
	    x -= BUTTON_SIZE;
	} else if(minimizeButton.getParent() != null) {
	    minimizeButton.getParent().remove(minimizeButton);
	}

        title.setBounds(BUTTON_SIZE, 0, x, BUTTON_SIZE);
    }

    protected void showSystemMenu(){
      systemMenu.show(systemButton, 0, BUTTON_SIZE);
    }    
   
    protected void hideSystemMenu(){
      systemMenu.setVisible(false);
    }
    
    static Dimension buttonDimension = new Dimension(BUTTON_SIZE, BUTTON_SIZE);

    private abstract class FrameButton extends JButton {

        FrameButton() {
            super();
            setFocusPainted(false);     
            setBorderPainted(false);
        }

        public boolean isFocusTraversable() { 
            return false; 
        }

        public void requestFocus() {
            // ignore request.
        }

        public Dimension getMinimumSize() {
            return buttonDimension;
        }

        public Dimension getPreferredSize() {
            return buttonDimension;
        }

        public void paint(Graphics g) {
            Dimension d = getSize();
            int maxX = d.width - 1;
            int maxY = d.height - 1;

            // draw background
            g.setColor(color);
            g.fillRect(1, 1, d.width, d.height);

            // draw border
            boolean pressed = getModel().isPressed();
            g.setColor(pressed ? shadow : highlight);
            g.drawLine(0, 0, maxX, 0);
            g.drawLine(0, 0, 0, maxY);
            g.setColor(pressed ? highlight : shadow);
            g.drawLine(1, maxY, maxX, maxY);
            g.drawLine(maxX, 1, maxX, maxY);
        }
    }
    
    private class MinimizeButton extends FrameButton {
        public void paint(Graphics g) {
            super.paint(g); 
            g.setColor(highlight);
            g.drawLine(7, 8, 7, 11);
            g.drawLine(7, 8, 10, 8);
            g.setColor(shadow);
            g.drawLine(8, 11, 10, 11);
            g.drawLine(11, 9, 11, 11);
        }
    }

    private class MaximizeButton extends FrameButton {
        public void paint(Graphics g) {
            super.paint(g); 
            int max = BUTTON_SIZE - 5;
            boolean isMaxed = iFrame.isMaximum();
            g.setColor(isMaxed ? shadow : highlight);
            g.drawLine(4, 4, 4, max);
            g.drawLine(4, 4, max, 4);
            g.setColor(isMaxed ? highlight : shadow);
            g.drawLine(5, max, max, max);
            g.drawLine(max, 5, max, max);
        }
    }

    private class SystemButton extends FrameButton {
        public boolean isFocusTraversable() { return false; }
        public void requestFocus() {}

        public void paint(Graphics g) {
            super.paint(g);
            g.setColor(highlight);
            g.drawLine(4, 8, 4, 11);
            g.drawLine(4, 8, BUTTON_SIZE - 5, 8);
            g.setColor(shadow);
            g.drawLine(5, 11, BUTTON_SIZE - 5, 11);
            g.drawLine(BUTTON_SIZE - 5, 9, BUTTON_SIZE - 5, 11);
        }
    }

    private class Title extends FrameButton {
        Title(String title) {
            super();
            setText(title);
            setHorizontalAlignment(SwingConstants.CENTER);
            setBorder(BorderFactory.createBevelBorder(
                BevelBorder.RAISED,
                UIManager.getColor("activeCaptionBorder"),
                UIManager.getColor("inactiveCaptionBorder")));

            // Forward mouse events to titlebar for moves.
            addMouseMotionListener(new MouseMotionListener() {
                public void mouseDragged(MouseEvent e) {
                    forwardEventToParent(e);
                }
                public void mouseMoved(MouseEvent e) {
                    forwardEventToParent(e);
                }
            });
            addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                    forwardEventToParent(e);
                }
                public void mousePressed(MouseEvent e) {
                    forwardEventToParent(e);
                }
                public void mouseReleased(MouseEvent e) {
                    forwardEventToParent(e);
                }
                public void mouseEntered(MouseEvent e) {
                    forwardEventToParent(e);
                }
                public void mouseExited(MouseEvent e) {
                    forwardEventToParent(e);
                }
            });
        }

        void forwardEventToParent(MouseEvent e) {
            getParent().dispatchEvent(new MouseEvent(
                getParent(), e.getID(), e.getWhen(), e.getModifiers(),
                e.getX(), e.getY(), e.getClickCount(), e.isPopupTrigger()));
        }

        public void paint(Graphics g) {
            super.paint(g);
            if (iFrame.isSelected()) {
                g.setColor(UIManager.getColor("activeCaptionText"));
            } else {
                g.setColor(UIManager.getColor("inactiveCaptionText"));
            }
            Dimension d = getSize();
            String frameTitle = iFrame.getTitle();
            if (frameTitle != null) {
                MotifGraphicsUtils.drawStringInRect(g, frameTitle,
                                                    0, 0, d.width, d.height,
                                                    SwingConstants.CENTER);
            }
        }
    }

}    /// End Title Pane Class
