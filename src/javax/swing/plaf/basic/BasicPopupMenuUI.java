/*
 * @(#)BasicPopupMenuUI.java	1.87 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.border.*;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import java.util.*;

/**
 * A Windows L&F implementation of PopupMenuUI.  This implementation 
 * is a "combined" view/controller.
 *
 * @version 1.87 12/03/01
 * @author Georges Saab
 * @author David Karlton
 * @author Arnaud Weber
 */
public class BasicPopupMenuUI extends PopupMenuUI {
    protected JPopupMenu popupMenu = null;
    static MenuKeyboardHelper menuKeyboardHelper = null;

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

	popupMenu.setFocusable(false);
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
	if (basicPopupMenuListener == null) {
	    basicPopupMenuListener = createPopupMenuListener();
	}
 	popupMenu.addPopupMenuListener(basicPopupMenuListener);

        if (menuKeyboardHelper == null) {
            menuKeyboardHelper = new MenuKeyboardHelper();
            MenuSelectionManager msm = MenuSelectionManager.defaultManager();
            msm.addChangeListener(menuKeyboardHelper);
        }
    }

    protected void installKeyboardActions() {
    }

    static InputMap getInputMap(JPopupMenu popup, JComponent c) {
        InputMap windowInputMap = null;
	Object[] bindings = (Object[])UIManager.get("PopupMenu.selectedWindowInputMapBindings");
	if (bindings != null) {
	    windowInputMap = LookAndFeel.makeComponentInputMap(c, bindings);
	    if (!popup.getComponentOrientation().isLeftToRight()) {
		Object[] km = (Object[])UIManager.get("PopupMenu.selectedWindowInputMapBindings.RightToLeft");
		if (km != null) {
		    InputMap rightToLeftInputMap = LookAndFeel.makeComponentInputMap(c, km);
		    rightToLeftInputMap.setParent(windowInputMap);
		    windowInputMap = rightToLeftInputMap;
		}
	    }
        }
        return windowInputMap;
    }

    static ActionMap getActionMap() {
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
  
    static ActionMap createActionMap() {
  	ActionMap map = new ActionMapUIResource();
  	// Set the ActionMap's parent to the Auditory Feedback Action Map
  	BasicLookAndFeel lf = (BasicLookAndFeel)UIManager.getLookAndFeel();
	ActionMap audioMap = lf.getAudioActionMap();
	map.setParent(audioMap);

	map.put("cancel", new CancelAction());
      map.put("selectNext", new SelectNextItemAction(
                                      SelectNextItemAction.FORWARD));
      map.put("selectPrevious", new SelectNextItemAction(
                                      SelectNextItemAction.BACKWARD));
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
	if (basicPopupMenuListener != null) {
	    popupMenu.removePopupMenuListener(basicPopupMenuListener);
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

    private static class MouseGrabber
        implements MouseListener, MouseMotionListener, MouseWheelListener,
                   WindowListener,ComponentListener, ChangeListener {
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
		    comp.addMouseWheelListener(this);
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
	    JPopupMenu pm = (JPopupMenu)e.getSource();
	    ActionMap map = pm.getActionMap();
	    if (map != null) {
		Action audioAction = map.get("PopupMenu.popupSound");
		if (audioAction != null) {
		    // pass off firing the Action to a utility method
		    BasicLookAndFeel lf = (BasicLookAndFeel)
			                   UIManager.getLookAndFeel();
		    lf.playSound(audioAction);
		}
	    }
	}
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

                    if (mi.getUI() instanceof BasicMenuItemUI) {
                        ((BasicMenuItemUI)mi.getUI()).doClick(msm);
                    }
                    else {
                        msm.clearSelectedPath();
                        mi.doClick(0);
                    }
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
        static int FORWARD = 1;
        static int BACKWARD = 2;
        int direction;

        SelectNextItemAction(int dir) {
            this.direction = dir;
        }

	public void actionPerformed(ActionEvent e) {
          MenuElement currentSelection[] =
                MenuSelectionManager.defaultManager().getSelectedPath();
	    if(currentSelection.length > 1) {
		MenuElement parent = currentSelection[currentSelection.length-2];
		if(parent.getComponent() instanceof JMenu) {
		    MenuElement childs[];
		    parent = currentSelection[currentSelection.length-1];
		    childs = parent.getSubElements();
		    if(childs.length > 0) {
                        MenuElement next = (direction == FORWARD?
                            nextEnabledChild(childs, 0):
                            previousEnabledChild(childs, childs.length-1));
                        if (next != null) {
                            MenuElement newPath[] = new MenuElement[currentSelection.length+1];
                            System.arraycopy(currentSelection, 0, newPath, 0,
                                             currentSelection.length);
                            newPath[currentSelection.length] = next;
			    MenuSelectionManager.defaultManager().setSelectedPath(newPath);
                      }
		    }
		} else {
		    MenuElement childs[] = parent.getSubElements();
                  MenuElement nextChild = null;
                  for(int i=0; i<childs.length; i++) {
			if(childs[i] == currentSelection[currentSelection.length-1]) {
                          nextChild = (direction == FORWARD?
                                nextEnabledChild(childs, i+1):
                                previousEnabledChild(childs, i-1));
			    break;
			}
		    }
                    if (nextChild == null) {
                        nextChild = (direction == FORWARD?
                            nextEnabledChild(childs,0):
                            previousEnabledChild(childs,childs.length-1));
                    }
                    if (nextChild != null) {
                        currentSelection[currentSelection.length-1] = nextChild;
                        MenuSelectionManager.defaultManager().setSelectedPath(currentSelection);
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
	    MenuSelectionManager msm = (MenuSelectionManager)ev.getSource();
	    MenuElement[] p = msm.getSelectedPath();
            JPopupMenu popup = getActivePopup(p);
            if (popup != null && popup instanceof BasicComboPopup) {
                // Combo popups don't transfer focus anywhere, so we do
                // nothing special.
                return;
            }

	    if (lastPathSelected.length == 0 && p.length != 0) {
                // menu posted
                if (popup == null) return;

                Component c = popup.getInvoker();
                while (!(c instanceof JComponent)) {
                    if (c == null) {
                        return;
                    }
                    c = c.getParent();
                }
                JComponent invoker = (JComponent)c;

                // remember current focus owner
                lastFocused = KeyboardFocusManager.
                    getCurrentKeyboardFocusManager().getFocusOwner();

                // request focus on root pane and install keybindings
                // used for menu navigation
                invokerRootPane = SwingUtilities.getRootPane(invoker);
                if (invokerRootPane != null) {
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
                if (lastFocused != null) {
                    lastFocused.requestFocus();
                }
                if (invokerRootPane != null) {
                    invokerRootPane.removeKeyListener(menuKeyboardHelper);
                    invokerRootPane.setFocusTraversalKeysEnabled(
                                       focusTraversalKeysEnabled);
                    removeUIInputMap(invokerRootPane, menuInputMap);
                    removeUIActionMap(invokerRootPane, menuActionMap);
                }

                receivedKeyPressed = false;
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
