/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.border.*;


/**
 * A default L&F implementation of MenuUI.  This implementation 
 * is a "combined" view/controller.
 *
 * @version 1.133 02/06/02
 * @author Georges Saab
 * @author David Karlton
 * @author Arnaud Weber
 */
public class BasicMenuUI extends BasicMenuItemUI 
{
    protected ChangeListener         changeListener;
    protected PropertyChangeListener propertyChangeListener;
    protected MenuListener           menuListener;

    private int lastMnemonic = 0;

    /** Uses as the parent of the windowInputMap when selected. */
    private InputMap selectedWindowInputMap;

    /* diagnostic aids -- should be false for production builds. */
    private static final boolean TRACE =   false; // trace creates and disposes
    private static final boolean VERBOSE = false; // show reuse hits/misses
    private static final boolean DEBUG =   false;  // show bad params, misc.

    public static ComponentUI createUI(JComponent x) {
	return new BasicMenuUI();
    }

    protected void installDefaults() {
	super.installDefaults();
	((JMenu)menuItem).setDelay(200);
    }

    protected String getPropertyPrefix() {
	return "Menu";
    }

    protected void installListeners() {
	super.installListeners();

        changeListener = createChangeListener(menuItem);
        propertyChangeListener = createPropertyChangeListener(menuItem);
	menuListener = createMenuListener(menuItem);

        menuItem.addChangeListener(changeListener);
        menuItem.addPropertyChangeListener(propertyChangeListener);
	((JMenu)menuItem).addMenuListener(menuListener);
    }

    protected void installKeyboardActions() {
	super.installKeyboardActions();
	updateMnemonicBinding();
    }

    void updateMnemonicBinding() {
	int mnemonic = menuItem.getModel().getMnemonic();
	if (mnemonic == lastMnemonic) {
	    return;
	}
	if (lastMnemonic != 0 && windowInputMap != null) {
	    windowInputMap.remove(KeyStroke.getKeyStroke
				(lastMnemonic, ActionEvent.ALT_MASK, false));
	}
	if (mnemonic != 0) {
	    if (windowInputMap == null) {
		windowInputMap = createInputMap(JComponent.
					      WHEN_IN_FOCUSED_WINDOW);
		SwingUtilities.replaceUIInputMap(menuItem, JComponent.
				       WHEN_IN_FOCUSED_WINDOW, windowInputMap);
	    }
	    windowInputMap.put(KeyStroke.getKeyStroke(mnemonic,
						    ActionEvent.ALT_MASK,
						    false),
			     "selectMenu");
        }
	lastMnemonic = mnemonic;
    }

    protected void uninstallKeyboardActions() {
	super.uninstallKeyboardActions();
    }

    /**
     * The ActionMap for BasicMenUI can not be shared, this is subclassed
     * to create a new one for each invocation.
     */
    ActionMap getActionMap() {
	return createActionMap();
    }

    /**
     * Invoked to create the ActionMap.
     */
    ActionMap createActionMap() {
	ActionMap am = super.createActionMap();
	if (am != null) {
	    am.put("selectMenu", new PostAction((JMenu)menuItem, true));
	    // Get the ActionMap that can be shared.
	    ActionMap parent = (ActionMap)UIManager.
		               get("Menu.sharedActionMap");
	    if (parent == null) {
		parent = createSharedActionMap();
		if (parent != null) {
		    UIManager.put("MenuUI.sharedActionMap", parent);
		}
	    }
	    if (parent != null) {
		am.setParent(parent);
	    }
	}
	return am;
    }

    /**
     * Creates the ActionMap containing the actions that can be shared.
     */
    ActionMap createSharedActionMap() {
	ActionMap am = new ActionMapUIResource();
	am.put("cancel", new CancelAction());
	am.put("selectNext", new SelectNextItemAction());
	am.put("selectPrevious", new SelectPreviousItemAction());
	am.put("selectParent", new SelectParentItemAction());
	am.put("selectChild", new SelectChildItemAction());
	am.put("return", new ReturnAction());
	return am;
    }

    protected MouseInputListener createMouseInputListener(JComponent c) {
	return new MouseInputHandler();
    }

    protected MenuListener createMenuListener(JComponent c) {
	return new MenuHandler();
    }

    protected ChangeListener createChangeListener(JComponent c) {
        return new ChangeHandler((JMenu)c, this);
    }

    protected PropertyChangeListener createPropertyChangeListener(JComponent c) {
        return new PropertyChangeHandler();
    }

    protected void uninstallDefaults() {
	menuItem.setArmed(false);
	menuItem.setSelected(false);
	menuItem.resetKeyboardActions();
	super.uninstallDefaults();
    }

    protected void uninstallListeners() {
	super.uninstallListeners();
        menuItem.removeChangeListener(changeListener);
        menuItem.removePropertyChangeListener(propertyChangeListener);
        ((JMenu)menuItem).removeMenuListener(menuListener);

	changeListener = null;
	propertyChangeListener = null;
	menuListener = null;
    }

    protected MenuDragMouseListener createMenuDragMouseListener(JComponent c) {
	return new MenuDragMouseHandler();
    }
    
    protected MenuKeyListener createMenuKeyListener(JComponent c) {
	return new MenuKeyHandler();
    }

    public Dimension getMaximumSize(JComponent c) {
	if (((JMenu)menuItem).isTopLevelMenu() == true) {
	    Dimension d = c.getPreferredSize();
	    return new Dimension(d.width, Short.MAX_VALUE);
	}
        return null;
    }

    protected void setupPostTimer(JMenu menu) {
        Timer timer = new Timer(menu.getDelay(),new PostAction(menu,false));
        timer.setRepeats(false);
        timer.start();
    }

    private static class PostAction extends AbstractAction {
	JMenu menu;
        boolean force=false;

        PostAction(JMenu menu,boolean shouldForce) {
	    this.menu = menu;
            this.force = shouldForce;
	}
	
	public void actionPerformed(ActionEvent e) {
	    /* Must be commented out to work around compiler bug (4242000)
	       if (DEBUG) {
		System.out.println("In PostAction.actionPerformed");
		Thread.dumpStack();
	    } */
	    final MenuSelectionManager defaultManager = MenuSelectionManager.defaultManager();
            if(force) {
                Container cnt = menu.getParent();
                if(cnt != null && cnt instanceof JMenuBar) {
                    final MenuElement me[];
                    MenuElement subElements[];
		    
                    subElements = menu.getPopupMenu().getSubElements();
                    if(subElements.length > 0) {
                        me = new MenuElement[4];
                        me[0] = (MenuElement) cnt;
                        me[1] = (MenuElement) menu;
                        me[2] = (MenuElement) menu.getPopupMenu();
                        me[3] = subElements[0];
                    } else {
                        me = new MenuElement[3];
                        me[0] = (MenuElement)cnt;
                        me[1] = menu;
                        me[2] = (MenuElement) menu.getPopupMenu();
                    }
		    // Clear the path now so that no menu items
		    // of currently selected menus get a chance
		    // at this KeyEvent
		    defaultManager.clearSelectedPath();
                    defaultManager.setSelectedPath(me);
		}
            } else {
                MenuElement path[] = defaultManager.getSelectedPath();
                if(path.length > 0 && path[path.length-1] == menu) {
                    MenuElement newPath[] = new MenuElement[path.length+1];
                    System.arraycopy(path,0,newPath,0,path.length);
                    newPath[path.length] = menu.getPopupMenu();
                    MenuSelectionManager.defaultManager().setSelectedPath(newPath);
                }
            }
        }

	public boolean isEnabled() {
	    return menu.getModel().isEnabled();
	}
    }


    private class PropertyChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
	    String prop = e.getPropertyName();
	    if(prop.equals(AbstractButton.MNEMONIC_CHANGED_PROPERTY)) {
		updateMnemonicBinding();
	    }
	}
    }

    private class MouseInputHandler implements MouseInputListener {
	public void mouseClicked(MouseEvent e) {}
        public void mousePressed(MouseEvent e) {
	    JMenu menu = (JMenu)menuItem;
	    if (!menu.isEnabled())
		return;

	    MenuSelectionManager manager = 
		MenuSelectionManager.defaultManager();
            if(menu.isTopLevelMenu()) {
		if(menu.isSelected()) {
		    manager.clearSelectedPath();
		} else {
		    Container cnt = menu.getParent();
		    if(cnt != null && cnt instanceof JMenuBar) {
			MenuElement me[] = new MenuElement[2];
			me[0]=(MenuElement)cnt;
			me[1]=menu;
			manager.setSelectedPath(me);
		    }
		}
	    }

            MenuElement selectedPath[] = manager.getSelectedPath();
            if(!(selectedPath.length > 0 && 
		 selectedPath[selectedPath.length-1] == 
		 menu.getPopupMenu())) {
		if(menu.isTopLevelMenu() || 
		   menu.getDelay() == 0) {
		    MenuElement newPath[] = new MenuElement[selectedPath.length+1];
		    System.arraycopy(selectedPath,0,newPath,0,selectedPath.length);
		    newPath[selectedPath.length] = menu.getPopupMenu();
		    manager.setSelectedPath(newPath);
		} else {
		    setupPostTimer(menu);
		}
            }
        }

	public void mouseReleased(MouseEvent e) {
	    JMenu menu = (JMenu)menuItem;
	    if (!menu.isEnabled())
		return;
	    MenuSelectionManager manager = 
		MenuSelectionManager.defaultManager();
	    manager.processMouseEvent(e);
	    if (!e.isConsumed())
		manager.clearSelectedPath();		
	}
	public void mouseEntered(MouseEvent e) {
	    JMenu menu = (JMenu)menuItem;
	    if (!menu.isEnabled())
		return;

	    MenuSelectionManager manager = 
		MenuSelectionManager.defaultManager();
	    MenuElement selectedPath[] = manager.getSelectedPath();	    
	    if (!menu.isTopLevelMenu()) {
		if(!(selectedPath.length > 0 && 
		     selectedPath[selectedPath.length-1] == 
		     menu.getPopupMenu())) {
		    if(menu.getDelay() == 0) {
			MenuElement newPath[] = new MenuElement[selectedPath.length+2];
			System.arraycopy(selectedPath,0,newPath,0,selectedPath.length);
			newPath[selectedPath.length] = menuItem;
			newPath[selectedPath.length+1] = menu.getPopupMenu();
			manager.setSelectedPath(newPath);
		    } else {
			manager.setSelectedPath(getPath());
			setupPostTimer(menu);
		    }
		}
	    } else {
		if(selectedPath.length > 0 &&
		   selectedPath[0] == menu.getParent()) {
		    MenuElement newPath[] = new MenuElement[3];
		    // A top level menu's parent is by definition 
		    // a JMenuBar
		    newPath[0] = (MenuElement)menu.getParent();
		    newPath[1] = menu;
		    newPath[2] = menu.getPopupMenu();
		    manager.setSelectedPath(newPath);
		}
	    }
	}
	public void mouseExited(MouseEvent e) {
	}
	public void mouseDragged(MouseEvent e) {
	    JMenu menu = (JMenu)menuItem;
	    if (!menu.isEnabled())
		return;
	    MenuSelectionManager.defaultManager().processMouseEvent(e);
	}
	public void mouseMoved(MouseEvent e) {
	}
    }

    private static class MenuHandler implements MenuListener {
	public void menuSelected(MenuEvent e) {}
	public void menuDeselected(MenuEvent e) {}
	public void menuCanceled(MenuEvent e) {
	    JMenu m = (JMenu)e.getSource();
	    MenuSelectionManager manager = MenuSelectionManager.defaultManager();
	    if(manager.isComponentPartOfCurrentMenu(m))
		MenuSelectionManager.defaultManager().clearSelectedPath();
	}
	
    }

    public class ChangeHandler implements ChangeListener {
        public JMenu    menu;
	public BasicMenuUI ui;
        public boolean  isSelected = false;
        public Component wasFocused;

        public ChangeHandler(JMenu m, BasicMenuUI ui) {
            menu = m;
            this.ui = ui;
            validateKeyboardActions(menu.isSelected());
        }

        public void stateChanged(ChangeEvent e) {
            validateKeyboardActions(menu.isSelected());
        }

        private Component findFocusedComponent(Component c) {
            Container parent;
            for(parent = c.getParent() ; parent != null ; parent = parent.getParent()) {
                if(parent instanceof java.awt.Window)
                    return ((java.awt.Window)parent).getFocusOwner();
            }
            return null;
        }

        private void validateKeyboardActions(boolean sel) {
            if(sel != isSelected) {
                isSelected = sel;
                if(isSelected) {
                    boolean isRequestFocusEnabled = menu.isRequestFocusEnabled();
                    wasFocused = findFocusedComponent(menu);
		    if ((wasFocused instanceof JComponent) &&
			((JComponent)wasFocused).getRootPane() != menu.getRootPane()) {
		      wasFocused = null;
		    }
                    if(!isRequestFocusEnabled)
                        menu.setRequestFocusEnabled(true);
                    menu.requestFocus();
                    if(!isRequestFocusEnabled)
                        menu.setRequestFocusEnabled(false);
		    if (selectedWindowInputMap == null) {
			Object[] bindings = (Object[])UIManager.get
			          ("Menu.selectedWindowInputMapBindings");
			if (bindings != null) {
			    selectedWindowInputMap = LookAndFeel.
				makeComponentInputMap(menuItem, bindings);
			}
		    }
		    if (windowInputMap == null) {
			windowInputMap = createInputMap(JComponent.
						    WHEN_IN_FOCUSED_WINDOW);
			SwingUtilities.replaceUIInputMap(menuItem, JComponent.
						   WHEN_IN_FOCUSED_WINDOW,
						   windowInputMap);
		    }
		    if (windowInputMap != null && selectedWindowInputMap != null) {
			windowInputMap.setParent(selectedWindowInputMap);
		    }
                } else {
		    if (windowInputMap != null && selectedWindowInputMap != null) {
			windowInputMap.setParent(null);
		    }
                    if(wasFocused != null) {
                        if(wasFocused instanceof JComponent) {
                            JComponent jc = (JComponent) wasFocused;
                            boolean isRFEnabled = jc.isRequestFocusEnabled();
                            if(!isRFEnabled)
                                jc.setRequestFocusEnabled(true);
                            wasFocused.requestFocus();
                            if(!isRFEnabled)
                                jc.setRequestFocusEnabled(false);
                        } else
                            wasFocused.requestFocus();
                        wasFocused = null;
                    }
                }
            }
        }
    }

    private static class CancelAction extends AbstractAction {
	public void actionPerformed(ActionEvent e) {
	    /* Must be commented out to work around compiler bug (4242000)
	      if (DEBUG) {
		System.out.println("In CancelAction.actionPerformed");
		Thread.dumpStack();
	    }
	    */
	    JMenu menu = (JMenu)e.getSource();
	    if (!menu.isEnabled())
		return;
		
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
	    JMenu menu = (JMenu)e.getSource();
	    if (!menu.isEnabled())
		return;

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
	    /* Must be commented out to work around compiler bug (4242000)
	      if (DEBUG) {
		System.out.println("In SelectNextItemAction.actionPerformed");
	    }
	    */

	    JMenu menu = (JMenu)e.getSource();
	    if (!menu.isEnabled())
		return;

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
	    /* Must be commented out to work around compiler bug (4242000)
	      if (DEBUG) {
		System.out.println("In SelectPreviousItemAction.actionPerformed");
	    }
	    */
	    JMenu menu = (JMenu)e.getSource();
	    if (!menu.isEnabled())
		return;

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
	    JMenu menu = (JMenu)e.getSource();
	    if (!menu.isEnabled())
		return;

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
	    JMenu menu = (JMenu)e.getSource();
	    if (!menu.isEnabled())
		return;

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


    private class MenuDragMouseHandler implements MenuDragMouseListener {
	public void menuDragMouseEntered(MenuDragMouseEvent e) {}
	public void menuDragMouseDragged(MenuDragMouseEvent e) {
	    if (menuItem.isEnabled() == false)
		return;
	    
	    MenuSelectionManager manager = e.getMenuSelectionManager();
	    MenuElement path[] = e.getPath();
	    
	    Point p = e.getPoint();
	    if(p.x >= 0 && p.x < menuItem.getWidth() &&
	       p.y >= 0 && p.y < menuItem.getHeight()) {
		JMenu menu = (JMenu)menuItem;
		MenuElement selectedPath[] = manager.getSelectedPath();
		if(!(selectedPath.length > 0 && 
		     selectedPath[selectedPath.length-1] == 
		     menu.getPopupMenu())) {
		    if(menu.isTopLevelMenu() || 
		       menu.getDelay() == 0  ||
		       e.getID() == MouseEvent.MOUSE_DRAGGED) {
			MenuElement newPath[] = new MenuElement[path.length+1];
			System.arraycopy(path,0,newPath,0,path.length);
			newPath[path.length] = menu.getPopupMenu();
			manager.setSelectedPath(newPath);
		    } else {
			manager.setSelectedPath(path);
			setupPostTimer(menu);
		    }
		}
	    } else if(e.getID() == MouseEvent.MOUSE_RELEASED) {
		Component comp = manager.componentForPoint(e.getComponent(), e.getPoint());
		if (comp == null)
		    manager.clearSelectedPath();
	    }
	    
	}
	public void menuDragMouseExited(MenuDragMouseEvent e) {}
	public void menuDragMouseReleased(MenuDragMouseEvent e) {}	    
    }

    private class MenuKeyHandler implements MenuKeyListener {
	public void menuKeyTyped(MenuKeyEvent e) { 
        int key = menuItem.getMnemonic();
        if(key == 0)
            return;
        MenuElement path[] = e.getPath();
        if(lower(key) == lower((int)(e.getKeyChar()))) {
            JPopupMenu popupMenu = ((JMenu)menuItem).getPopupMenu();
            MenuElement sub[] = popupMenu.getSubElements();
            if(sub.length > 0) {
                MenuSelectionManager manager = e.getMenuSelectionManager();
                MenuElement newPath[] = new MenuElement[path.length + 2];
                System.arraycopy(path,0,newPath,0,path.length);
                newPath[path.length] = popupMenu;
                newPath[path.length+1] = sub[0];
                manager.setSelectedPath(newPath);
            }
            e.consume();
        }
	}
	public void menuKeyPressed(MenuKeyEvent e) {}
	public void menuKeyReleased(MenuKeyEvent e) {}

	private int lower(int ascii) {
	    if(ascii >= 'A' && ascii <= 'Z')
		return ascii + 'a' - 'A';
	    else
		return ascii;
	}

    }
}




