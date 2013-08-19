/*
 * @(#)SynthPopupMenuUI.java	1.14 03/04/10
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.border.*;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import java.util.*;

/**
 * @version 1.14, 04/10/03 (based on BasicPopupMenuUI v 1.96)
 * @author Georges Saab
 * @author David Karlton
 * @author Arnaud Weber
 */
class SynthPopupMenuUI extends PopupMenuUI implements PropertyChangeListener,
                                    SynthUI {
    /**
     * Maximum size of the text portion of the children menu items.
     */
    private int maxTextWidth;

    /**
     * Maximum size of the text for the acclerator portion of the children
     * menu items.
     */
    private int maxAcceleratorWidth;

    static boolean menuKeyboardHelperInstalled = false;
    static MenuKeyboardHelper menuKeyboardHelper = null;

    protected JPopupMenu popupMenu = null;
    private SynthStyle style;

    private static boolean checkInvokerEqual(MenuElement present, MenuElement last) {
        Component invokerPresent = present.getComponent();
        Component invokerLast = last.getComponent();

        if (invokerPresent instanceof JPopupMenu) {
            invokerPresent = ((JPopupMenu)invokerPresent).getInvoker();
        }
        if (invokerLast instanceof JPopupMenu) {
            invokerLast = ((JPopupMenu)invokerLast).getInvoker();
        }
        return (invokerPresent == invokerLast);
    }



    public static ComponentUI createUI(JComponent x) {
	return new SynthPopupMenuUI();
    }

    public void installUI(JComponent c) {
	popupMenu = (JPopupMenu) c;

	installDefaults();
        installListeners();
        installKeyboardActions();
    }

    public void installDefaults() {
	if (popupMenu.getLayout() == null ||
	    popupMenu.getLayout() instanceof UIResource) {
	    popupMenu.setLayout(new DefaultMenuLayout(
                                    popupMenu, BoxLayout.Y_AXIS));
        }
        fetchStyle(popupMenu);
    }

    private void fetchStyle(JComponent c) {
        SynthContext context = getContext(c, ENABLED);

        style = SynthLookAndFeel.updateStyle(context, this);
        context.dispose();
    }

    protected void installListeners() {
	if (mouseGrabber == null) {
	    mouseGrabber = new MouseGrabber();
	}
	if (basicPopupMenuListener == null) {
	    basicPopupMenuListener = createPopupMenuListener();
	}
 	popupMenu.addPopupMenuListener(basicPopupMenuListener);

        if (!menuKeyboardHelperInstalled) {
            if (menuKeyboardHelper == null) {
                menuKeyboardHelper = new MenuKeyboardHelper();
            }
            MenuSelectionManager msm = MenuSelectionManager.defaultManager();
            msm.addChangeListener(menuKeyboardHelper);
        }

        popupMenu.addPropertyChangeListener(this);
    }

    protected void installKeyboardActions() {
    }

    static InputMap getInputMap(JPopupMenu popup, JComponent c) {
        InputMap windowInputMap = null;
        PopupMenuUI ui = popup.getUI();
        SynthContext context = null;
        Object[] bindings = null;
        if (ui instanceof SynthPopupMenuUI) {
            context = ((SynthPopupMenuUI)ui).getContext(popup, ENABLED);
            bindings = (Object[])context.getStyle().get(context,
                                   "PopupMenu.selectedWindowInputMapBindings");
        }
        if (bindings == null) {
            bindings = new Object[] {
		  "ESCAPE", "cancel",
                    "DOWN", "selectNext",
		 "KP_DOWN", "selectNext",
		      "UP", "selectPrevious",
		   "KP_UP", "selectPrevious",
		    "LEFT", "selectParent",
		 "KP_LEFT", "selectParent",
		   "RIGHT", "selectChild",
		"KP_RIGHT", "selectChild",
		   "ENTER", "return",
		   "SPACE", "return"
	    };
        }
        windowInputMap = LookAndFeel.makeComponentInputMap(c, bindings);
        if (!popup.getComponentOrientation().isLeftToRight()) {
            Object[] km = null;
            if (context != null) {
                km = (Object[])context.getStyle().get(context,
                       "PopupMenu.selectedWindowInputMapBindings.RightToLeft");
            }
            if (km == null) {
                km = new Object[] {
                    "LEFT", "selectChild",
                    "KP_LEFT", "selectChild",
                    "RIGHT", "selectParent",
                    "KP_RIGHT", "selectParent",
                };
            }
            InputMap rightToLeftInputMap = LookAndFeel.
                                           makeComponentInputMap(c, km);
            rightToLeftInputMap.setParent(windowInputMap);
            windowInputMap = rightToLeftInputMap;
        }
        if (context != null) {
            context.dispose();
        }
        return windowInputMap;
    }

    static ActionMap getActionMap() {
        return LazyActionMap.getActionMap(SynthPopupMenuUI.class,
                                          "PopupMenu.actionMap");
    }
  
    public static void loadActionMap(ActionMap map) {
        // NOTE: this needs to remain static. If you have a need to
        // have Actions that reference the UI in the ActionMap,
        // then you'll also need to change the registeration of the
        // ActionMap.
        // PENDING: need support for actions.
/*
  	// Set the ActionMap's parent to the Auditory Feedback Action Map
  	BasicLookAndFeel lf = (SynthLookAndFeel)UIManager.getLookAndFeel();
	ActionMap audioMap = lf.getAudioActionMap();
	map.setParent(audioMap);
*/

	map.put("cancel", new CancelAction());
        map.put("selectNext",
                new SelectNextItemAction(SelectNextItemAction.FORWARD));
        map.put("selectPrevious",
                new SelectNextItemAction(SelectNextItemAction.BACKWARD));
	map.put("selectParent",
                new SelectParentChildAction(SelectParentChildAction.PARENT));
	map.put("selectChild",
                new SelectParentChildAction(SelectParentChildAction.CHILD));
	map.put("return", new ReturnAction());
    }

    public void uninstallUI(JComponent c) {
        uninstallDefaults();
        uninstallListeners();
        uninstallKeyboardActions();
	
	popupMenu = null;
    }
    
    protected void uninstallDefaults() {
        SynthContext context = getContext(popupMenu, ENABLED);

        style.uninstallDefaults(context);
        context.dispose();
        style = null;

        if (popupMenu.getLayout() instanceof UIResource) {
            popupMenu.setLayout(null);
        }
    }

    protected void uninstallListeners() {
	if (basicPopupMenuListener != null) {
	    popupMenu.removePopupMenuListener(basicPopupMenuListener);
	}
        popupMenu.removePropertyChangeListener(this);
    }

    protected void uninstallKeyboardActions() {
	SwingUtilities.replaceUIActionMap(popupMenu, null);
	SwingUtilities.replaceUIInputMap(popupMenu, 
				  JComponent.WHEN_IN_FOCUSED_WINDOW, null);
    }

    public SynthContext getContext(JComponent c) {
        return getContext(c, getComponentState(c));
    }

    private SynthContext getContext(JComponent c, int state) {
        return SynthContext.getContext(SynthContext.class, c,
                    SynthLookAndFeel.getRegion(c), style, state);
    }

    private Region getRegion(JComponent c) {
        return SynthLookAndFeel.getRegion(c);
    }

    private int getComponentState(JComponent c) {
        return SynthLookAndFeel.getComponentState(c);
    }

    /**
     * Resets the max text and accerator widths.
     */
    void resetAcceleratorWidths() {
        maxTextWidth = maxAcceleratorWidth = 0;
    }

    /**
     * Adjusts the width needed to display the maximum menu item string.
     *
     * @param width Text width.
     * @return max width
     */
    int adjustTextWidth(int width) {
        maxTextWidth = Math.max(maxTextWidth, width);
        return maxTextWidth;
    }

    /**
     * Adjusts the width needed to display the maximum accelerator.
     *
     * @param width Text width.
     * @return max width
     */
    int adjustAcceleratorWidth(int width) {
        maxAcceleratorWidth = Math.max(maxAcceleratorWidth, width);
        return maxAcceleratorWidth;
    }

    /**
     * Maximum size to display text of children menu items.
     */
    int getMaxTextWidth() {
        return maxTextWidth;
    }

    /**
     * Maximum size needed to display accelerators of children menu items.
     */
    int getMaxAcceleratorWidth() {
        return maxAcceleratorWidth;
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


    public void update(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        SynthLookAndFeel.update(context, g);
        paint(context, g);
        context.dispose();
    }

    public void paint(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        paint(context, g);
        context.dispose();
    }

    protected void paint(SynthContext context, Graphics g) {
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (SynthLookAndFeel.shouldUpdateStyle(e)) {
            fetchStyle(popupMenu);
        }
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

    
    private static MenuElement getFirstPopup() {
	MenuSelectionManager msm = MenuSelectionManager.defaultManager();
	MenuElement[] p = msm.getSelectedPath();
	MenuElement me = null;	    
	
	for(int i = 0 ; me == null && i < p.length ; i++) {
	    if (p[i] instanceof JPopupMenu)
		me = p[i];
	}
	
	return me;
    }

    private static JPopupMenu getLastPopup() {
	MenuSelectionManager msm = MenuSelectionManager.defaultManager();
	MenuElement[] p = msm.getSelectedPath();
	JPopupMenu popup = null;	    
	
	for(int i = p.length - 1; popup == null && i >= 0; i--) {
	    if (p[i] instanceof JPopupMenu)
		popup = (JPopupMenu)p[i];
	}
	return popup;
    }

    private static List getPopups() {
	MenuSelectionManager msm = MenuSelectionManager.defaultManager();
	MenuElement[] p = msm.getSelectedPath();
	
	List list = new ArrayList(p.length); 
	for(int i = 0; i < p.length; i++) {
	    if (p[i] instanceof JPopupMenu) {
		list.add((JPopupMenu)p[i]);
	    }
	}
	return list;
    }

    private transient static MouseGrabber mouseGrabber = null;

    private static class MouseGrabber
        implements MouseListener, MouseMotionListener, MouseWheelListener,
                   WindowListener, WindowFocusListener, ComponentListener, ChangeListener {
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

	    JPopupMenu firstPopup = (JPopupMenu)getFirstPopup();
	    if (lastGrabbed == firstPopup) {
		// 4234793: This action should call firePopupMenuCanceled but it's
		// a protected method. The real solution could be to make 
		// firePopupMenuCanceled public and call it directly.
		List popups = getPopups();
		Iterator iter = popups.iterator();
		while (iter.hasNext()) {
		    JPopupMenu popup = (JPopupMenu)iter.next();
		    popup.putClientProperty("JPopupMenu.firePopupMenuCanceled", Boolean.TRUE);
		}
		MenuSelectionManager.defaultManager().clearSelectedPath();
		ungrabContainers();
	    } else {
		// The cancel has cause another menu selection
		lastGrabbed = firstPopup;
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

            if (lastPathSelected.length != 0 && p.length != 0 ) {
                      if (!checkInvokerEqual(p[0],lastPathSelected[0])) {
                           requestRemoveGrab();
                           lastPathSelected = new MenuElement[0];
                     }

        	}

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

             Component ignore = null;
	    if(c instanceof java.awt.Window) {
		((java.awt.Window)c).addWindowListener(this);
		((java.awt.Window)c).addWindowFocusListener(this);                
		((java.awt.Window)c).addComponentListener(this);
		grabbed.addElement(c);
	    }
            else if (c instanceof JRootPane) {
                // Only add listeners to the glass pane if it listeners,
                // otherwise if the glass pane is visible no mouse move
                // events will come to us.
                ignore = ((JRootPane)c).getGlassPane();
                if (ignore.getMouseListeners().length > 0 ||
                           ignore.getMouseMotionListeners().length > 0 ||
                           ignore.getMouseWheelListeners().length > 0) {
                    ignore = null;
                }
            }
	    synchronized(c.getTreeLock()) {
		int ncomponents = c.getComponentCount();
		Component[] component = c.getComponents();
		for (int i = 0 ; i < ncomponents ; i++) {
		    Component comp = component[i];
		    if(!comp.isVisible())
			continue;
                    if (comp != ignore) {
                        comp.addMouseListener(this);
                        comp.addMouseMotionListener(this);
                        comp.addMouseWheelListener(this);
                        grabbed.addElement(comp);
                    }
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
                    ((java.awt.Window)cpn).removeWindowFocusListener(this);
                    ((java.awt.Window)cpn).removeComponentListener(this);
                } else {
                    cpn.removeMouseListener(this);
                    cpn.removeMouseMotionListener(this);
                    cpn.removeMouseWheelListener(this);
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
        public void mouseWheelMoved(MouseWheelEvent e) {
            cancelPopupMenu();
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
        }
        public void windowDeactivated(WindowEvent e) {
        }
        public void windowLostFocus(WindowEvent e) {
        }
        public void windowGainedFocus(WindowEvent e) {
        }
    }

    public boolean isPopupTrigger(MouseEvent e) {
	return ((e.getID()==MouseEvent.MOUSE_RELEASED) 
		&& ((e.getModifiers() & MouseEvent.BUTTON3_MASK)!=0));
    }	    

    // for auditory feedback
    private transient PopupMenuListener basicPopupMenuListener = null;

    // for auditory feedback
    private PopupMenuListener createPopupMenuListener() {
        return new BasicPopupMenuListener();
    }

    /**
     * This Listener fires the Action that provides the correct auditory
     * feedback.
     *
     * @since 1.4
     */
    private class BasicPopupMenuListener implements PopupMenuListener {
        public void popupMenuCanceled(PopupMenuEvent e) {
	}

        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
	}

        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
	    // Fire the Action that renders the auditory cue.
            SynthLookAndFeel.playSound((JComponent)e.getSource(),
                                       "PopupMenu.popupSound");
	}
    }

    private static class CancelAction extends AbstractAction {
	public void actionPerformed(ActionEvent e) {
		
	    // 4234793: This action should call JPopupMenu.firePopupMenuCanceled but it's
	    // a protected method. The real solution could be to make 
	    // firePopupMenuCanceled public and call it directly.
	    JPopupMenu lastPopup = (JPopupMenu)getLastPopup();
	    if (lastPopup != null) {
		lastPopup.putClientProperty("JPopupMenu.firePopupMenuCanceled", Boolean.TRUE);
	    }


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
            KeyboardFocusManager fmgr =
                KeyboardFocusManager.getCurrentKeyboardFocusManager();
            Component focusOwner = fmgr.getFocusOwner();
            if(focusOwner != null && !(focusOwner instanceof JRootPane)) {
                return;
            }

            MenuSelectionManager msm = MenuSelectionManager.defaultManager();
	    MenuElement path[] = msm.getSelectedPath();
	    MenuElement lastElement;
	    if(path.length > 0) {
		lastElement = path[path.length-1];
		if(lastElement instanceof JMenu) {
		    MenuElement newPath[] = new MenuElement[path.length+1];
		    System.arraycopy(path,0,newPath,0,path.length);
		    newPath[path.length] = ((JMenu)lastElement).getPopupMenu();
		    msm.setSelectedPath(newPath);
		} else if(lastElement instanceof JMenuItem) {
                    JMenuItem mi = (JMenuItem)lastElement;

                    if (mi.getUI() instanceof SynthMenuItemUI) {
                        ((SynthMenuItemUI)mi.getUI()).doClick(msm);
                    }
                    else {
                        msm.clearSelectedPath();
                        mi.doClick(0);
                    }
		}
	    }
	}
    }

    private static MenuElement nextEnabledChild(MenuElement e[],
                                                int fromIndex, int toIndex) {
	for (int i=fromIndex; i<=toIndex; i++) {
	    if (e[i] != null) {
		Component comp = e[i].getComponent();
		if (comp != null && comp.isEnabled()) return e[i];
	    }
	}
	return null;
    }

    private static MenuElement previousEnabledChild(MenuElement e[],
                                                int fromIndex, int toIndex) {
	for (int i=fromIndex; i>=toIndex; i--) {
	    if (e[i] != null) {
		Component comp = e[i].getComponent();
		if (comp != null && comp.isEnabled()) return e[i];
	    }
	}
	return null;
    }

    private static MenuElement findEnabledChild(MenuElement e[], int fromIndex,
                                                boolean forward) {
        MenuElement result = null;
        if (forward) {
            result = nextEnabledChild(e, fromIndex+1, e.length-1);
            if (result == null) result = nextEnabledChild(e, 0, fromIndex-1);
        } else {
            result = previousEnabledChild(e, fromIndex-1, 0);
            if (result == null) result = previousEnabledChild(e, e.length-1,
                                                              fromIndex+1);
        }
	return result;
    }

    private static MenuElement findEnabledChild(MenuElement e[],
                                   MenuElement elem, boolean forward) {
        for (int i=0; i<e.length; i++) {
            if (e[i] == elem) {
                return findEnabledChild(e, i, forward);
            }
        }
        return null;
    }

    private static class SelectNextItemAction extends AbstractAction {
        static boolean FORWARD = true;
        static boolean BACKWARD = false;
        boolean direction;

        SelectNextItemAction(boolean dir) {
            direction = dir;
        }

	public void actionPerformed(ActionEvent e) {
            MenuSelectionManager msm = MenuSelectionManager.defaultManager();
            MenuElement path[] = msm.getSelectedPath();
            if (path.length < 2) {
                return;
            }
            int len = path.length;

            if (path[0] instanceof JMenuBar &&
                path[1] instanceof JMenu && len == 2) {

                // a toplevel menu is selected, but its popup not shown.
                // Show the popup and select the first item
                JPopupMenu popup = ((JMenu)path[1]).getPopupMenu();
                MenuElement next =
                    findEnabledChild(popup.getSubElements(), -1, FORWARD);
                MenuElement[] newPath;

                if (next != null) {
                    // an enabled item found -- include it in newPath
                    newPath = new MenuElement[4];
                    newPath[3] = next;
                } else {
                    // menu has no enabled items -- still must show the popup
                    newPath = new MenuElement[3];
                }
                System.arraycopy(path, 0, newPath, 0, 2);
                newPath[2] = popup;
                msm.setSelectedPath(newPath);

            } else if (path[len-1] instanceof JPopupMenu &&
                       path[len-2] instanceof JMenu) {

                // a menu (not necessarily toplevel) is open and its popup
                // shown. Select the appropriate menu item
                JMenu menu = (JMenu)path[len-2];
                JPopupMenu popup = menu.getPopupMenu();
                MenuElement next =
                    findEnabledChild(popup.getSubElements(), -1, direction);

                if (next != null) {
                    MenuElement[] newPath = new MenuElement[len+1];
                    System.arraycopy(path, 0, newPath, 0, len);
                    newPath[len] = next;
                    msm.setSelectedPath(newPath);
                } else {
                    // all items in the popup are disabled.
                    // We're going to find the parent popup menu and select
                    // its next item. If there's no parent popup menu (i.e.
                    // current menu is toplevel), do nothing
                    if (len > 2 && path[len-3] instanceof JPopupMenu) {
                        popup = ((JPopupMenu)path[len-3]);
                        next = findEnabledChild(popup.getSubElements(),
                                                menu, direction);

                        if (next != null && next != menu) {
                            MenuElement[] newPath = new MenuElement[len-1];
                            System.arraycopy(path, 0, newPath, 0, len-2);
                            newPath[len-2] = next;
                            msm.setSelectedPath(newPath);
                        }
                    }
                }

            } else {
                // just select the next item, no path expansion needed
                MenuElement subs[] = path[len-2].getSubElements();
                MenuElement nextChild =
                    findEnabledChild(subs, path[len-1], direction);
                if (nextChild == null) {
                    nextChild = findEnabledChild(subs, -1, direction);
                }
                if (nextChild != null) {
                    path[len-1] = nextChild;
                    msm.setSelectedPath(path);
		}
	    }
	}
    }

    private static class SelectParentChildAction extends AbstractAction {
        static boolean PARENT = false;
        static boolean CHILD = true;
        boolean direction;

        SelectParentChildAction(boolean dir) {
            direction = dir;
        }

	public void actionPerformed(ActionEvent e) {
            MenuSelectionManager msm = MenuSelectionManager.defaultManager();
            MenuElement path[] = msm.getSelectedPath();
            int len = path.length;

            if (direction == PARENT) {
                // selecting parent
                int popupIndex = len-1;

                if (len > 2 &&
                    // check if we have an open submenu. A submenu item may or
                    // may not be selected, so submenu popup can be either the
                    // last or next to the last item.
                    (path[popupIndex] instanceof JPopupMenu ||
                     path[--popupIndex] instanceof JPopupMenu) &&
                    !((JMenu)path[popupIndex-1]).isTopLevelMenu()) {

                    // we have a submenu, just close it
                    MenuElement newPath[] = new MenuElement[popupIndex];
                    System.arraycopy(path, 0, newPath, 0, popupIndex);
                    msm.setSelectedPath(newPath);
                    return;
                }
            } else {
                // selecting child
                if (len > 0 && path[len-1] instanceof JMenu &&
                    !((JMenu)path[len-1]).isTopLevelMenu()) {

                    // we have a submenu, open it
                    JMenu menu = (JMenu)path[len-1];
                    JPopupMenu popup = menu.getPopupMenu();
                    MenuElement[] subs = popup.getSubElements();
                    MenuElement item = findEnabledChild(subs, -1, true);
                    MenuElement[] newPath;

                    if (item == null) {
                        newPath = new MenuElement[len+1];
                    } else {
                        newPath = new MenuElement[len+2];
                        newPath[len+1] = item;
                    }
                    System.arraycopy(path, 0, newPath, 0, len);
                    newPath[len] = popup;
                    msm.setSelectedPath(newPath);
                    return;
                }
            }

            // check if we have a toplevel menu selected.
            // If this is the case, we select another toplevel menu
	    if (len > 1 && path[0] instanceof JMenuBar) {
                MenuElement currentMenu = path[1];
		MenuElement nextMenu = findEnabledChild(
                    path[0].getSubElements(), currentMenu, direction);

		if (nextMenu != null && nextMenu != currentMenu) {
		    MenuElement newSelection[];
		    if (len == 2) {
                        // menu is selected but its popup not shown
			newSelection = new MenuElement[2];
			newSelection[0] = path[0];
			newSelection[1] = nextMenu;
		    } else {
                        // menu is selected and its popup is shown
			newSelection = new MenuElement[3];
			newSelection[0] = path[0];
			newSelection[1] = nextMenu;
			newSelection[2] = ((JMenu)nextMenu).getPopupMenu();
                    }
		    msm.setSelectedPath(newSelection);
		}
	    }
	}
    }

    /**
     * This helper is added to MenuSelectionManager as a ChangeListener to 
     * listen to menu selection changes. When a menu is activated, it passes
     * focus to its parent JRootPane, and installs an ActionMap/InputMap pair
     * on that JRootPane. Those maps are necessary in order for menu
     * navigation to work. When menu is being deactivated, it restores focus
     * to the component that has had it before menu activation, and uninstalls
     * the maps.
     * This helper is also installed as a KeyListener on root pane when menu
     * is active. It forwards key events to MenuSelectionManager for mnemonic
     * keys handling.
     */
    private static class MenuKeyboardHelper
        implements ChangeListener, KeyListener {

        private Component lastFocused = null;
  	private MenuElement[] lastPathSelected = new MenuElement[0];
        private JPopupMenu lastPopup;

        private JRootPane invokerRootPane;
        private ActionMap menuActionMap = getActionMap();
        private InputMap menuInputMap;
        private boolean focusTraversalKeysEnabled;

        /*
         * Fix for 4213634
         * If this is false, KEY_TYPED and KEY_RELEASED events are NOT
         * processed. This is needed to avoid activating a menuitem when
         * the menu and menuitem share the same mnemonic.
         */
        private boolean receivedKeyPressed = false;


        void removeItems() {
            if (lastFocused != null) {
                if(!lastFocused.requestFocusInWindow()) {
                    // If lastFocused is not in currently focused window
                    // requestFocusInWindow will fail. In this case we must
                    // request focus by requestFocus() if it was not
                    // transferred from our popup
                    Window cfw = KeyboardFocusManager
                                 .getCurrentKeyboardFocusManager()
                                  .getFocusedWindow();
                    if(cfw != null &&
                       "###focusableSwingPopup###".equals(cfw.getName())) {
                        lastFocused.requestFocus();
                    }

                }
                lastFocused = null;
            }
            if (invokerRootPane != null) {
                invokerRootPane.removeKeyListener(menuKeyboardHelper);
                invokerRootPane.setFocusTraversalKeysEnabled(focusTraversalKeysEnabled);
                removeUIInputMap(invokerRootPane, menuInputMap);
                removeUIActionMap(invokerRootPane, menuActionMap);
                invokerRootPane = null;
            } 
            receivedKeyPressed = false;
        }

        private FocusListener rootPaneFocusListener = new FocusAdapter() {
                public void focusGained(FocusEvent ev) {
                    Component opposite = ev.getOppositeComponent();
                    if (opposite != null) {
                        lastFocused = opposite;
                    }
                    ev.getComponent().removeFocusListener(this);
                }
            };

        /**
         * Return the last JPopupMenu in <code>path</code>,
         * or <code>null</code> if none found
         */
        JPopupMenu getActivePopup(MenuElement[] path) {
            for (int i=path.length-1; i>=0; i--) {
                MenuElement elem = path[i];
                if (elem instanceof JPopupMenu) {
                    return (JPopupMenu)elem;
                }
            }
            return null;
        }

        void addUIInputMap(JComponent c, InputMap map) {
            InputMap lastNonUI = null;
            InputMap parent = c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

            while (parent != null && !(parent instanceof UIResource)) {
                lastNonUI = parent;
                parent = parent.getParent();
            }

            if (lastNonUI == null) {
                c.setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, map);
            } else {
                lastNonUI.setParent(map);
            }
            map.setParent(parent);
        }

        void addUIActionMap(JComponent c, ActionMap map) {
            ActionMap lastNonUI = null;
            ActionMap parent = c.getActionMap();

            while (parent != null && !(parent instanceof UIResource)) {
                lastNonUI = parent;
                parent = parent.getParent();
            }

            if (lastNonUI == null) {
                c.setActionMap(map);
            } else {
                lastNonUI.setParent(map);
            }
            map.setParent(parent);
        }

        void removeUIInputMap(JComponent c, InputMap map) {
            InputMap im = null;
            InputMap parent = c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

            while (parent != null) {
                if (parent == map) {
                    if (im == null) {
                        c.setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW,
                                      map.getParent());
                    } else {
                        im.setParent(map.getParent());
                    }
                    break;
                }
                im = parent;
                parent = parent.getParent();
            }
        }

        void removeUIActionMap(JComponent c, ActionMap map) {
            ActionMap im = null;
            ActionMap parent = c.getActionMap();

            while (parent != null) {
                if (parent == map) {
                    if (im == null) {
                        c.setActionMap(map.getParent());
                    } else {
                        im.setParent(map.getParent());
                    }
                    break;
                }
                im = parent;
                parent = parent.getParent();
            }
        }

        public void stateChanged(ChangeEvent ev) {
            if (!(UIManager.getLookAndFeel() instanceof SynthLookAndFeel)) {
                MenuSelectionManager msm = MenuSelectionManager.
                                           defaultManager();
                msm.removeChangeListener(this);
                menuKeyboardHelperInstalled = false;
                return;
            }
	    MenuSelectionManager msm = (MenuSelectionManager)ev.getSource();
	    MenuElement[] p = msm.getSelectedPath();
            JPopupMenu popup = getActivePopup(p);
            if (popup != null && !popup.isFocusable()) {
                // Do nothing for non-focusable popups
                return;
            }

            if   (lastPathSelected.length != 0 && p.length != 0 ) {
                if (!checkInvokerEqual(p[0],lastPathSelected[0])) {
                        removeItems();
                        lastPathSelected = new MenuElement[0];
                        }

        	}




	    if (lastPathSelected.length == 0 && p.length > 0) {
                // menu posted
                JComponent invoker;

                if (popup == null) {
                    if (p.length == 2 && p[0] instanceof JMenuBar &&
                        p[1] instanceof JMenu) {
                        // a menu has been selected but not open
                        invoker = (JComponent)p[1];
                        popup = ((JMenu)invoker).getPopupMenu();
                    } else {
                        return;
                    }
                } else {
                    Component c = popup.getInvoker();
                    if(c instanceof JFrame) {
                        invoker = ((JFrame)c).getRootPane();
                    } else if(c instanceof JApplet) {
                        invoker = ((JApplet)c).getRootPane();
                    } else {
                        while (!(c instanceof JComponent)) {
                            if (c == null) {
                                return;
                            }
                            c = c.getParent();
                        }
                        invoker = (JComponent)c;
                    }
                }

                // remember current focus owner
                lastFocused = KeyboardFocusManager.
                    getCurrentKeyboardFocusManager().getFocusOwner();

                // request focus on root pane and install keybindings
                // used for menu navigation
                invokerRootPane = SwingUtilities.getRootPane(invoker);
                if (invokerRootPane != null) {
                    invokerRootPane.addFocusListener(rootPaneFocusListener);
                    invokerRootPane.requestFocus(true);
                    invokerRootPane.addKeyListener(menuKeyboardHelper);
                    focusTraversalKeysEnabled = invokerRootPane.
                                      getFocusTraversalKeysEnabled();
                    invokerRootPane.setFocusTraversalKeysEnabled(false);

                    menuInputMap = getInputMap(popup, invokerRootPane);
                    addUIInputMap(invokerRootPane, menuInputMap);
                    addUIActionMap(invokerRootPane, menuActionMap);
                }
            } else if (lastPathSelected.length != 0 && p.length == 0) {
		// menu hidden -- return focus to where it had been before
                // and uninstall menu keybindings
                   removeItems();
	    } else {
                if (popup != lastPopup) {
                    receivedKeyPressed = false;
                }
            }

            // Remember the last path selected
            lastPathSelected = p;
            lastPopup = popup;
        }

        public void keyPressed(KeyEvent ev) {
            receivedKeyPressed = true;
            MenuSelectionManager.defaultManager().processKeyEvent(ev);
        }

        public void keyReleased(KeyEvent ev) {
	    if (receivedKeyPressed) {
		receivedKeyPressed = false;
                MenuSelectionManager.defaultManager().processKeyEvent(ev);
            }
        }

        public void keyTyped(KeyEvent ev) {
	    if (receivedKeyPressed) {
                MenuSelectionManager.defaultManager().processKeyEvent(ev);
            }
        }
    }
}
