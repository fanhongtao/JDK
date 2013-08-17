/*
 * @(#)BasicPopupMenuUI.java	1.64 98/08/26
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package javax.swing.plaf.basic;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.border.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.*;
import java.util.*;



/**
 * A Windows L&F implementation of PopupMenuUI.  This implementation 
 * is a "combined" view/controller.
 *
 * @version 1.64 08/26/98
 * @author Georges Saab
 * @author David Karlton
 * @author Arnaud Weber
 */
public class BasicPopupMenuUI extends PopupMenuUI {
    protected JPopupMenu popupMenu = null;
    private Frame frame;

    public static ComponentUI createUI(JComponent x) {
	return new BasicPopupMenuUI();
    }

    public void installUI(JComponent c) {
	popupMenu = (JPopupMenu) c;

	installDefaults();
        installListeners();
        installKeyboardActions();

    }

    public void installDefaults() {
	if (popupMenu.getLayout() == null ||
	    popupMenu.getLayout() instanceof UIResource)
	    popupMenu.setLayout(new DefaultMenuLayout(popupMenu, BoxLayout.Y_AXIS));

	popupMenu.setOpaque(true);
	LookAndFeel.installBorder(popupMenu, "PopupMenu.border");
	LookAndFeel.installColorsAndFont(popupMenu,
					 "PopupMenu.background",
					 "PopupMenu.foreground",
					 "PopupMenu.font");
    }
    
    protected void installListeners() {
	if (mouseGrabber == null)
	    mouseGrabber = new MouseGrabber();
    }

    protected void installKeyboardActions() {}

    public void uninstallUI(JComponent c) {
        uninstallDefaults();
        uninstallListeners();
        uninstallKeyboardActions();
	
	popupMenu = null;
    }
    
    protected void uninstallDefaults() {}

    protected void uninstallListeners() {
    }

    protected void uninstallKeyboardActions() {}

    public Dimension getMinimumSize(JComponent c) {
        return getPreferredSize(c);
    }

    public Dimension getPreferredSize(JComponent c) {
	return null;
    }

    public Dimension getMaximumSize(JComponent c) {
        return getPreferredSize(c);
    }

///////////////////////////////////////////////////////////////////////////////
//// Grab Code
///////////////////////////////////////////////////////////////////////////////
    private static Window getWindow(Component c) {
        Component w = c;

        while(!(w instanceof Window) && (w!=null)) {
            w = w.getParent();
        }
        return (Window)w;
    }

    
    private transient static MouseGrabber mouseGrabber = null;

    private static class MouseGrabber implements MouseListener, MouseMotionListener,WindowListener,ComponentListener, ChangeListener {
	Vector grabbed = new Vector();
	MenuElement lastGrabbed = null;
	boolean lastGrabbedMenuBarChild = false;

        public MouseGrabber() {
	    MenuSelectionManager msm = MenuSelectionManager.defaultManager();
	    msm.addChangeListener(this);
        }
	
	private void requestAddGrab(Component invoker) {
	    Window ancestor;
	    ancestor = getWindow(invoker);

	    MenuSelectionManager msm = MenuSelectionManager.defaultManager();
	    MenuElement[] p = msm.getSelectedPath();
	    Component excluded = null;
	    
	    for(int i = 0 ; excluded == null && i < p.length ; i++) {
		if (p[i] instanceof JPopupMenu)
		    excluded = p[i].getComponent();
	    }

	    grabContainer(ancestor, excluded);
	}

	private void requestRemoveGrab() {
	    ungrabContainers();
	}
    
	void cancelPopupMenu() {
	    MenuSelectionManager msm = MenuSelectionManager.defaultManager();
	    MenuElement[] p = msm.getSelectedPath();

	    if (lastGrabbed==getFirstPopup()) {
		MenuSelectionManager.defaultManager().clearSelectedPath();
		ungrabContainers();
	    } else {
		// The cancel has cause another menu selection
		lastGrabbed=getFirstPopup();
		if (p[0] instanceof JMenuBar) {
		    lastGrabbedMenuBarChild = true;
		} else {
		    lastGrabbedMenuBarChild = false;
		}
	    }

	}

	MenuElement[] lastPathSelected = new MenuElement[0];

	public void stateChanged(ChangeEvent e) {
	    MenuSelectionManager msm = MenuSelectionManager.defaultManager();
	    MenuElement[] p = msm.getSelectedPath();

	    if (lastPathSelected.length == 0 &&
		p.length != 0) {
		// A grab needs to be added
		Component invoker = p[0].getComponent();
		if (invoker instanceof JPopupMenu)
		    invoker = ((JPopupMenu)invoker).getInvoker();
		requestAddGrab(invoker);
	    }

	    if (lastPathSelected.length != 0 &&
		p.length == 0) {
		// The grab should be removed
		requestRemoveGrab();
	    }

	    // Switch between menubar children
	    if (p!=null && p.length>2 && (p[0] instanceof JMenuBar &&
					  lastGrabbedMenuBarChild == true)) {

		if (!(lastGrabbed==getFirstPopup())) {
		    lastGrabbed=getFirstPopup();

		    if (p[0] instanceof JMenuBar) {
			lastGrabbedMenuBarChild = true;
		    } else {
			lastGrabbedMenuBarChild = false;
		    }
		}
	    }

	    // Remember the last path selected
	    lastPathSelected = p;
	}
	
	private MenuElement getFirstPopup() {
	    MenuSelectionManager msm = MenuSelectionManager.defaultManager();
	    MenuElement[] p = msm.getSelectedPath();
	    MenuElement me = null;	    

	    for(int i = 0 ; me == null && i < p.length ; i++) {
		if (p[i] instanceof JPopupMenu)
		    me = p[i];
	    }

	    return me;
	}
	
	void grabContainer(Container c, Component excluded) {
	    if(c == excluded)
		return;
	    
	    MenuSelectionManager msm = MenuSelectionManager.defaultManager();
	    MenuElement[] p = msm.getSelectedPath();
	    lastGrabbed = getFirstPopup();
	     if (p[0] instanceof JMenuBar) {
		 lastGrabbedMenuBarChild = true;
	     } else {
		 lastGrabbedMenuBarChild = false;
	     }

	    if(c instanceof java.awt.Window) {
		((java.awt.Window)c).addWindowListener(this);
		((java.awt.Window)c).addComponentListener(this);
		grabbed.addElement(c);
	    }
	    synchronized(c.getTreeLock()) {
		int ncomponents = c.getComponentCount();
		Component[] component = c.getComponents();
		for (int i = 0 ; i < ncomponents ; i++) {
		    Component comp = component[i];
		    if(!comp.isVisible())
			continue;
		    comp.addMouseListener(this);
		    comp.addMouseMotionListener(this);
		    grabbed.addElement(comp);
                    if (comp instanceof Container) {
                        Container cont = (Container) comp;
                        grabContainer(cont, excluded);
                    } 
                }
            }
	    
        }
	
        void ungrabContainers() {
            int i,c;
            Component cpn;
            for(i=0,c=grabbed.size();i<c;i++) {
                cpn = (Component)grabbed.elementAt(i);
                if(cpn instanceof java.awt.Window) {
                    ((java.awt.Window)cpn).removeWindowListener(this);
                    ((java.awt.Window)cpn).removeComponentListener(this);
                } else {
                    cpn.removeMouseListener(this);
                    cpn.removeMouseMotionListener(this);
                }
            }
            grabbed = new Vector();
	    lastGrabbed = null;
	    lastGrabbedMenuBarChild = false;
        }
	
        public void mousePressed(MouseEvent e) {
            Component c = (Component)e.getSource();
            MenuSelectionManager msm = MenuSelectionManager.defaultManager();
	    
	    cancelPopupMenu();
	    /*
	      if(msm.isComponentPartOfCurrentMenu(popupMenu) && msm.isComponentPartOfCurrentMenu(c)) {
	      return;
	      } else {
	      cancelPopupMenu();
	      }
	      */
        }
        
        public void mouseReleased(MouseEvent e) {
            MenuSelectionManager.defaultManager().processMouseEvent(e);
        }
        public void mouseEntered(MouseEvent e) {
            // MenuSelectionManager.defaultManager().processMouseEvent(e);
        }
        public void mouseExited(MouseEvent e) {
            // MenuSelectionManager.defaultManager().processMouseEvent(e);
        }
        public void mouseMoved(MouseEvent e) {
            // MenuSelectionManager.defaultManager().processMouseEvent(e);
        }
        public void mouseDragged(MouseEvent e) {
            MenuSelectionManager.defaultManager().processMouseEvent(e);
        }
        public void mouseClicked(MouseEvent e) {
        }
        public void componentResized(ComponentEvent e) {
            cancelPopupMenu();
        }
        public void componentMoved(ComponentEvent e) {
            cancelPopupMenu();
        }
        public void componentShown(ComponentEvent e) {
            cancelPopupMenu();
        }
        public void componentHidden(ComponentEvent e) {
            cancelPopupMenu();
        }
        public void windowOpened(WindowEvent e) {}
        public void windowClosing(WindowEvent e) {
            cancelPopupMenu();
        }
        public void windowClosed(WindowEvent e) {
            cancelPopupMenu();
        }
        public void windowIconified(WindowEvent e) {
            cancelPopupMenu();
        }
        public void windowDeiconified(WindowEvent e) {}
        public void windowActivated(WindowEvent e) {
            /*   cancelPopupMenu(); cannot do this because
                 this might happen when using cascading heavy weight 
                 menus
                 */
        }
        public void windowDeactivated(WindowEvent e) {
            /*   cancelPopupMenu(); cannot do this because
                 this might happen when using cascading heavy weight 
                 menus
                 */
        }
    }

}




