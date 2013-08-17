/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import java.util.*;

/**
 * A Windows L&F implementation of PopupMenuUI.  This implementation 
 * is a "combined" view/controller.
 *
 * @version 1.71 11/27/00
 * @author Georges Saab
 * @author David Karlton
 * @author Arnaud Weber
 */
public class BasicPopupMenuUI extends PopupMenuUI {
    protected JPopupMenu popupMenu = null;

    // Shared instance of a property change handler
    private static PropertyChangeListener changeHandler;

    // The previous focus owner which will request the focus
    // when the popup becomes hidden.
    private Component prevFocusOwner;

    private InputMap windowInputMap;

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
	if (mouseGrabber == null) {
	    mouseGrabber = new MouseGrabber();
	}

        if (changeHandler == null) {
            // Create shared instance of the PropertyChangeListener
            changeHandler = new PropertyChangeHandler();
        }
        popupMenu.addPropertyChangeListener(changeHandler);
    }

    protected void installKeyboardActions() {
        ActionMap actionMap = getActionMap();
        SwingUtilities.replaceUIActionMap(popupMenu, actionMap);

        Object[] bindings = (Object[])UIManager.get("PopupMenu.selectedWindowInputMapBindings");
        if (bindings != null) {
            windowInputMap = LookAndFeel.makeComponentInputMap(popupMenu, bindings);
            SwingUtilities.replaceUIInputMap(popupMenu,
                              JComponent.WHEN_IN_FOCUSED_WINDOW, windowInputMap);
        }
    }

    ActionMap getActionMap() {
        ActionMap map = (ActionMap)UIManager.get("PopupMenu.actionMap");
        if (map == null) {
            map = createActionMap();
            if (map != null) {
                UIManager.getLookAndFeelDefaults().put("PopupMenu.actionMap",
                                                       map);
            }
        }
        return map;
    }

    ActionMap createActionMap() {
        ActionMap map = new ActionMapUIResource();

        map.put("cancel", new CancelAction());
        map.put("selectNext", new SelectNextItemAction());
        map.put("selectPrevious", new SelectPreviousItemAction());
        map.put("selectParent", new SelectParentItemAction());
        map.put("selectChild", new SelectChildItemAction());
        map.put("return", new ReturnAction());

        // return the created map
        return map;
    }

    public void uninstallUI(JComponent c) {
        uninstallDefaults();
        uninstallListeners();
        uninstallKeyboardActions();
	
	popupMenu = null;
    }
    
    protected void uninstallDefaults() {
	LookAndFeel.uninstallBorder(popupMenu);
    }

    protected void uninstallListeners() {
        if (changeHandler != null) {
            popupMenu.removePropertyChangeListener(changeHandler);
        }
    }

    protected void uninstallKeyboardActions() {
        SwingUtilities.replaceUIActionMap(popupMenu, null);
        SwingUtilities.replaceUIInputMap(popupMenu,
                                  JComponent.WHEN_IN_FOCUSED_WINDOW, null);
    }

    public Dimension getMinimumSize(JComponent c) {
        return getPreferredSize(c);
    }

    public Dimension getPreferredSize(JComponent c) {
	return null;
    }

    public Dimension getMaximumSize(JComponent c) {
        return getPreferredSize(c);
    }

    private static class PropertyChangeHandler implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
	    Container parent;
            String propertyName = evt.getPropertyName();
            JPopupMenu menu = (JPopupMenu)evt.getSource();

            if (propertyName != null && propertyName.equals("visible")) {
                // The visible property will temporarily transfer the focus to 
                // the popup when it is visible and restores it to the
                // previous component when the popup is hidden.

                boolean isVisible = ((Boolean)evt.getNewValue()).booleanValue();
		boolean isRequestFocusEnabled;

                BasicPopupMenuUI ui = (BasicPopupMenuUI)menu.getUI();
                if (isVisible) {
                    if (ui.windowInputMap != null) {
                        SwingUtilities.replaceUIInputMap(menu,
                             JComponent.WHEN_IN_FOCUSED_WINDOW, ui.windowInputMap);
                    }

                    // Hold reference to the old focus owner
                    ui.prevFocusOwner = null;
		    for ( parent = menu.getParent() ; parent != null ; parent = parent.getParent()) {
			if(parent instanceof java.awt.Window) {
                            ui.prevFocusOwner = ((java.awt.Window)parent).getFocusOwner();
			    break;
			}
		    }
		    if ((ui.prevFocusOwner instanceof JComponent) &&
                        ((JComponent)ui.prevFocusOwner).getRootPane() != menu.getRootPane()) {
                      ui.prevFocusOwner = null;
                    }
                    //ui.prevFocusOwner = KeyboardFocusManager.
                        //getCurrentKeyboardFocusManager().getFocusOwner();

		    isRequestFocusEnabled = menu.isRequestFocusEnabled();

                    if (!isRequestFocusEnabled) {
			menu.setRequestFocusEnabled(true);
                        menu.requestFocus();
                        // XXX - should enable this when (if) JComponent.requestFocus(boolean)
                        // becomes public
                        //menu.requestFocus(true);
			menu.setRequestFocusEnabled(false);
                    } else {
                        menu.requestFocus();
                        // XXX - should enable this when (if) JComponent.requestFocus(boolean)
                        // becomes public
                        //menu.requestFocus(true);
                    }
                }
                else {
                    if (ui.windowInputMap != null) {
                        SwingUtilities.replaceUIInputMap(menu,
                                     JComponent.WHEN_IN_FOCUSED_WINDOW, null);
                    }

                    // Set focus to previous component
                    if (ui.prevFocusOwner != null) {
			if(ui.prevFocusOwner instanceof JComponent) {
                            JComponent jc = (JComponent) ui.prevFocusOwner;
                            boolean isRFEnabled = jc.isRequestFocusEnabled();
                            if(!isRFEnabled)
                                jc.setRequestFocusEnabled(true);
                            ui.prevFocusOwner.requestFocus();
                            if(!isRFEnabled)
                                jc.setRequestFocusEnabled(false);
                        } else
                            ui.prevFocusOwner.requestFocus();
                        ui.prevFocusOwner = null;

                    }
                }
            } // end "visible" handler
        }
    } // end PropertyChangeHandler


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

    public boolean isPopupTrigger(MouseEvent e) {
	return ((e.getID()==MouseEvent.MOUSE_RELEASED) 
		&& ((e.getModifiers() & MouseEvent.BUTTON3_MASK)!=0));
    }	    

    private static class CancelAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {

            MenuElement path[] = MenuSelectionManager.defaultManager().getSelectedPath();
            if(path.length > 4) { /* PENDING(arnaud) Change this to 2 when a mouse grabber is available for MenuBar */
                MenuElement newPath[] = new MenuElement[path.length - 2];
                System.arraycopy(path,0,newPath,0,path.length-2);
                MenuSelectionManager.defaultManager().setSelectedPath(newPath);
            } else
                MenuSelectionManager.defaultManager().clearSelectedPath();
        }
    }

    private static class ReturnAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            MenuElement path[] = MenuSelectionManager.defaultManager().getSelectedPath();
            MenuElement lastElement;
            if(path.length > 0) {
                lastElement = path[path.length-1];
                if(lastElement instanceof JMenu) {
                    MenuElement newPath[] = new MenuElement[path.length+1];
                    System.arraycopy(path,0,newPath,0,path.length);
                    newPath[path.length] = ((JMenu)lastElement).getPopupMenu();
                    MenuSelectionManager.defaultManager().setSelectedPath(newPath);
                } else if(lastElement instanceof JMenuItem) {
                    MenuSelectionManager.defaultManager().clearSelectedPath();
                    ((JMenuItem)lastElement).doClick(0);
                    ((JMenuItem)lastElement).setArmed(false);
                }
            }
        }
    }

    private static MenuElement nextEnabledChild(MenuElement e[],int fromIndex) {
        int i,c;
        for(i=fromIndex,c=e.length ; i < c ; i++) {
            if (e[i]!=null) {
                Component comp = e[i].getComponent();
                if(comp != null && comp.isEnabled())
                    return e[i];
            }
        }
        return null;
    }

    private static MenuElement previousEnabledChild(MenuElement e[],int fromIndex) {
        int i;
        for(i=fromIndex ; i >= 0 ; i--) {
            if (e[i]!=null) {
                Component comp = e[i].getComponent();
                if(comp != null && comp.isEnabled())
                    return e[i];
            }
        }
        return null;
    }

    private static class SelectNextItemAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {

            MenuElement currentSelection[] = MenuSelectionManager.defaultManager().getSelectedPath();
            if(currentSelection.length > 1) {
                MenuElement parent = currentSelection[currentSelection.length-2];
                if(parent.getComponent() instanceof JMenu) {
                    MenuElement childs[];
                    parent = currentSelection[currentSelection.length-1];
                    childs = parent.getSubElements();
                    if(childs.length > 0) {
                        MenuElement newPath[] = new MenuElement[currentSelection.length+1];
                        System.arraycopy(currentSelection,0,
                                         newPath,0,currentSelection.length);
                        newPath[currentSelection.length] = nextEnabledChild(childs,0);
                        if(newPath[currentSelection.length] != null) {
                            MenuSelectionManager.defaultManager().setSelectedPath(newPath);
                        }
                    }
                } else {
                    MenuElement childs[] = parent.getSubElements();
                    MenuElement nextChild;
                    int i,c;
                    for(i=0,c=childs.length;i<c;i++) {
                        if(childs[i] == currentSelection[currentSelection.length-1]) {
                            nextChild = nextEnabledChild(childs,i+1);
                            if(nextChild == null)
                                nextChild = nextEnabledChild(childs,0);
                            if(nextChild != null) {
                                currentSelection[currentSelection.length-1] = nextChild;
                                MenuSelectionManager.defaultManager().setSelectedPath(currentSelection);
                            }
                            break;
                        }
                    }
                }
            }
	}
    }

    private static class SelectPreviousItemAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {

            MenuElement currentSelection[] = MenuSelectionManager.defaultManager().getSelectedPath();
            if(currentSelection.length > 1) {
                MenuElement parent = currentSelection[currentSelection.length-2];
                if(parent.getComponent() instanceof JMenu) {
                    MenuElement childs[];
                    parent = currentSelection[currentSelection.length-1];
                    childs = parent.getSubElements();
                    if(childs.length > 0) {
                        MenuElement newPath[] = new MenuElement[currentSelection.length+1];
                        System.arraycopy(currentSelection,0,
                                         newPath,0,currentSelection.length);
                        newPath[currentSelection.length] = previousEnabledChild(childs,childs.length-1);
                        if(newPath[currentSelection.length] != null)
                            MenuSelectionManager.defaultManager().setSelectedPath(newPath);
                    }
                } else {
                    MenuElement childs[] = parent.getSubElements();
                    MenuElement nextChild;
                    int i,c;
                    for(i=0,c=childs.length;i<c;i++) {
                        if(childs[i] == currentSelection[currentSelection.length-1]) {
                            nextChild = previousEnabledChild(childs,i-1);
                            if(nextChild == null)
                                nextChild = previousEnabledChild(childs,childs.length-1);
			    if(nextChild != null) {
                                currentSelection[currentSelection.length-1] = nextChild;
                                MenuSelectionManager.defaultManager().setSelectedPath(currentSelection);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    private static class SelectParentItemAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            MenuElement path[] = MenuSelectionManager.defaultManager().getSelectedPath();

            if(path.length > 3 && path[path.length-3].getComponent() instanceof JMenu &&
               !((JMenu)path[path.length-3].getComponent()).isTopLevelMenu()) {
                MenuElement newPath[] = new MenuElement[path.length-2];
                System.arraycopy(path,0,newPath,0,path.length-2);
                MenuSelectionManager.defaultManager().setSelectedPath(newPath);
            } else if(path.length > 0 && path[0].getComponent() instanceof JMenuBar) {
                MenuElement nextMenu=null,popup=null,firstItem=null;
                MenuElement tmp[];
                int i,c;

                if(path.length > 1) {
                    MenuElement previousElement;
                    tmp = path[0].getSubElements();
                    for(i=0,c=tmp.length;i<c;i++) {
                        if(tmp[i] == path[1]) {
                            nextMenu = previousEnabledChild(tmp,i-1);
                            if(nextMenu == null)
                                nextMenu = previousEnabledChild(tmp,tmp.length-1);
                        }
                    }
                }

                if(nextMenu != null) {
                    MenuElement newSelection[];
                    popup = ((JMenu)nextMenu).getPopupMenu();
                    if(((JMenu)nextMenu).isTopLevelMenu())
                        firstItem = null;
                    else {
                        tmp = popup.getSubElements();
                        if(tmp.length > 0)
                            firstItem = nextEnabledChild(tmp,0);
                    }

		    if(firstItem != null) {
                        newSelection = new MenuElement[4];
                        newSelection[0] = path[0];
                        newSelection[1] = nextMenu;
                        newSelection[2] = popup;
                        newSelection[3] = firstItem;
                    } else {
                        newSelection = new MenuElement[3];
                        newSelection[0] = path[0];
                        newSelection[1] = nextMenu;
                        newSelection[2] = popup;
                    }
                    MenuSelectionManager.defaultManager().setSelectedPath(newSelection);
                }
            }
        }
    }

    private static class SelectChildItemAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            MenuElement path[] = MenuSelectionManager.defaultManager().getSelectedPath();

            if(path.length > 0 && path[path.length-1].getComponent().isEnabled() &&
               path[path.length-1].getComponent() instanceof JMenu &&
               !((JMenu)path[path.length-1].getComponent()).isTopLevelMenu()) {
                MenuElement newPath[] = new MenuElement[path.length+2];
                MenuElement subElements[];
                System.arraycopy(path,0,newPath,0,path.length);
                newPath[path.length] = ((JMenu)path[path.length-1].getComponent()).getPopupMenu();
                subElements = newPath[path.length].getSubElements();
                if(subElements.length > 0) {
                    newPath[path.length+1] = nextEnabledChild(subElements,0);
                    MenuSelectionManager.defaultManager().setSelectedPath(newPath);
                }
            } else if(path.length > 0 && path[0].getComponent() instanceof JMenuBar) {
                MenuElement nextMenu=null,popup=null,firstItem=null;
                MenuElement tmp[];
                int i,c;

                if(path.length > 1) {
                    tmp = path[0].getSubElements();
                    for(i=0,c=tmp.length;i<c;i++) {
                        if(tmp[i] == path[1]) {
                            nextMenu = nextEnabledChild(tmp,i+1);
                            if(nextMenu == null)
                                nextMenu = nextEnabledChild(tmp,0);
                        }
		    }
                }

                if(nextMenu != null) {
                    MenuElement newSelection[];
                    popup = ((JMenu)nextMenu).getPopupMenu();
                    if(((JMenu)nextMenu).isTopLevelMenu())
                        firstItem = null;
                    else {
                        tmp = popup.getSubElements();
                        if(tmp.length > 0)
                            firstItem = nextEnabledChild(tmp,0);
                    }

                    if(firstItem != null) {
                        newSelection = new MenuElement[4];
                        newSelection[0] = path[0];
                        newSelection[1] = nextMenu;
                        newSelection[2] = popup;
                        newSelection[3] = firstItem;
                    } else {
                        newSelection = new MenuElement[3];
                        newSelection[0] = path[0];
                        newSelection[1] = nextMenu;
                        newSelection[2] = popup;
                    }
                    MenuSelectionManager.defaultManager().setSelectedPath(newSelection);
                }
            }
        }
    }
}




